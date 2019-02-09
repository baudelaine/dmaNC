package com.dma.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Tools {

	public final static String toJSON(Object o){
		try{
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		StringWriter sw = new StringWriter();
		String jsonResult = null;
		mapper.writeValue(sw, o);
		sw.flush();
		jsonResult = sw.toString();
		sw.close();
		return jsonResult;
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}
		return null;
	}
	
	public final static List<Map<String, Object>> fromJSON2ML(InputStream is){
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		
		try{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			mapList = mapper.readValue(br, new TypeReference<List<Map<String, Object>>>(){});
			return mapList;
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}
		
        return null;
	}	
	
	public final static List<Map<String, Object>> fromJSON2ML(File file) throws FileNotFoundException{
		return fromJSON2ML(new FileInputStream(file));
	}
	
	public final static List<Map<String, Object>> fromJSON2ML(String string) throws FileNotFoundException{
		return fromJSON2ML(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
	}
	
	public final static Map<String, Object> fromJSON(InputStream is){
		Map<String, Object>	map = new HashMap<String, Object>();
		
		try{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			map = mapper.readValue(br, new TypeReference<Map<String, Object>>(){});
			return map;
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}
		
        return null;
	}
	
	public final static Map<String, Object> fromJSON(File file) throws FileNotFoundException{
		return fromJSON(new FileInputStream(file));
	}

	public final static Map<String, Object> fromJSON(String string){
		return fromJSON(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
	}
	
	public final static <T> Object fromJSON(InputStream is, TypeReference<T> t){
		
		try{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			ObjectMapper mapper = new ObjectMapper();
	        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			return mapper.readValue(br, t);		
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}
		
        return null;
	}

	public final static <T> Object fromJSON(File file, TypeReference<T> t) throws FileNotFoundException{
		return fromJSON(new FileInputStream(file), t);
	}
	
	public final static <T> Object fromJSON(String string, TypeReference<T> t) throws FileNotFoundException{
		return fromJSON(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)), t);
	}		
	
	public final static Map<String, Resource> getResources(){
		
		Map<String, Resource> result = new HashMap<String, Resource>();
		
		try{
			
			String wlpHome = System.getenv("WLP_HOME");
			String wlpSrvName = System.getenv("WLP_SRV_NAME");
			
			Path cnfwlpPath = Paths.get(wlpHome +  "/usr/servers/" + wlpSrvName + "/server.xml");
			
			if(!Files.exists(cnfwlpPath)){
				return null;
			}
			
			File file = cnfwlpPath.toFile();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			
			XPathFactory xfact = XPathFactory.newInstance();
			XPath xpath = xfact.newXPath();
			
			NodeList nodeList = (NodeList) xpath.evaluate("/server/dataSource", document, XPathConstants.NODESET);
			
			List<Resource> rs = new ArrayList<Resource>();
			for(int index = 0; index < nodeList.getLength(); index++){
				Node node = nodeList.item(index);
				Resource r = new Resource();
				r.setJndiName(getAttrValue(node, "jndiName"));
				r.setCognosCatalog(getAttrValue(node, "cognosCatalog"));
				r.setCognosDataSource(getAttrValue(node, "cognosDataSource"));
				r.setCognosSchema(getAttrValue(node, "cognosSchema"));
				NodeList childNodes = node.getChildNodes();
				for(int i = 0; i < childNodes.getLength(); i++){
					Node childNode = childNodes.item(i);
					if(childNode.getNodeName().equalsIgnoreCase("jdbcDriver")){
						r.setDbEngine(getAttrValue(childNode, "libraryRef").split("Lib")[0]);
					}
					if(childNode.getNodeName().toLowerCase().startsWith("properties")){
						r.setDbName(getAttrValue(childNode, "databaseName"));
					}
				}
				
				if(StringUtils.isNoneEmpty(r.getDbName(), r.getJndiName(), r.getDbEngine())){
					rs.add(r);
				}				
			}
			
			for(Resource r: rs){
				r.setDescription(r.getDbName() + " (" + r.getJndiName() + " - " + r.getDbEngine() + ")");
				result.put(r.getJndiName(), r);
			}
			
			return result;
			
		}
		catch(Exception e){
			e.printStackTrace(System.err);
		}
		
		return null;
		
	}

	static private String getAttrValue(Node node,String attrName) {
	    if ( ! node.hasAttributes() ) return "";
	    NamedNodeMap nmap = node.getAttributes();
	    if ( nmap == null ) return "";
	    Node n = nmap.getNamedItem(attrName);
	    if ( n == null ) return "";
	    return n.getNodeValue();
	}
	
	@SuppressWarnings("unused")
	static private String getTextContent(Node parentNode,String childName) {
	    NodeList nlist = parentNode.getChildNodes();
	    for (int i = 0 ; i < nlist.getLength() ; i++) {
		    Node n = nlist.item(i);
		    String name = n.getNodeName();
		    if ( name != null && name.equals(childName) ) return n.getTextContent();
	    }
	    return "";
	}	
	
//	static Dimension recurse0(String qsAlias, String gDirName, String qsFinalName, String qSleftType, Dimension dimension, Map<String, QuerySubject> query_subjects) {
//		
//		
//		String gDirNameCurrent = "";
//		QuerySubject query_subject;
//		query_subject = query_subjects.get(qsAlias + qSleftType);
//    	List<String> orders = dimension.getDimensionDetails().getOrders();
//    	List<String> BKs = dimension.getDimensionDetails().getBKs();
//			
//		for(Relation rel: query_subject.getRelations()){
//			if(rel.isRef()) { 
//				if((rel.getUsedForDimensions().equals(dimension.getName()) && qSleftType.equalsIgnoreCase("Final")) 
//						|| (rel.getUsedForDimensions().equalsIgnoreCase("true") && qSleftType.equalsIgnoreCase("Ref"))) {
//				
//								
//					String pkAlias = rel.getPktable_alias();
//	
//					if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){
//							gDirNameCurrent = gDirName + "." + pkAlias;
//					}
//					else{
//							gDirNameCurrent = gDirName + "." + rel.getAbove();
//					}					
//					
//					for(Field field: query_subjects.get(pkAlias + "Ref").getFields()){
//					    if (field.getDimension().equals(dimension.getName())) {
//					    	orders.add(gDirNameCurrent.substring(1) + "." + field.getField_name());
//					    }
//					    BKs.add(gDirNameCurrent.substring(1) + "." + field.getField_name());
//					}
//					
//					recurse0(pkAlias, gDirNameCurrent, qsFinalName, "Ref" ,dimension, query_subjects);	
//				}
//			}
//		}
//		return dimension;
//	}	
	
	
}
