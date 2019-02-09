package com.dma.action;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dma.web.Dimension;
import com.dma.web.Field;
import com.dma.web.QuerySubject;
import com.dma.web.Relation;
import com.dma.web.Tools;
import com.fasterxml.jackson.core.type.TypeReference;

public class Test3 {

	static Map<String, QuerySubject> query_subjects = new HashMap<String, QuerySubject>();
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub

//		Path path = Paths.get("/opt/wks/dmaNC/foodmart_multi_BK-2019-02-3-12-22-49.json");
		Path path = Paths.get("/home/dma/seb/sample0/models/m0-2019-02-9-15-45-25.json");
		
		
		try {
			List<QuerySubject> json = (List<QuerySubject>) Tools.fromJSON(path.toFile(), new TypeReference<List<QuerySubject>>(){});
			
			
			for(QuerySubject qs: json) {
				query_subjects.put(qs.get_id(), qs);
			}

			// la dimension que tu scan pour trouver les les champs qui la compose
//			String selectedDimension = "d0";  
			Dimension d0 = new Dimension();
			d0.setName("d0");
			
			for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
				
				if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
					
					String qsAlias = query_subject.getValue().getTable_alias();  // table de gauche, celle ou tu es actuellement
					String gDirName = ""; // prefix qu'on cherche, il vaut cher
					String qsFinalName = query_subject.getValue().getTable_alias();   //CONSTANTE, nom du QS final auquel l'arbre ref est accroché, le tronc, on peut le connaitre à tout moment de f1
					String qSleftType = "Final";
					
					System.out.println("qsAlias=" + qsAlias);
					d0.getDimensionDetails().setQsFinalName(qsFinalName);
					
					
					recurse0(qsAlias, gDirName, qsFinalName, qSleftType, d0);
					
					System.out.println(Tools.toJSON(d0));
															
				}
				
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected static Dimension recurse0(String qsAlias, String gDirName, String qsFinalName, String qSleftType, Dimension dimension) {
		
		
		String gDirNameCurrent = "";
		QuerySubject query_subject;
		query_subject = query_subjects.get(qsAlias + qSleftType);
    	List<String> orders = dimension.getDimensionDetails().getOrders();
    	List<String> BKs = dimension.getDimensionDetails().getBKs();
			
		for(Relation rel: query_subject.getRelations()){
			if(rel.isRef()) { 
				if((rel.getUsedForDimensions().equals(dimension.getName()) && qSleftType.equalsIgnoreCase("Final")) 
						|| (rel.getUsedForDimensions().equalsIgnoreCase("true") && qSleftType.equalsIgnoreCase("Ref"))) {
				
								
					String pkAlias = rel.getPktable_alias();
	
					if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){
							gDirNameCurrent = gDirName + "." + pkAlias;
					}
					else{
							gDirNameCurrent = gDirName + "." + rel.getAbove();
					}					
					
					for(Field field: query_subjects.get(pkAlias + "Ref").getFields()){
					    if (field.getDimension().equals(dimension.getName())) {
					    	orders.add(gDirNameCurrent.substring(1) + "." + field.getField_name());
					    }
					    BKs.add(gDirNameCurrent.substring(1) + "." + field.getField_name());
					}
					
					recurse0(pkAlias, gDirNameCurrent, qsFinalName, "Ref" ,dimension);	
				}
			}
		}
		return dimension;
	}	

}
