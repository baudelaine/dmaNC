package com.dma.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Field {

	String _id = "";
	String _ref = null;
	String field_name = "";
	String field_type = "";
	boolean pk = false;
	boolean index = false;
	String label = "";
	int field_size = 0;
	String nullable = "NO";
	boolean traduction = false;
	boolean hidden = false;
	boolean timezone = false;
	String icon = "Attribute";
	String displayType = "Value";
	String description = "";
	String expression = "";
	Map<String, String> labels = new HashMap<String, String>();
	Map<String, String> descriptions = new HashMap<String, String>();
	String dimension = "";
	List<Map<String, String>> dimensions = new ArrayList<Map<String, String>>();
	String measure = "";
	String order = "";
	boolean custom = false;
	String BK = "";
	String hierarchyName = "";
	boolean timeDimension = false;
	
	public String getBK() {
		return BK;
	}
	public void setBK(String bK) {
		BK = bK;
	}
	public boolean isTimeDimension() {
		return timeDimension;
	}
	public void setTimeDimension(boolean timeDimension) {
		this.timeDimension = timeDimension;
	}
	public List<Map<String, String>> getDimensions() {
		return dimensions;
	}
	public void setDimensions(List<Map<String, String>> dimensions) {
		this.dimensions = dimensions;
	}
	public String getHierarchyName() {
		return hierarchyName;
	}
	public void setHierarchyName(String hierarchyName) {
		this.hierarchyName = hierarchyName;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public Map<String, String> getLabels() {
		return labels;
	}
	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}
	public Map<String, String> getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(Map<String, String> descriptions) {
		this.descriptions = descriptions;
	}
	public String get_ref() {
		return _ref;
	}
	public void set_ref(String _ref) {
		this._ref = _ref;
	}
	public String getField_name() {
		return field_name;
	}
	public void setField_name(String field_name) {
		this.field_name = field_name;
	}
	public String getField_type() {
		return field_type;
	}
	public void setField_type(String field_type) {
		this.field_type = field_type;
	}
	public boolean isPk() {
		return pk;
	}
	public void setPk(boolean pk) {
		this.pk = pk;
	}
	public boolean isIndex() {
		return index;
	}
	public void setIndex(boolean index) {
		this.index = index;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getField_size() {
		return field_size;
	}
	public void setField_size(int field_size) {
		this.field_size = field_size;
	}
	public String getNullable() {
		return nullable;
	}
	public void setNullable(String nullable) {
		this.nullable = nullable;
	}
	public boolean isTraduction() {
		return traduction;
	}
	public void setTraduction(boolean traduction) {
		this.traduction = traduction;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public boolean isTimezone() {
		return timezone;
	}
	public void setTimezone(boolean timezone) {
		this.timezone = timezone;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getDisplayType() {
		return displayType;
	}
	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDimension() {
		return dimension;
	}
	public void setDimension(String dimension) {
		this.dimension = dimension ;
	}
	public String getMeasure() {
		return measure;
	}
	public void setMeasure(String measure) {
		this.measure = measure;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public boolean isCustom() {
		return custom;
	}
	public void setCustom(boolean custom) {
		this.custom = custom;
	}

}
