package com.tabulaex.graphdblp;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "venue")
public class Venue extends AutocompleteEntity{

}
