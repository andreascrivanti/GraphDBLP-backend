package com.tabulaex.graphdblp;

import org.springframework.data.mongodb.core.index.TextIndexed;

public abstract class AutocompleteEntity {
	@TextIndexed
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		return name.equals(((AutocompleteEntity)obj).getName());
	}
}
