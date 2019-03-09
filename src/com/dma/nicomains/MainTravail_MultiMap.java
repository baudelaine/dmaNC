package com.dma.nicomains;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dma.svc.CognosSVC;
import com.dma.svc.FactorySVC;
import com.dma.web.Field;
import com.dma.web.QuerySubject;
import com.dma.web.Relation;
import com.dma.web.RelationShip;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainTravail_MultiMap {

	static Map<String, Integer> gRefMap;
	static List<RelationShip> rsList;
	static Map<String, QuerySubject> query_subjects;
	static Map<String, String> labelMap;
	static Map<String, String> toolTipMap;
	static Map<String, String> filterMap;
	static Map<String, String> filterMapApply;
	static List<QuerySubject> qsList = null;
	static CognosSVC csvc;
	static FactorySVC fsvc;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String json = null;
		String model = "/opt/wks/dmaNC/WebContent/models/foodmart_multi_map.json";
		Path path = Paths.get(model);
		Charset charset = StandardCharsets.UTF_8;

		Map<String, Integer> recurseCount = null;
 
		if(Files.exists(path)){
			try {
				json = new String(Files.readAllBytes(path), charset);
//				System.out.println("json =" + json);

		        ObjectMapper mapper = new ObjectMapper();
		        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		        
//		        QuerySubject[] qsArray = mapper.readValue(br, QuerySubject[].class);
		        qsList = Arrays.asList(mapper.readValue(json, QuerySubject[].class));
		        
		        query_subjects = new HashMap<String, QuerySubject>();
		        recurseCount = new HashMap<String, Integer>();
		        
		        for(QuerySubject qs: qsList){
		        	query_subjects.put(qs.get_id(), qs);
		        	recurseCount.put(qs.getTable_alias(), 0);
		        }
		       
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// On est bien Tintin...
		
//        TaskerSVC.start();
		
		String cognosDataSource = "PGfoodmart";
		String cognosCatalog = "";
		String cognosSchema = "public";
		String cognosDefaultLocale = "en-gb";
		String cognosLocales = "en";
		
		String cognosDispatcher = "http://172.16.186.246:9300/p2pd/servlet/dispatch";
		String pathToXML = "/opt/wks/dmaNC/WebContent/res/templates";
		String cognosLogin = "admin";
		String cognosPassword = "Freestyle05$";
		String cognosNamespace = "CognosEx";
		String projectName = "foodmart_multi_map4";
		String cognosFolder = "C:/models";
		String cognosModelsPath = "/mnt/models";
		String dbEngine = "PGSQL";
		
		Path projectPath = Paths.get(cognosModelsPath + "/" + projectName);
		
		
		csvc = new CognosSVC(cognosDispatcher);
		csvc.setPathToXML(pathToXML);
		fsvc = new FactorySVC(csvc);
		csvc.logon(cognosLogin, cognosPassword, cognosNamespace);
		String modelName = projectName;
		csvc.openModel(modelName, cognosFolder);
		fsvc.setLocale(cognosDefaultLocale);
		

// end start		
//		TaskerSVC.IICInitNameSpace();

		fsvc.createNamespace("PHYSICAL", "Model");
		fsvc.createNamespace("PHYSICALUSED", "Model");
		fsvc.createNamespace("AUTOGENERATION", "Model");
		fsvc.createNamespace("FINAL", "AUTOGENERATION");
		fsvc.createNamespace("REF", "AUTOGENERATION");
		fsvc.createNamespace("SEC", "AUTOGENERATION");
		fsvc.createNamespace("FILTER_FINAL", "AUTOGENERATION");
		fsvc.createNamespace("FILTER_REF", "AUTOGENERATION");
		fsvc.createNamespace("DATA", "Model");
		fsvc.createNamespace("DIMENSIONAL", "Model");

//		TaskerSVC.Import();
		fsvc.DBImport("PHYSICAL", cognosDataSource, cognosCatalog ,cognosSchema, dbEngine);
		
		gRefMap = new HashMap<String, Integer>();
		
		rsList = new ArrayList<RelationShip>();

		labelMap = new HashMap<String, String>();
		toolTipMap = new HashMap<String, String>();
		filterMap = new HashMap<String, String>();
		filterMapApply = new HashMap<String, String>();
				
		for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
			
			if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
				
				fsvc.copyQuerySubject("[PHYSICALUSED]", "[PHYSICAL].[" + query_subject.getValue().getTable_name() + "]");
				fsvc.renameQuerySubject("[PHYSICALUSED].[" + query_subject.getValue().getTable_name() + "]", "FINAL_" + query_subject.getValue().getTable_alias());
				
				fsvc.createQuerySubject("PHYSICALUSED", "FINAL", "FINAL_" + query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_alias());
				//ajout filter
				if (!query_subject.getValue().getFilter().equals(""))
				{
					fsvc.createQuerySubject("FINAL", "FILTER_FINAL", query_subject.getValue().getTable_alias() , query_subject.getValue().getTable_alias());
					filterMap.put(query_subject.getValue().getTable_alias(), query_subject.getValue().getFilter());
					filterMapApply.put(query_subject.getValue().getTable_alias(), "[FILTER_FINAL].[" + query_subject.getValue().getTable_alias() + "]");
					
					fsvc.createQuerySubject("FILTER_FINAL", "DATA", query_subject.getValue().getTable_alias() , query_subject.getValue().getTable_alias());
				} else {
					fsvc.createQuerySubject("FINAL", "DATA", query_subject.getValue().getTable_alias() , query_subject.getValue().getTable_alias());
				}
				//end filter
				//tooltip
				String desc = "";
				if(query_subject.getValue().getDescription() != null) {desc = ": " + query_subject.getValue().getDescription();}
				fsvc.createScreenTip("querySubject", "[DATA].[" + query_subject.getValue().getTable_alias() + "]" , query_subject.getValue().getTable_name() + desc );
				//end tooltip
				
				//lancement f1 ref
				for(QuerySubject qs: qsList){
		        	recurseCount.put(qs.getTable_alias(), 0);
		        }
				
				f1(query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_alias(), "", "[DATA].[" + query_subject.getValue().getTable_alias() + "]", query_subject.getValue().getTable_alias(), recurseCount, true);
				//end f1
				
				//lancement f2 sec
				for(QuerySubject qs: qsList){
		        	recurseCount.put(qs.getTable_alias(), 0);
		        }
				
				f2(query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_alias(), "", recurseCount, "FINAL");
				//end lancement f2
				
				for(Relation rel: query_subject.getValue().getRelations()){
					if(rel.isFin()){
						
						RelationShip RS = new RelationShip("[FINAL].[" + query_subject.getValue().getTable_alias() + "]" , "[FINAL].[" + rel.getPktable_alias() + "]");
						// changer en qs + refobj
						RS.setExpression(rel.getRelationship());
						if (rel.isRightJoin())
						{
							RS.setCard_left_min("zero");
						} else {
							RS.setCard_left_min("one");
						}
						RS.setCard_left_max("many");
	
						if (rel.isLeftJoin())
						{
							RS.setCard_right_min("zero");
						} else {
							RS.setCard_right_min("one");
						}
						RS.setCard_right_max("one");
						RS.setParentNamespace("FINAL");
						rsList.add(RS);					
				
					}
				}				
				//add label map qs
				if(query_subject.getValue().getLabel() == null || query_subject.getValue().getLabel().equals("")) {
				labelMap.put(query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_name());
				} else {
					labelMap.put(query_subject.getValue().getTable_alias(), query_subject.getValue().getLabel());
				}
				
				//add label map fields
				for(Field field: query_subject.getValue().getFields()) {
					
					if (field.isCustom()) {
						
						fsvc.createQueryItem("[DATA].[" + query_subject.getValue().getTable_alias() + "]", field.getField_name(), field.getExpression(), cognosDefaultLocale);
					}
					
					if (field.getLabel() == null || field.getLabel().equals("")) {
					labelMap.put(query_subject.getValue().getTable_alias() + "." + field.getField_name(), field.getField_name());
					} else {
						labelMap.put(query_subject.getValue().getTable_alias() + "." + field.getField_name(), field.getLabel());
					}
					//add tooltip
					desc = "";
					if(field.getDescription() != null) {desc = ": " + field.getDescription();}	
					fsvc.createScreenTip("queryItem", "[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", query_subject.getValue().getTable_name() + "." + field.getField_name() + desc);
					//end tooltip
					//change property query item
					fsvc.changeQueryItemProperty("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", "usage", field.getIcon().toLowerCase());
					if (!field.getDisplayType().toLowerCase().equals("value"))
					{
						fsvc.changeQueryItemProperty("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", "displayType", field.getDisplayType().toLowerCase());
					}
					if (field.isHidden())
					{
						fsvc.changeQueryItemProperty("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", "hidden", "true");
						
					}
				//end change
				}
				// end label
			}
			
		}
		//IICCreateRelation(rsList);
		for(RelationShip rs: rsList){
			fsvc.createRelationship(rs);
		}
		
		//Filters creation
		for(Entry<String, String> map: filterMap.entrySet()){
			
			String pathRefQS = filterMapApply.get(map.getKey());
			if (pathRefQS != null) {
			fsvc.createQuerySubjectFilter(pathRefQS , map.getValue());
			}
		}
		
		// multidimensional

		Map<String, Map<String, String>> measures = new HashMap<String, Map<String, String>>();
		
		String qSIDStart = "";
		for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
			Set<String> set = query_subject.getValue().getLinker_ids();
			for (String s : set) {
				if (s.equals("Root")) {
					qSIDStart = query_subject.getValue().get_id();
					System.out.println(query_subject.getValue().get_id() + " ids : " + s);
					createMeasures("", qSIDStart, true, measures);
//					System.out.println("MeasureMap : " + measures.toString());
				}
			}
		}

		Map<String, Map<String, String>> dimensions = new HashMap<String, Map<String, String>>();
		scanDimensions(dimensions);
		scanFinalFieldsDimensions(dimensions);				
		createHierarchiesNb(dimensions);				
		renameHierarchies(dimensions);
		buildDimensions(dimensions, measures);

