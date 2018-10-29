package com.dma.web;

public class Resource {

	String jndiName = "";
	String dbName = "";
	String dbEngine = "";
	String description = "";
	String cognosCatalog = "";
	String cognosSchema = "";
	String cognosDataSource = "";

	public String getJndiName() {
		return jndiName;
	}
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getDbEngine() {
		return dbEngine;
	}
	public void setDbEngine(String dbEngine) {
		this.dbEngine = dbEngine;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCognosCatalog() {
		return cognosCatalog;
	}
	public void setCognosCatalog(String cognosCatalog) {
		this.cognosCatalog = cognosCatalog;
	}
	public String getCognosSchema() {
		return cognosSchema;
	}
	public void setCognosSchema(String cognosSchema) {
		this.cognosSchema = cognosSchema;
	}
	public String getCognosDataSource() {
		return cognosDataSource;
	}
	public void setCognosDataSource(String cognosDataSource) {
		this.cognosDataSource = cognosDataSource;
	}
	
}
