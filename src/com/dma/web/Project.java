package com.dma.web;

import java.util.ArrayList;
import java.util.List;

public class Project {

	String name = "";
	List<String> languages = new ArrayList<String>();
	String dbSchema = "";
	Resource resource = null;
	String description = "";
	String timestamp = "";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getLanguages() {
		return languages;
	}
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	public String getDbSchema() {
		return dbSchema;
	}
	public void setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
	}
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
}