// end multidimensional

		
		fsvc.addLocale(cognosLocales, cognosDefaultLocale);				
		
		// tests
		
		csvc.executeAllActions();
		// fin tests
	
		//stop();
		csvc.saveModel();
		csvc.closeModel();
		csvc.logoff();
		System.out.println("END COGNOS API");
		
		// code parser xml for labels

		System.out.println("START XML MODIFICATION");
			try {
							
				String modelSharedPath = projectPath + "/model.xml";
				
				Path input = Paths.get(modelSharedPath);
				Path output = Paths.get(modelSharedPath);
				
				String datas = null;
				String inputSearch = "xmlns=\"http://www.developer.cognos.com/schemas/bmt/60/12\"";
				String outputSearch = "queryMode=\"dynamic\"";
				String outputReplace = outputSearch + " " + inputSearch;  
				
				charset = StandardCharsets.UTF_8;
				if(Files.exists(input)){
					datas = new String(Files.readAllBytes(input), charset);
				}

				datas = StringUtils.replace(datas, inputSearch, "");
				
				// modifs
				
//				File xmlFile = new File(ConfigProperties.modelXML);
				SAXReader reader = new SAXReader();
				Document document = reader.read(new ByteArrayInputStream(datas.getBytes(StandardCharsets.UTF_8)));

				String namespaceName = "DATA";
				String spath = "/project/namespace/namespace";
				int k=1;
				
				Element namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");			
				while(!namespace.getStringValue().equals(namespaceName) && namespace != null)
				{
				k++;
				namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");
				}
				
				spath = spath + "[" + k + "]";
				fsvc.recursiveParserQS(document, spath, cognosLocales, labelMap);

				//dimensions
				namespaceName = "DIMENSIONAL";
				spath = "/project/namespace/namespace";
				k=1;
				
				namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");			
				while(!namespace.getStringValue().equals(namespaceName) && namespace != null)
				{
				k++;
				namespace = (Element) document.selectSingleNode(spath + "[" + k + "]/name");
				}
				
				spath = spath + "[" + k + "]";
				fsvc.recursiveParserDimension(document, spath, "en", labelMap);

				
				try {
	
					datas = document.asXML();

					datas = StringUtils.replace(datas, outputSearch, outputReplace);
					Files.write(output, datas.getBytes());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// fin test writer
				
			} catch (DocumentException | IOException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println("END XML MODIFICATION");		
			//publication
//			System.out.println("Create and Publish Package");	
			
			//start
			csvc = new CognosSVC(cognosDispatcher);
			csvc.setPathToXML(pathToXML);
			fsvc = new FactorySVC(csvc);
			csvc.logon(cognosLogin, cognosPassword, cognosNamespace);
			csvc.openModel(modelName, cognosFolder);
			fsvc.setLocale(cognosDefaultLocale);
			
			@SuppressWarnings("unused")
			String[] locales = {cognosLocales};
			fsvc.changePropertyFixIDDefaultLocale();
//			fsvc.createPackage(modelName, modelName, modelName, locales);
//			fsvc.publishPackage(modelName,"/content");
			
			csvc.executeAllActions();
			
			csvc.saveModel();
			csvc.closeModel();
			csvc.logoff();
			
			
			System.out.println("Model Generation Finished");
	}
	
	protected static void f1(String qsAlias, String qsAliasInc, String gDirName, String qsFinal, String qsFinalName, Map<String, Integer> recurseCount, Boolean qSleftIsFinal) {
		
		Map<String, Integer> copyRecurseCount = new HashMap<String, Integer>();
		copyRecurseCount.putAll(recurseCount);
		
		QuerySubject query_subject;
		if (!qSleftIsFinal) {
			
			query_subject = query_subjects.get(qsAlias + "Ref");
			
			int j = copyRecurseCount.get(qsAlias);
			if(j == query_subject.getRecurseCount()){
				return;
			}
			copyRecurseCount.put(qsAlias, j + 1);
		} else {
			query_subject = query_subjects.get(qsAlias + "Final");
		}

		for(Relation rel: query_subject.getRelations()){
			if(rel.isRef()){
				
				String pkAlias = rel.getPktable_alias();
				Integer i = gRefMap.get(pkAlias);
				
				if(i == null){
					gRefMap.put(pkAlias, new Integer(0));
					i = gRefMap.get(pkAlias);
				}
				gRefMap.put(pkAlias, i + 1);

				fsvc.copyQuerySubject("[PHYSICALUSED]", "[PHYSICAL].[" + rel.getPktable_name() + "]");	
				fsvc.renameQuerySubject("[PHYSICALUSED].[" + rel.getPktable_name() + "]","REF_" + pkAlias + String.valueOf(i));
				fsvc.createQuerySubject("PHYSICALUSED", "REF","REF_" + pkAlias + String.valueOf(i), pkAlias + String.valueOf(i));
				
				//seq
				String gFieldName = "";
				String gDirNameCurrent = "";
				String label = "";
				if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){
					if (qSleftIsFinal) {
						gFieldName = pkAlias;
						gDirNameCurrent = "." + pkAlias;
					} else {
						gFieldName = gDirName.substring(1) + "." + pkAlias;
						gDirNameCurrent = gDirName + "." + pkAlias;
					}
					
					if(query_subjects.get(pkAlias + "Ref").getLabel() == null || query_subjects.get(pkAlias + "Ref").getLabel().equals(""))
					{label = pkAlias;} else {label = query_subjects.get(pkAlias + "Ref").getLabel();
					}
					labelMap.put(qsFinalName + gDirNameCurrent, label);
				}
				else{
					if (qSleftIsFinal) {
						gFieldName = rel.getAbove();
						gDirNameCurrent = "." + rel.getAbove();
					} else {
						gFieldName = gDirName.substring(1) + "." + rel.getAbove();
						gDirNameCurrent = gDirName + "." + rel.getAbove();
					}
				}
				
				//filtre
				String filterNameSpaceSource = "[REF]";
			//	String filterReset = "";
				if (!query_subjects.get(pkAlias + "Ref").getFilter().equals(""))
				{
					//traitement language filter DDtool -> Separé par ; = diffrentes clauses pour ce QSRef
					//séparé par :  Partie 0 du tableau = emplacement QS, Partie 1 = clause filtre
					// remplacer * par le chemin en cours dans l'emplacement et dans la clause.
					
					fsvc.createQuerySubject("REF", "FILTER_REF", pkAlias + String.valueOf(i), pkAlias + String.valueOf(i));
					
					String filterArea = query_subjects.get(pkAlias + "Ref").getFilter();
					String allClauses[] = StringUtils.split(filterArea, ";");
					
					Boolean exit = false;
					for (int x=0; x < 3 && !exit; x++) {
						for (int y=0; y < allClauses.length && !exit; y++) {
							if(allClauses[y].contains(":")) {
								String pathFilter[] = StringUtils.split(allClauses[y], ":");
								String pathRefQs = pathFilter[0].trim();
								String filterRefQs = pathFilter[1];
							//	System.out.println(pathRefQs + " ? equals ? [" + qsFinalName + gDirNameCurrent + "]");
								//replace *-j dans lexpression du filtre
								String actualPath = qsFinalName + gDirNameCurrent;
								String actualPathTable[] = StringUtils.split(actualPath, ".");
								if(filterRefQs.contains("*-")){	
									Boolean rok = false;
									for (int z=1; z<100 && !rok;z++){
										if(filterRefQs.contains("*-" + String.valueOf(z))) {
											String pathReplace = "";
											for (int k=0;k < actualPathTable.length - z; k++) {
												pathReplace = pathReplace + actualPathTable[k];
												if (k != actualPathTable.length - z - 1) {
													pathReplace = pathReplace + ".";
												}
											}
											if(!filterRefQs.contains("*-")){
												rok = true;
											}
										//	System.out.println("pathReplace : *-" + String.valueOf(z) + " = " + pathReplace);
											filterRefQs = StringUtils.replace(filterRefQs, "*-" + String.valueOf(z), pathReplace);
										}
									}
								}
								//replace * dans l'expression du filtre
								filterRefQs = StringUtils.replace(filterRefQs, "*", qsFinalName + gDirNameCurrent);										
								if (pathRefQs.equals("[" + qsFinalName + gDirNameCurrent + "]")) {
									
								//	System.out.println("filtre ajouté :" + pathRefQs + " * * * * * " + filterRefQs);
									filterMap.put(qsFinalName + gDirNameCurrent, filterRefQs);
									//Set filter path dans ref pkalias + i correspondancies
									filterMapApply.put(qsFinalName + gDirNameCurrent, "[FILTER_REF].[" + pkAlias + String.valueOf(i) + "]");
									
									exit=true;
								}
							}
						}
						//remmplacement %
						if(!exit && x == 0) {
							String actualPath = qsFinalName + gDirNameCurrent;
						//	System.out.println("actualPath  * * * * * * * * * * " + actualPath);
							for (int y=0; y < allClauses.length; y++) {
								if(allClauses[y].contains(":")) {
									String pathFilter[] = StringUtils.split(allClauses[y], ":");
									String pathRefQs = pathFilter[0].trim();
									String containPaths[] = StringUtils.split(pathRefQs, "%");
								//	System.out.println("containPaths  * * * * * * * * * * " + containPaths.length);
									if (containPaths.length == 2) {
									//	System.out.println("containPaths  * * * * * * * * * * " + containPaths[0].substring(1));
										if (actualPath.startsWith(containPaths[0].substring(1))) {
											filterArea = StringUtils.replace(filterArea, pathRefQs, "[" + actualPath + "]");
										//	System.out.println("filterArea * * * * " + filterArea);
										}
									} else if (containPaths.length == 3) {
									//	System.out.println("containPaths  * * * * * * * * * * " + containPaths[1]);
										if (actualPath.contains(containPaths[1])) {
											filterArea = StringUtils.replace(filterArea, pathRefQs, "[" + actualPath + "]");
										//	System.out.println("filterArea * * * * " + filterArea);
										}
									}
								}
							}
							allClauses = StringUtils.split(filterArea, ";");
						//	System.out.println("filterArea * * * * " + filterArea);
						}
						//remplacement *
						if(!exit && x == 1) {
							filterArea = StringUtils.replace(filterArea, "*-", "ù");
							filterArea = StringUtils.replace(filterArea, "*", qsFinalName + gDirNameCurrent);
							filterArea = StringUtils.replace(filterArea, "ù", "*-");
						//	System.out.println("* = " + qsFinalName + gDirNameCurrent);
							allClauses = StringUtils.split(filterArea, ";");
						}
					}
					filterNameSpaceSource = "[FILTER_REF]";
				}
				//end filtre
				
				String gFieldNameReorder;
				if(qSleftIsFinal) {
					gFieldNameReorder = rel.getAbove();
				} else {
					gFieldNameReorder = gDirName.substring(1) + "." + rel.getAbove();
				}
				String rep = qsFinal + ".[" + gDirName + "]";
				
				if (qSleftIsFinal) {
					fsvc.createSubFolder("[DATA].[" + qsFinalName + "]", gDirNameCurrent);
				} else {
					fsvc.createSubFolderInSubFolderIIC(rep, gDirNameCurrent);
				}

				//add tooltip
				String desc = "";
				if(query_subjects.get(pkAlias + "Ref").getDescription() != null) {desc = ": " + query_subjects.get(pkAlias + "Ref").getDescription();}
				fsvc.createScreenTip("queryItemFolder", qsFinal + ".[" + gDirNameCurrent + "]", query_subjects.get(pkAlias + "Ref").getTable_name() + desc);
				//end tooltip
				
				if(rel.getKey_type().equalsIgnoreCase("F")){
					fsvc.ReorderSubFolderBefore(qsFinal + ".[" + gDirNameCurrent + "]", qsFinal + ".[" + gFieldNameReorder + "]");
				}
				
				for(Field field: query_subjects.get(pkAlias + "Ref").getFields()){
					
					fsvc.createQueryItemInFolder(qsFinal, gDirNameCurrent, gFieldName + "." + field.getField_name(), filterNameSpaceSource + ".[" + pkAlias + String.valueOf(i) +"].[" + field.getField_name() + "]");
					
					//add label
					if(field.getLabel() == null || field.getLabel().equals(""))
					{label = field.getField_name();} else {label = field.getLabel();
					}
					labelMap.put(qsFinalName + "." + gFieldName + "." + field.getField_name(), label);
					// end label
					// add tooltip
					desc = "";
					if(field.getDescription() != null) {desc = ": " + field.getDescription();}
					fsvc.createScreenTip("queryItem", qsFinal + ".[" + gFieldName + "." + field.getField_name() + "]", query_subjects.get(pkAlias + "Ref").getTable_name() + "." + field.getField_name() + desc);
					// end tooltip
					//change property query item
					fsvc.changeQueryItemProperty(qsFinal + ".[" + gFieldName + "." + field.getField_name() + "]", "usage", field.getIcon().toLowerCase());
					if (!field.getDisplayType().toLowerCase().equals("value"))
					{
						fsvc.changeQueryItemProperty(qsFinal + ".[" + gFieldName + "." + field.getField_name() + "]", "displayType", field.getDisplayType().toLowerCase());
						
					}
					if (field.isHidden())
					{
						fsvc.changeQueryItemProperty(qsFinal + ".[" + gFieldName + "." + field.getField_name() + "]", "hidden", "true");
						
					}
					//end change
				}

				//create relation
				String parentNameSpace = "";
				String leftQsType = "";
				if(qSleftIsFinal) {
					parentNameSpace = "AUTOGENERATION";
					leftQsType = "FINAL";
				} else {
					parentNameSpace = "REF";
					leftQsType = "REF";
				}
				
				RelationShip RS = new RelationShip("["+ leftQsType + "].[" + qsAliasInc + "]" , "[REF].[" + pkAlias + String.valueOf(i) + "]");
				// changer en qs + refobj
				String fixedExp = rel.getRelationship();
				if(!qSleftIsFinal) {
					fixedExp = StringUtils.replace(rel.getRelationship(), "[REF].[" + qsAlias + "]", "[REF].[" + qsAliasInc + "]");
				}
				fixedExp = StringUtils.replace(fixedExp, "[REF].[" + pkAlias + "]", "[REF].[" + pkAlias + String.valueOf(i) + "]");
				RS.setExpression(fixedExp);
				if (rel.isRightJoin())
				{
					RS.setCard_left_min("zero");
				} else {
					RS.setCard_left_min("one");
				}
				RS.setCard_left_max("many");

				if (rel.isLeftJoin())
				{
					RS.setCard_right_min("zero");
				} else {
					RS.setCard_right_min("one");
				}
				RS.setCard_right_max("one");
				RS.setParentNamespace(parentNameSpace);
				rsList.add(RS);
				
				//end create relation
				

				f1(pkAlias, pkAlias + String.valueOf(i), gDirNameCurrent, qsFinal, qsFinalName, copyRecurseCount, false);
				
				//Modify filters
				String QSPath = qsFinalName + gDirNameCurrent;
				for(Entry<String, String> map: filterMap.entrySet()){
					String EntirePath = map.getKey();
					String filterReplace = map.getValue();
				//	System.out.println(QSPath + " ? startWith ? " + EntirePath);
					if (filterReplace.contains("[REF]") && (QSPath.startsWith(EntirePath) || EntirePath.startsWith(QSPath))) {
						filterReplace = StringUtils.replace(filterReplace, "[REF].[" + pkAlias + "]", "[REF].[" + pkAlias + String.valueOf(i) + "]");
					//	System.out.println(map.getKey() + " ? filtre ref modify ? " + filterReplace);
						filterMap.put(map.getKey(), filterReplace);
					}
				}
				//end modify	
			}
		}
		Map<String, Integer> recurseCountSec = null;
		recurseCountSec = new HashMap<String, Integer>();
		for(QuerySubject qs: qsList){
        	recurseCountSec.put(qs.getTable_alias(), 0);
        }
		if (!qSleftIsFinal) {
		f2(qsAlias, qsAliasInc, qsFinalName + gDirName, "", recurseCountSec, "REF");
		}
	}

	protected static void f2(String qsAlias, String qsAliasInc, String gDirName, String qsInitialName, Map<String, Integer> recurseCount, String leftQSType){
		
		Map<String, Integer> copyRecurseCount = new HashMap<String, Integer>();
		copyRecurseCount.putAll(recurseCount);
		
		QuerySubject query_subject;
		if (leftQSType.equals("FINAL")) {
			query_subject = query_subjects.get(qsAlias + "Final");
		} else if (leftQSType.equals("REF")){
			query_subject = query_subjects.get(qsAlias + "Ref");
		}
		else {
			query_subject = query_subjects.get(qsAlias + "Sec");
			
			int j = copyRecurseCount.get(qsAlias);
			if(j == query_subject.getRecurseCount()){
				return;
			}
			copyRecurseCount.put(qsAlias, j + 1);
		}

		for(Relation rel: query_subject.getRelations()){
			if(rel.isSec()){
				
				String pkAlias = rel.getPktable_alias();
				String sec = "";
				if (!leftQSType.equals("SEC")) {
					sec = ".SEC";
				}
				
				//seq
				String gDirNameCurrent = "";
				if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){
					gDirNameCurrent = gDirName + sec + "." + pkAlias;
				}
				else{
					gDirNameCurrent = gDirName + sec + "." + rel.getAbove();
				}
				
				fsvc.copyQuerySubject("[PHYSICALUSED]", "[PHYSICAL].[" + rel.getPktable_name() + "]");	
				fsvc.renameQuerySubject("[PHYSICALUSED].[" + rel.getPktable_name() + "]","SEC_" + qsInitialName + gDirNameCurrent);
				fsvc.createQuerySubject("PHYSICALUSED", "SEC","SEC_" + qsInitialName + gDirNameCurrent, qsInitialName + gDirNameCurrent);

				//create relation
				String parentNameSpace = "";
				
				if(leftQSType.equals("SEC")) {
					parentNameSpace = "SEC";
				} else {
					parentNameSpace = "AUTOGENERATION";
				}
				
				RelationShip RS = new RelationShip("[" + leftQSType + "].[" + qsAliasInc + "]" , "[SEC].[" + qsInitialName + gDirNameCurrent + "]");
				// changer en qs + refobj
				String fixedExp = rel.getRelationship();
				if (!leftQSType.equals("FINAL")) {
					fixedExp = StringUtils.replace(fixedExp, "[" + leftQSType + "].[" + qsAlias + "]", "[" + leftQSType + "].[" + qsAliasInc + "]");
				}
				fixedExp = StringUtils.replace(fixedExp, "[SEC].[" + pkAlias + "]", "[SEC].[" + qsInitialName + gDirNameCurrent + "]");
				RS.setExpression(fixedExp);
				if (rel.isRightJoin())
				{
					RS.setCard_left_min("zero");
				} else {
					RS.setCard_left_min("one");
				}
				RS.setCard_left_max("many");

				if (rel.isLeftJoin())
				{
					RS.setCard_right_min("zero");
				} else {
					RS.setCard_right_min("one");
				}
				RS.setCard_right_max("one");
				RS.setParentNamespace(parentNameSpace);
				rsList.add(RS);
				
				f2(pkAlias, qsInitialName + gDirNameCurrent, gDirNameCurrent, "", copyRecurseCount, "SEC");
				
			}
		}
	}
	
	protected static void createMeasures(String PreviousQSID, String qsID, Boolean first, Map<String, Map<String, String>> mm) {

		QuerySubject query_subject = query_subjects.get(qsID);
		QuerySubject query_subject_prev = query_subjects.get(PreviousQSID);
		
		if(!mm.containsKey("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact]")) {
			Boolean folderCreated = false;
			for(Field field: query_subject.getFields()) {
				
				if ((!field.getMeasure().equals(""))) {
					
					if (!folderCreated) {
						if (first) {
						//	fsvc.createFolder("[DIMENSIONAL]", query_subject.getTable_alias());
							first = false;
						} else {
						//	fsvc.createFolderInFolder("[DIMENSIONAL]", query_subject_prev.getTable_alias(), query_subject.getTable_alias());
						}
					folderCreated = true;
				//	fsvc.createMeasureDimension("[DIMENSIONAL].[" + query_subject.getTable_alias() + "]", query_subject.getTable_alias() + " Fact");
					fsvc.createMeasureDimension("[DIMENSIONAL]", query_subject.getTable_alias() + " Fact"); //without folder
					Map<String, String> m = new HashMap<String, String>();
					mm.put("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact]", m);
					}
				fsvc.createMeasure("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact" + "]", query_subject.getTable_alias() + "." + field.getField_name(), "[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]");
				fsvc.modify("measure/regularAggregate", "/O/regularAggregate[0]/O/[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact" + "].[" + query_subject.getTable_alias() + "." + field.getField_name() + "]", field.getMeasure().toLowerCase());
				Map<String, String> m = mm.get("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact]");
				m.put(field.getField_name(), "[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact" + "].[" + query_subject.getTable_alias() + "." + field.getField_name() + "]");
				}
			}
		}
		
		for(Relation rel: query_subject.getRelations()){
			if(rel.isFin()){
				createMeasures(qsID, rel.getPktable_alias() + "Final", first, mm);
			}
		}
	}
	
	protected static void scanDimensions(Map<String, Map<String, String>> dimensions) {
		//On parcours tous les QS Final + Ref pour trouver les différentes dimensions, sans alimenter les hierachies, ni les levels, ni les champs
		
		for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
			for(Field field: query_subject.getValue().getFields()) {
				if (!field.getDimension().equals("")) {
					if (!field.getDimension().startsWith("[") && !field.getDimension().endsWith("]")) {
						
						//Multi map
						List<Map<String, String>> fieldDimensions = field.getDimensions();
						for (Map<String, String> dimensionAttributes : fieldDimensions) {
							String attDimension = dimensionAttributes.get("dimension");
							String attOrder = dimensionAttributes.get("order");
							String attBK = dimensionAttributes.get("bk");
							System.out.println("attDimension : " + attDimension);
							System.out.println("attOrder : " + attOrder);
							System.out.println("attBK : " + attBK);
							
							if (!dimensions.containsKey(attDimension)) {
								Map<String, String> hierarchiesFields = new HashMap<String, String>();
								//On crée la dimension
								dimensions.put(attDimension, hierarchiesFields);
							}
						}
						//end Multi map
						/*
						String dim[] = StringUtils.split(field.getDimension(), ",");
						for (int i=0; i < dim.length; i++) {
							//un tableau pour les eventuelles multiples dimensions pour un champ de ref. 
							String dimension = dim[i];
							if (!dimensions.containsKey(dimension)) {
								Map<String, String> hierarchiesFields = new HashMap<String, String>();
								//On crée la dimension
								dimensions.put(dimension, hierarchiesFields);
							}
						} */
					} else {
						// create time dimension
						Map<String, String> hierarchies = new HashMap<String, String>();
						dimensions.put("Time Dimension " + query_subject.getValue().getTable_alias() + "." + field.getField_name(), hierarchies);
					}
				}
			}
		}
	}
	
	protected static void scanFinalFieldsDimensions(Map<String, Map<String, String>> dimensions) {
		
		for (Entry<String, Map<String, String>> dimension: dimensions.entrySet()) {
			System.out.println("dimension.getKey() : " + dimension.getKey());
			for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
				if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
					String qsFinal = query_subject.getValue().getTable_alias();
					for(Field field: query_subject.getValue().getFields()) {
						/*
						if (field.getDimension().equals(dimension.getKey())) {
							Map<String, String> hierarchiesFields = dimension.getValue();
							//S'il n'y est pas deja, on ajoute le champ dans le map des fields, tous les champs de la dimension seront dans ce map.
							if (!hierarchiesFields.containsKey("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]")) {
								//on ajoute le champ concerné dans le map des fields afin de determiner plus tard le nombre de hierarchies et les hierarchies
								hierarchiesFields.put("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", field.getOrder());
							}
						}*/
						//multi_map
						List<Map<String, String>> fieldDimensions = field.getDimensions();
						for (Map<String, String> dimensionAttributes : fieldDimensions) {
							String attDimension = dimensionAttributes.get("dimension");
							String attOrder = dimensionAttributes.get("order");
							if (attDimension.equals(dimension.getKey())) {
								Map<String, String> hierarchiesFields = dimension.getValue();
								//S'il n'y est pas deja, on ajoute le champ dans le map des fields, tous les champs de la dimension seront dans ce map.
								if (!hierarchiesFields.containsKey("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]")) {
									//on ajoute le champ concerné dans le map des fields afin de determiner plus tard le nombre de hierarchies et les hierarchies
									hierarchiesFields.put("[DATA].[" + query_subject.getValue().getTable_alias() + "].[" + field.getField_name() + "]", attOrder);
								}
							}
						}
						//end multi map
					}
					//lancement scanRef ref
					Map<String, Integer> recurseCount = new HashMap<String, Integer>();
					for(QuerySubject qs: qsList){
			        	recurseCount.put(qs.getTable_alias(), 0);
					}
					scanRefFieldsDimensions(dimensions, dimension.getKey(), query_subject.getValue().getTable_alias(), query_subject.getValue().getTable_alias(), "", "[DATA].[" + query_subject.getValue().getTable_alias() + "]", query_subject.getValue().getTable_alias(), recurseCount, true);
					//End scanRef
				}	
			}
		}
	}
			
	protected static void scanRefFieldsDimensions(Map<String, Map<String, String>> dimensions, String dimension, String qsAlias, String qsAliasInc, String gDirName, String qsFinal, String qsFinalName, Map<String, Integer> recurseCount, Boolean qSleftIsFinal) {
		//On va parcourir chaque arbre ref en partant de la table final, afin de pouvoir utiliser le gDirName pour modifier la référence de la clef du map. Ex SYSUSERRef devient pour cognos [DATA].[S_SAMPLE].[SECURITYUSER.SYSUSERDESC]
		
		Map<String, String> hierarchiesFields = dimensions.get(dimension);
		Map<String, Integer> copyRecurseCount = new HashMap<String, Integer>();
		copyRecurseCount.putAll(recurseCount);
		
		QuerySubject query_subject;
		if (!qSleftIsFinal) {
			
			query_subject = query_subjects.get(qsAlias + "Ref");
			
			int j = copyRecurseCount.get(qsAlias);
			if(j == query_subject.getRecurseCount()){
				return;
			}
			copyRecurseCount.put(qsAlias, j + 1);
		} else {
			query_subject = query_subjects.get(qsAlias + "Final");
		}
		if (!qSleftIsFinal) {
			for(Field field: query_subject.getFields()) {
				if (!field.getDimension().equals("")) {
					if (!field.getDimension().startsWith("[") && !field.getDimension().endsWith("]")) {
						/*
						String dim[] = StringUtils.split(field.getDimension(), ",");
						for (int i=0; i < dim.length; i++) {
							//un tableau pour les eventuelles multiples dimensions pour un champ de ref. 
							String dimensionTab = dim[i];
							if (dimensionTab.equals(dimension)) {
								
								if (!hierarchiesFields.containsKey("[DATA].[" + qsFinalName + "].[" + gDirName.substring(1) + field.getField_name() + "]")) {
									String orderFieldList[] = StringUtils.split(field.getOrder(), ",");
									
									for (int j=0; j < orderFieldList.length; j++) {
										String dimension_field[] = StringUtils.split(orderFieldList[j], "-");
										if (dimension_field[0].equals(dimension)) {
											//on ajoute le champ concerné dans le map des fields afin de determiner plus tard le nombre de hierarchies et les hierarchies
											String key = StringUtils.replace("[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]", "[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]", "[DATA].[" + query_subject.getTable_alias() + "].[" + gDirName.substring(1) + "." + field.getField_name() + "]") ;
											key = StringUtils.replace(key,"[" + query_subject.getTable_alias() + "]","[" + qsFinalName + "]");
											if (dimension_field.length > 1) {
												hierarchiesFields.put(key, dimension_field[1]);
											} else {
												hierarchiesFields.put(key, "");
											}
										}
									}
								}
							}
						}
						*/
						//multi_map
						List<Map<String, String>> fieldDimensions = field.getDimensions();
						for (Map<String, String> dimensionAttributes : fieldDimensions) {
							String attDimension = dimensionAttributes.get("dimension");
							String attOrder = dimensionAttributes.get("order");
							if (attDimension.equals(dimension)) {
								if (!hierarchiesFields.containsKey("[DATA].[" + qsFinalName + "].[" + gDirName.substring(1) + field.getField_name() + "]")) {
									//on ajoute le champ concerné dans le map des fields afin de determiner plus tard le nombre de hierarchies et les hierarchies
									String key = StringUtils.replace("[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]", "[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]", "[DATA].[" + query_subject.getTable_alias() + "].[" + gDirName.substring(1) + "." + field.getField_name() + "]") ;
									key = StringUtils.replace(key,"[" + query_subject.getTable_alias() + "]","[" + qsFinalName + "]");
									hierarchiesFields.put(key, attOrder);
								}
							}
						}
						//end multi map
					}
				}
			}
		}
		
		for(Relation rel: query_subject.getRelations()){
			
			if(rel.isRef() && (rel.getUsedForDimensions().equals("true") && !qSleftIsFinal) || (rel.getUsedForDimensions().equals(dimension) && qSleftIsFinal)){
				//seq
				String pkAlias = rel.getPktable_alias();

				String gDirNameCurrent = "";
				if(rel.getKey_type().equalsIgnoreCase("P") || rel.isNommageRep()){	
					gDirNameCurrent = gDirName + "." + pkAlias;
				}
				else {
					gDirNameCurrent = gDirName + "." + rel.getAbove();
				}
				
				scanRefFieldsDimensions(dimensions, dimension, pkAlias, qsFinalName + gDirNameCurrent, gDirNameCurrent, qsFinal, qsFinalName, copyRecurseCount, false);
			}
		}
	}
			
	protected static void createHierarchiesNb(Map<String, Map<String, String>> dimensions) {
		
		for(Entry<String, Map<String, String>> dimension: dimensions.entrySet()){
			if(!dimension.getKey().startsWith("Time Dimension")) {
				Map<String, String> dimensionFields = new HashMap<String, String>();
				Map<String, String> hierarchies = new HashMap<String, String>();
				dimensionFields.putAll(dimension.getValue());
				System.out.println("dimensionFields : " + dimensionFields);
				int i = 1;
				for(Entry<String, String> dimensionField: dimensionFields.entrySet()){
					if (dimensionField.getValue().equals("")) {
						String hierarchy = dimensionField.getKey();
						hierarchies.put(Integer.toString(i), hierarchy);
						i = createHierarchiesNbRecurs(dimensionFields, dimensionField.getKey(), hierarchies, i);
						i++;
					}
				}
				System.out.println("Hierarchies : " + hierarchies);
				dimensions.put(dimension.getKey(), hierarchies);
			}
		}
	}
	
	protected static int createHierarchiesNbRecurs (Map<String, String> dimensionFields, String previousField, Map<String, String> hierarchies, int i) {

		String hierarchyBase = hierarchies.get(Integer.toString(i));
		Boolean addHierarchy = false;
		for(Entry<String, String> dimensionField: dimensionFields.entrySet()){
			if (dimensionField.getValue().equals(previousField)) {
				if (addHierarchy) {i++;}
				String hierarchy = hierarchyBase + ";" + dimensionField.getKey();
				hierarchies.put(Integer.toString(i), hierarchy);
				i = createHierarchiesNbRecurs(dimensionFields, dimensionField.getKey(), hierarchies, i);
				addHierarchy = true;
				
			}
		}
		return i;
	}

	protected static void renameHierarchies(Map<String, Map<String, String>> dimensions){
		for(Entry<String, Map<String, String>> dimension: dimensions.entrySet()){
			if(!dimension.getKey().startsWith("Time Dimension")) {
				Map<String, String> hierarchies = new HashMap<String, String>();
				Map<String, String> namedHierarchies = new HashMap<String, String>();
				hierarchies.putAll(dimension.getValue());
				for(Entry<String, String> hierarchy: hierarchies.entrySet()){
					String hierarchyTab[] = StringUtils.split(hierarchy.getValue(), ";");
					if (hierarchyTab.length > 0) {
						String hierarchyNameExp[] = StringUtils.splitByWholeSeparator(hierarchyTab[hierarchyTab.length - 1], "].[");
						if (hierarchyNameExp.length > 2) {
							String hierarchyName = hierarchyNameExp[1] + "." + StringUtils.replace(hierarchyNameExp[2], "]", "");
							namedHierarchies.put(hierarchyName, hierarchy.getValue());
						}
					}
				}
				dimensions.put(dimension.getKey(), namedHierarchies);
			}
		}
	}
	
	protected static void buildDimensions(Map<String, Map<String, String>> dimensions, Map<String, Map<String, String>> measures){
		
		Map<String, String> dimensionScreenTip = new HashMap<String, String> ();
		Map<String, String> hierarchyScreenTip = new HashMap<String, String> ();
		Map<String, String> levelScreenTip = new HashMap<String, String> ();
		
		Map<String, String> scopesToDisable = new HashMap<String, String> ();
		Map<String, String> scopesToEnable = new HashMap<String, String> ();
		
		for(Entry<String, Map<String, String>> dimension: dimensions.entrySet()){

			if (dimension.getKey().startsWith("Time Dimension ")) {
				String path = dimension.getKey();
				path = StringUtils.replace(path, "Time Dimension ", "");
				String split[] = StringUtils.split(path, ".");
				String qiName = split[1];
				path = StringUtils.replace(path, ".", "].[");
				path = "[DATA].[" + path + "]";
				System.out.println("createTimeDimension : " + path + ", " + dimension.getKey() + ", " + qiName);
				fsvc.createTimeDimension(path, dimension.getKey(), qiName);
			} else 
			{
				fsvc.createDimension("[DIMENSIONAL]", dimension.getKey());			
			}
			
			// time dimension
//			String dateQueryItemPath = "[FINAL].[SDIDATA].[CREATEDT]";
//			String dateQueryItemName = "CREATEDT";
//			String dimensionName = "SDIDATA.CREATEDT";
			if (!dimension.getKey().startsWith("Time Dimension ")) {
				for (Entry<String, String> hierarchy: dimension.getValue().entrySet()) {
					
					fsvc.createEmptyNewHierarchy("[DIMENSIONAL].[" + dimension.getKey() + "]");
					fsvc.createEmptyHierarchy("[DIMENSIONAL].[" + dimension.getKey() + "]", hierarchy.getKey());
					
					String levels[] = StringUtils.split(hierarchy.getValue(), ";");
					
					for (int i = levels.length - 1; i >= 0; i--) {
						
						String name = "";
						String levelNameExp[] = StringUtils.splitByWholeSeparator(levels[i], "].[");
						if (levelNameExp.length > 2) {
							name = levelNameExp[1] + "." + StringUtils.replace(levelNameExp[2], "]", "");
						}
						
						String exp = levels[i];
						
						String expScope[] = StringUtils.split(exp, ".");
						String tableScope = StringUtils.replace(expScope[1], "[", "");
						tableScope = StringUtils.replace(tableScope, "]", "");
						fsvc.createEmptyHierarchyLevel("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "]", name);
						fsvc.createHierarchyLevelQueryItem("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]", name, exp);
						
						fsvc.createDimensionRole_MC("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "]");
						fsvc.createDimensionRole_MD("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "]");
						fsvc.createDimensionRole_BK("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "]");
						
						//screenTip QueryItem
						fsvc.createScreenTip("queryItem", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "].[" + name + "]", exp);
						
						//define scope
						for(Entry<String, Map<String, String>> measureDimension: measures.entrySet()){
							
							String measureDimensionQsTab[] = StringUtils.split(measureDimension.getKey(), ".");
							String measureDimensionQs = StringUtils.replace(measureDimensionQsTab[1], "[", "");
							measureDimensionQs = StringUtils.replace(measureDimensionQs, " Fact]", "");
							
							Boolean isHigher = isQsHigherThanMeasure(measureDimensionQs + "Final", tableScope + "Final");
													
							//System.out.println("isQsHigherThanMeasure(" + measureDimensionQs + "Final, " + tableScope + "Final" + ")");
							//System.out.println(isHigher);
							if (isHigher) {
								for (Entry<String, String> measure: measureDimension.getValue().entrySet()) {
									//scopesToEnable
									// key : measureDimensionPath ; measurePath ; hierarchyPath
									// value : dimensionPath ; level
									scopesToEnable.put(measureDimension.getKey() + ";" + measure.getValue() + ";" + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "]" + ";" + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]");
									fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]", "0");
									//System.out.println("adjustScopeRelationship( " + measureDimension.getKey() + ", " + measure.getValue() + ", " + "[DIMENSIONAL].[" + dimension.getKey() + "]" + ", " + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]");
								}
								//manage screenTip for dimension, hierarchy, level
								//level
								String measureDimensionName = StringUtils.replace(StringUtils.splitByWholeSeparator(measureDimension.getKey(), "].[")[1], "]", "");
								String lScreenTip = "";
								if (levelScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]")==null) {
									lScreenTip = measureDimensionName;
								} else {
									lScreenTip = levelScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]");
								}
								if (!lScreenTip.contains(measureDimensionName)) {
									lScreenTip = lScreenTip + ", " + measureDimensionName;
								}
								levelScreenTip.put("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + "]", lScreenTip);
								//hierarchy
								String hScreenTip = "";
								if (hierarchyScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "]")==null) {
									hScreenTip = measureDimensionName;
								} else {
									hScreenTip = hierarchyScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "]");
								}
								if (!hScreenTip.contains(measureDimensionName)) {
									hScreenTip = hScreenTip + ", " + measureDimensionName;
								}
								hierarchyScreenTip.put("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "]", hScreenTip);
								//dimension
								String dScreenTip = "";
								if (dimensionScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "]")==null) {
									dScreenTip = measureDimensionName;
								} else {
									dScreenTip = dimensionScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "]");
								}
								if (!dScreenTip.contains(measureDimensionName)) {
									dScreenTip = dScreenTip + ", " + measureDimensionName;
								}
								dimensionScreenTip.put("[DIMENSIONAL].[" + dimension.getKey() + "]", dScreenTip);
							} else {
								//disable scope
								for (Entry<String, String> measure: measureDimension.getValue().entrySet()) {
									String all = "";
									if (i == levels.length - 1) {
										all = "(All)";
										// key : measure dimensionPath - measurePath - levelPath
										// value : dimensionPath
										scopesToDisable.put(measureDimension.getKey() + ";" + measure.getValue() + ";" + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + all + "]", "[DIMENSIONAL].[" + dimension.getKey() + "]");
										//fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchy.getKey() + "].[" + name + all + "]", "1");
									}
								}
							}
						}
					}
				}
			}
		}
		for(Entry<String, Map<String, String>> dimension: dimensions.entrySet()){
			if (dimension.getKey().startsWith("Time Dimension ")) {
				String path = dimension.getKey();
				path = StringUtils.replace(path, "Time Dimension ", "");
				String split[] = StringUtils.split(path, ".");
				String qiName = split[1];
				String tableScope = split[0];
				
				for(Entry<String, Map<String, String>> measureDimension: measures.entrySet()){
					
					String measureDimensionQsTab[] = StringUtils.split(measureDimension.getKey(), ".");
					String measureDimensionQs = StringUtils.replace(measureDimensionQsTab[1], "[", "");
					measureDimensionQs = StringUtils.replace(measureDimensionQs, " Fact]", "");
					
					Boolean isHigher = isQsHigherThanMeasure(measureDimensionQs + "Final", tableScope + "Final");
					
					//System.out.println("isQsHigherThanMeasure(" + measureDimensionQs + "Final, " + tableScope + "Final" + ")");
					//System.out.println(isHigher);
					if (!isHigher) {
						for (Entry<String, String> measure: measureDimension.getValue().entrySet()) {
							fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + qiName + " (By month)].[" + qiName + " (By month)(All)]", "1");
							fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + qiName + " (By week)].[" + qiName + " (By week)(All)]", "1");
						}
					} else {
						
						//screenTip
						//dimension
						String measureDimensionName = StringUtils.replace(StringUtils.splitByWholeSeparator(measureDimension.getKey(), "].[")[1], "]", "");
						String dScreenTip = "";
						if (dimensionScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "]")==null) {
							dScreenTip = measureDimensionName;
						} else {
							dScreenTip = dimensionScreenTip.get("[DIMENSIONAL].[" + dimension.getKey() + "]");
						}
						if (!dScreenTip.contains(measureDimensionName)) {
							dScreenTip = dScreenTip + ", " + measureDimensionName;
						}
						dimensionScreenTip.put("[DIMENSIONAL].[" + dimension.getKey() + "]", dScreenTip);
						
					}
				}
			}
		}
		//set level screenTip
		for (Entry<String, String> level: levelScreenTip.entrySet()) {
			fsvc.createScreenTip("level", level.getKey(), level.getValue());
		}
		//set hierarchy screenTip
		for (Entry<String, String> hierarchy: hierarchyScreenTip.entrySet()) {
			fsvc.createScreenTip("hierarchy", hierarchy.getKey(), hierarchy.getValue());
		}
		//set dimension screenTip
		for (Entry<String, String> dimension: dimensionScreenTip.entrySet()) {
		fsvc.createScreenTip("dimension", dimension.getKey(), dimension.getValue());
		}
		//disable scope hierarchy after set scope
		for(Entry<String, String> scopeToDisable: scopesToDisable.entrySet()){
			String tab[] = StringUtils.split(scopeToDisable.getKey(), ";");
			String measureDimensionPath = tab[0];
			String measurePath = tab[1];
			String levelPath = tab[2];
			String dimensionPath = scopeToDisable.getValue();
			fsvc.adjustScopeRelationship(measureDimensionPath, measurePath, dimensionPath, levelPath, "1");
		}
		//Re - enable scope hierarchy after set scope, contourning possible FM Bug
				for(Entry<String, String> scopeToEnable: scopesToEnable.entrySet()){
					String tabKey[] = StringUtils.split(scopeToEnable.getKey(), ";");
					String tabValue[] = StringUtils.split(scopeToEnable.getValue(), ";");
					String measureDimensionPath = tabKey[0];
					String measurePath = tabKey[1];
					String dimensionPath = tabValue[0];
					String levelPath = tabValue[1];
					fsvc.adjustScopeRelationship(measureDimensionPath, measurePath, dimensionPath, levelPath, "0");
				}
	}
	
	protected static Boolean isQsHigherThanMeasure(String qsIDMeasure, String searchQsID) {
		QuerySubject qS = query_subjects.get(qsIDMeasure);
		
		if (qsIDMeasure.equals(searchQsID)) {
			return true;
		}
		
		for(Relation rel: qS.getRelations()){
			if(rel.isFin()){
				//System.out.println("recurs isQsHigherThanMeasure : " + rel.getPktable_alias() + "Final" + ", " + searchQsID);
				if (isQsHigherThanMeasure(rel.getPktable_alias() + "Final", searchQsID)) {
					return true;
				}
			}
		}		
		return false;
	}
	
	protected static void moveDimensions(String qsID, Map <String, Map<String, String>> dimensions) {

		QuerySubject query_subject = query_subjects.get(qsID);
		
		for(Field field: query_subject.getFields()) {
			if (!field.getMeasure().equals("")) {
				
				
				for(Entry<String, Map<String, String>> dimension: dimensions.entrySet()){
					
					String tableScope = "";
					Boolean move = true;
					
					if (dimension.getKey().startsWith("Time Dimension ")) {
						String path = dimension.getKey();
						path = StringUtils.replace(path, "Time Dimension ", "");
						String split[] = StringUtils.split(path, ".");
						tableScope = split[0];
						
						Boolean isHigher = isQsHigherThanMeasure(qsID, tableScope + "Final") || isQsHigherThanMeasure(qsID, tableScope + "Ref");
						if (!isHigher || !move) {
							move = false;
						}
					}
					
					for (Entry<String, String> hierarchies: dimension.getValue().entrySet()) {
		
						if (!dimension.getKey().startsWith("Time Dimension ")) {
							String levels[] = StringUtils.split(hierarchies.getValue(), ";");
							String lev[] = StringUtils.split(levels[levels.length - 1], "-");
							String exp = lev[1];
							String expScope[] = StringUtils.split(exp, ".");
							tableScope = StringUtils.replace(expScope[1], "[", "");
							tableScope = StringUtils.replace(tableScope, "]", "");
							
							Boolean isHigher = isQsHigherThanMeasure(qsID, tableScope + "Final") || isQsHigherThanMeasure(qsID, tableScope + "Ref");
							if (!isHigher || !move) {
								move = false;
							}
						}
					}	
					if (move) {
						fsvc.moveDimension("[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + query_subject.getTable_alias() + "]");
					}
				}
			}
		}
		
		for(Relation rel: query_subject.getRelations()){
			if(rel.isFin()){
				moveDimensions(rel.getPktable_alias() + "Final", dimensions);
			}
		}
	}
}
