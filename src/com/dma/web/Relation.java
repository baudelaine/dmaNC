package com.dma.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Relation {

	String _id = "";
	String _rev = null;
	String key_name = "";
	String fk_name = "";
	String pk_name = "";
	String table_name = "";
	String table_alias = "";
	String pktable_name = "";	
	String pktable_alias = "";	
	String label = "";
	String description = "";
	Map<String, String> labels = new HashMap<String, String>();
	Map<String, String> descriptions = new HashMap<String, String>();
	boolean fin = false;
	boolean ref = false;
	boolean sec = false;
	boolean tra = false;
	boolean nommageRep = false;
	String above = "";
	boolean leftJoin = false;
	String usedForDimensions = "";
	boolean rightJoin = false;
	String relationship = "";
	String where = "";
	String key_type = "";
	String type = "";
	List<Seq> seqs = new ArrayList<Seq>();
	long recCount= 0L;
	int recCountPercent = 0;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String get_rev() {
		return _rev;
	}
	public void set_rev(String _rev) {
		this._rev = _rev;
	}
	public long getRecCount() {
		return recCount;
	}
	public void setRecCount(long recCount) {
		this.recCount = recCount;
	}
	public boolean isNommageRep() {
		return nommageRep;
	}
	public void setNommageRep(boolean nommageRep) {
		this.nommageRep = nommageRep;
	}
	public String getKey_name() {
		return key_name;
	}
	public void setKey_name(String key_name) {
		this.key_name = key_name;
	}
	public String getFk_name() {
		return fk_name;
	}
	public void setFk_name(String fk_name) {
		this.fk_name = fk_name;
	}
	public String getPk_name() {
		return pk_name;
	}
	public void setPk_name(String pk_name) {
		this.pk_name = pk_name;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getTable_alias() {
		return table_alias;
	}
	public void setTable_alias(String table_alias) {
		this.table_alias = table_alias;
	}
	public String getPktable_name() {
		return pktable_name;
	}
	public void setPktable_name(String pktable_name) {
		this.pktable_name = pktable_name;
	}
	public String getPktable_alias() {
		return pktable_alias;
	}
	public void setPktable_alias(String pktable_alias) {
		this.pktable_alias = pktable_alias;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public boolean isFin() {
		return fin;
	}
	public void setFin(boolean fin) {
		this.fin = fin;
	}
	public boolean isRef() {
		return ref;
	}
	public void setRef(boolean ref) {
		this.ref = ref;
	}
	public boolean isSec() {
		return sec;
	}
	public void setSec(boolean sec) {
		this.sec = sec;
	}
	public boolean isTra() {
		return tra;
	}
	public void setTra(boolean tra) {
		this.tra = tra;
	}
	public String getRelationship() {
		return relationship;
	}
	public void setRelashionship(String relashionship) {
		this.relationship = relashionship;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getKey_type() {
		return key_type;
	}
	public void setKey_type(String key_type) {
		this.key_type = key_type;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Seq> getSeqs() {
		return seqs;
	}
	public void setSeqs(List<Seq> seqs) {
		this.seqs = seqs;
	}
	public void addSeq(Seq seq) {
		this.seqs.add(seq);
	}
	public int getRecCountPercent() {
		return recCountPercent;
	}
	public void setRecCountPercent(int recCountPercent) {
		this.recCountPercent = recCountPercent;
	}
	public String getAbove() {
		return above;
	}
	public void setAbove(String above) {
		this.above = above;
	}
	public boolean isLeftJoin() {
		return leftJoin;
	}
	public void setLeftJoin(boolean leftJoin) {
		this.leftJoin = leftJoin;
	}
	public String getUsedForDimensions() {
		return usedForDimensions;
	}
	public void setUsedForDimensions(String usedForDimensions) {
		this.usedForDimensions = usedForDimensions;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	public boolean isRightJoin() {
		return rightJoin;
	}
	public void setRightJoin(boolean rightJoin) {
		this.rightJoin = rightJoin;
	}
	
}
