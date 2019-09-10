package com.tabulaex.graphdblp;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "keyword")
public class Keyword extends AutocompleteEntity{

}
