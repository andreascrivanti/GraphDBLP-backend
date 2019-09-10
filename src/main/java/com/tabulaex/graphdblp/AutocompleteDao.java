package com.tabulaex.graphdblp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

@Repository
public class AutocompleteDao {
	@Autowired
	private MongoTemplate mongo;
	@Autowired
	private AppProperties props;
	
	@PostConstruct
	public void init() throws IOException{
		File fDir = new File(props.getDataDir());
		if(fDir.isDirectory()) {
			//drop all
			mongo.getCollectionNames().forEach(x -> mongo.dropCollection(x));
			
			File aFile = new File(fDir, "authors.txt");
			File kFile = new File(fDir, "keywords.txt");
			File vFile = new File(fDir, "venues.txt");
			try {
				loadAutocompleteFile(kFile, Keyword.class);
				loadAutocompleteFile(aFile, Author.class);
				loadAutocompleteFile(vFile, Venue.class);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Data dir '" + fDir.getPath() + "' not found; database will be empty!");
		}
	}
	
	private void loadAutocompleteFile(File file, Class<? extends AutocompleteEntity> entity) throws Exception{
		if(file.isFile()) {
			Scanner s = new Scanner(file);
			s.useDelimiter("\n");
			while(s.hasNext()) {
				Constructor<? extends AutocompleteEntity> constr = entity.getConstructor();
				AutocompleteEntity instance = constr.newInstance();
				String val = s.next();
				val = Strings.nullToEmpty(val);
				if(entity.equals(Author.class) && val.length() > 9) {
					val = val.substring(9);
				}
				instance.setName(val.trim());
				mongo.save(instance);
			}
			s.close();
		} else {
			System.out.println("File '" + file.getPath() + "' not found. Skipped.");
		}
	}
	
	public <T extends AutocompleteEntity> List<T> searchAutocompleteEntity(String text, Class<T> entity) throws Exception{
		//first starts with, then contains
		List<T> res = new ArrayList<>();
		//fare prime 5 starts with per ogni livello (per length più corte), trimmare a 5
		//fare prime 5 contains per ogni livello (per length più corte), trimmare a 5
		List<T> resStart = new ArrayList<>();
		List<T> resContains = new ArrayList<>();
		List<Bson> filters = new ArrayList<>();
		//filters.add(Filters.eq("type", "skill"));
		for(int m=0; m<2; m++) {
			List<Bson> pipeline = new ArrayList<>();
			if(m == 0) {
				List<Bson> tmp = new ArrayList<>(filters);
				tmp.add(Filters.regex("name", "^" + text + ".*", "i"));
				pipeline.add(Aggregates.match(Filters.and(tmp)));
			} else {
				List<Bson> tmp = new ArrayList<>(filters);
				tmp.add(Filters.regex("name", ".*" + text + ".*", "i"));
				pipeline.add(Aggregates.match(Filters.and(tmp)));
			}
			pipeline.add(Aggregates.project(Projections.fields(Projections.include("name"), new Document("field_length", new Document("$strLenCP", "$name")) )));
			//pipeline.add(Aggregates.group("$_id", new BsonField("name", new Document("$first", "$name")), new BsonField("field_length", new Document("$first", "$field_length"))));
			pipeline.add(Aggregates.sort(new Document("field_length", -1)));
			pipeline.add(Aggregates.limit(10));
			AggregateIterable<Document> docs = mongo.getCollection(mongo.getCollectionName(entity)).aggregate(pipeline);
			MongoCursor<Document> cursor = docs.iterator();
	        try {
	            while(cursor.hasNext()) {
	            	Document doc = cursor.next();
	            	Constructor<T> constr = entity.getConstructor();
	                T sl = constr.newInstance();
	                sl.setName(doc.getString("name"));
	                if(m == 0) {
	                	resStart.add(sl);
	                } else {
	                	resContains.add(sl);
	                }
	            }
	        } finally {
	            cursor.close();
	        }		
		}
		resStart.sort(new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return Integer.compare(o1.getName().length(), o2.getName().length());
			}
		});
		
		resContains.sort(new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return Integer.compare(o1.getName().length(), o2.getName().length());
			}
		});
		if(resStart.size() > 5) {
			res.addAll(resStart.subList(0, 5));
		} else {
			res.addAll(resStart);
		}
		for(int i=0; i<resContains.size() && res.size() < 10; i++) {
			if(!res.contains(resContains.get(i))) {
				res.add(resContains.get(i));
			}
		}
		
        return res;
	}
}
