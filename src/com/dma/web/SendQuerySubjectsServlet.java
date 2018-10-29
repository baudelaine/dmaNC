package com.dma.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dma.svc.CognosSVC;
import com.dma.svc.FactorySVC;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class GetSelectionsServlet
 */
@WebServlet(name = "SendQuerySubjects", urlPatterns = { "/SendQuerySubjects" })
public class SendQuerySubjectsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Map<String, Integer> gRefMap;
	List<RelationShip> rsList;
	Map<String, QuerySubject> query_subjects;
	Map<String, String> labelMap;
	Map<String, String> toolTipMap;
	Map<String, String> filterMap;
	Map<String, String> filterMapApply;
	List<QuerySubject> qsList = null;
	CognosSVC csvc;
	FactorySVC fsvc;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendQuerySubjectsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		Map<String, Object> parms = Tools.fromJSON(request.getInputStream());
		
		String projectName = (String) parms.get("projectName");
		String data = (String) parms.get("data");
		
		ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        qsList = Arrays.asList(mapper.readValue(data, QuerySubject[].class));
        
        query_subjects = new HashMap<String, QuerySubject>();
        Map<String, Integer> recurseCount = new HashMap<String, Integer>();
        
        for(QuerySubject qs: qsList){
        	query_subjects.put(qs.get_id(), qs);
        	recurseCount.put(qs.getTable_alias(), 0);
        }
        
        request.getSession().setAttribute("query_subjects", query_subjects);
        
		query_subjects = (Map<String, QuerySubject>) request.getSession().getAttribute("query_subjects");
		
		System.out.println("query_subjects.size=" + query_subjects.size());
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		// START SETUP COGNOS ENVIRONMENT
		
		Path cognosModelsPath = Paths.get((String) request.getServletContext().getAttribute("cognosModelsPath"));
		if(!Files.isWritable(cognosModelsPath)){
			result.put("status", "KO");
			result.put("message", "cognosModelsPath '" + cognosModelsPath + "' not writeable." );
			result.put("troubleshooting", "Check that '" + cognosModelsPath + "' exists on server and is writable.");
		}
		
		Path projectPath = Paths.get(cognosModelsPath + "/" + projectName);
		
		if(Files.exists(projectPath)){
			Files.walk(Paths.get(projectPath.toString()))
            .map(Path::toFile)
            .sorted((o1, o2) -> -o1.compareTo(o2))
            .forEach(File::delete);
		}
		
//		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
//		FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(perms);

		Set<PosixFilePermission> perms = new HashSet<>();
	    perms.add(PosixFilePermission.OWNER_READ);
	    perms.add(PosixFilePermission.OWNER_WRITE);
	    perms.add(PosixFilePermission.OWNER_EXECUTE);

	    perms.add(PosixFilePermission.OTHERS_READ);
	    perms.add(PosixFilePermission.OTHERS_WRITE);
	    perms.add(PosixFilePermission.OTHERS_EXECUTE);

	    perms.add(PosixFilePermission.GROUP_READ);
	    perms.add(PosixFilePermission.GROUP_WRITE);
	    perms.add(PosixFilePermission.GROUP_EXECUTE);		
		
		Files.createDirectories(projectPath);
		Files.setPosixFilePermissions(projectPath, perms);
		
		Path zip = Paths.get(getServletContext().getRealPath("/res/model.zip"));
		if(!Files.exists(zip)){
			result.put("status", "KO");
			result.put("message", "Generic model '" + zip + "' not found." );
			result.put("troubleshooting", "Check that '" + zip + "' exists on server.");
		}
		
		
		BufferedOutputStream dest = null;
		int BUFFER = Long.bitCount(Files.size(zip));
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(Files.newInputStream(zip))); 
		ZipEntry entry;
		while((entry = zis.getNextEntry()) != null) {
			System.out.println("Extracting: " + entry);
            int count;
            byte datas[] = new byte[BUFFER];
            // write the files to the disk
            FileOutputStream fos = new FileOutputStream(projectPath + "/" + entry.getName());
            dest = new BufferedOutputStream(fos, BUFFER);
            while ((count = zis.read(datas, 0, BUFFER)) 
              != -1) {
               dest.write(datas, 0, count);
            }
            dest.flush();
            dest.close();
        }
		zis.close();
		
		Path cpf = Paths.get(projectPath + "/model.cpf");
		Path renamedCpf = Paths.get(projectPath + "/" + projectName + ".cpf"); 
		if(Files.exists(cpf)){
			Files.move(cpf, renamedCpf);
			result.put("status", "OK");
			result.put("message", renamedCpf + " found in " + projectPath + ".");
			result.put("troubleshooting", "");
		}

		String dBEngine = (String) request.getSession().getAttribute("dbEngine");
		String cognosFolder = (String) request.getServletContext().getAttribute("cognosFolder");
		String cognosDispatcher = (String) request.getServletContext().getAttribute("cognosDispatcher");
		String cognosLogin = (String) request.getServletContext().getAttribute("cognosLogin");
		String cognosPassword = (String) request.getServletContext().getAttribute("cognosPassword");
		String cognosNamespace = (String) request.getServletContext().getAttribute("cognosNamespace");
		String pathToXML = getServletContext().getRealPath("/") + "/res/templates";

		if(!Files.exists(Paths.get(pathToXML))){
			result.put("status", "KO");
			result.put("message", "PathToXML " + pathToXML + " not found." );
			result.put("troubleshooting", "Check that '" + pathToXML + "' exists on server and contains XML templates.");
		}
		
		perms = new HashSet<>();
	    perms.add(PosixFilePermission.OWNER_READ);
	    perms.add(PosixFilePermission.OWNER_WRITE);
	    perms.add(PosixFilePermission.OWNER_EXECUTE);

	    perms.add(PosixFilePermission.OTHERS_READ);
	    perms.add(PosixFilePermission.OTHERS_WRITE);
	    perms.add(PosixFilePermission.OTHERS_EXECUTE);

	    perms.add(PosixFilePermission.GROUP_READ);
	    perms.add(PosixFilePermission.GROUP_WRITE);
	    perms.add(PosixFilePermission.GROUP_EXECUTE);		
		
		try {
			DirectoryStream<Path> ds = Files.newDirectoryStream(projectPath);
			for(Path path: ds){
//				System.out.println(path.toString());
				Files.setPosixFilePermissions(path, perms);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		// END SETUP COGNOS ENVIRONMENT

		if(((String) result.get("status")).equalsIgnoreCase("OK")){

			try{
				
		        //start();
				String cognosDataSource = (String) request.getSession().getAttribute("cognosDataSource");
				String cognosCatalog = (String) request.getSession().getAttribute("cognosCatalog");
				String cognosSchema = (String) request.getSession().getAttribute("cognosSchema");
				String cognosDefaultLocale = (String) request.getServletContext().getAttribute("cognosDefaultLocale");
				String cognosLocales = (String) request.getServletContext().getAttribute("cognosLocales");
				System.out.println("cognosLocales=" + cognosLocales);

				csvc = new CognosSVC(cognosDispatcher);
				csvc.setPathToXML(pathToXML);
				fsvc = new FactorySVC(csvc);
				csvc.logon(cognosLogin, cognosPassword, cognosNamespace);
				String modelName = projectName;
				csvc.openModel(modelName, cognosFolder);
				fsvc.setLocale(cognosDefaultLocale);
				
				//IICInitNameSpace();
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
				
				//Import();
				fsvc.DBImport("PHYSICAL", cognosDataSource, cognosCatalog, cognosSchema, dBEngine);
				
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
						}
					}
				}
				
				createMeasures("", qSIDStart, true, measures);
				Map<String, Map<String, String>> dimensions = new HashMap<String, Map<String, String>>();
				createDimensions(qSIDStart, dimensions);

				System.out.println(dimensions);
				
				for(Entry<String, Map<String, String>> map: dimensions.entrySet()){
					createHierarchies(qSIDStart, map.getKey(), "", dimensions);
				}
				
				buildDimensions(dimensions, measures);
				
				moveDimensions(qSIDStart, dimensions);
				
				System.out.println(dimensions);
				
		// end multidimensional
				
				fsvc.addLocale(cognosLocales, cognosDefaultLocale);
	
		/*
				for(Entry<String, String> map: labelMap.entrySet()){
					System.out.println(map.getKey() + " * * * * * " + map.getValue());
				}
		*/		
				
				
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
					
					Charset charset = StandardCharsets.UTF_8;
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
	
					try {
		
						datas = document.asXML();
	
						datas = StringUtils.replace(datas, outputSearch, outputReplace);
						Files.write(output, datas.getBytes());
	
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// fin test writer
					
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("END XML MODIFICATION");
				
				//publication
				System.out.println("Create and Publish Package");	
				
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
//				fsvc.createPackage(modelName, modelName, modelName, locales);
//				fsvc.publishPackage(modelName,"/content");
				
				csvc.executeAllActions();
				
				csvc.saveModel();
				csvc.closeModel();
				csvc.logoff();
				
				
				System.out.println("Model Generation Finished");

				result.put("status", "OK");
				result.put("message", projectName + " published sucessfully.");
				result.put("troubleshooting", "");
				
				}
				catch(Exception e){
					e.printStackTrace(System.err);
				}
			
			}
			
			//response to the browser
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(Tools.toJSON(result));
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	protected void f1(String qsAlias, String qsAliasInc, String gDirName, String qsFinal, String qsFinalName, Map<String, Integer> recurseCount, Boolean qSleftIsFinal) {
		
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
						gFieldName = rel.getSeqs().get(0).getColumn_name();
						gDirNameCurrent = "." + rel.getSeqs().get(0).getColumn_name();
					} else {
						gFieldName = gDirName.substring(1) + "." + rel.getSeqs().get(0).getColumn_name();
						gDirNameCurrent = gDirName + "." + rel.getSeqs().get(0).getColumn_name();
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
					gFieldNameReorder = rel.getSeqs().get(0).getColumn_name();
				} else {
					gFieldNameReorder = gDirName.substring(1) + "." + rel.getSeqs().get(0).getColumn_name();
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

	protected void f2(String qsAlias, String qsAliasInc, String gDirName, String qsInitialName, Map<String, Integer> recurseCount, String leftQSType){
		
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
					gDirNameCurrent = gDirName + sec + "." + rel.getSeqs().get(0).getColumn_name();
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
	
	protected void createMeasures(String PreviousQSID, String qsID, Boolean first, Map<String, Map<String, String>> mm) {

		QuerySubject query_subject = query_subjects.get(qsID);
		QuerySubject query_subject_prev = query_subjects.get(PreviousQSID);
		
		Boolean folderCreated = false;
		for(Field field: query_subject.getFields()) {
			
			if ((!field.getMeasure().equals(""))) {
				
				if (!folderCreated) {
					if (first) {
						fsvc.createFolder("[DIMENSIONAL]", query_subject.getTable_alias());
						first = false;
					} else {
						fsvc.createFolderInFolder("[DIMENSIONAL]", query_subject_prev.getTable_alias(), query_subject.getTable_alias());
					}
				folderCreated = true;
				fsvc.createMeasureDimension("[DIMENSIONAL].[" + query_subject.getTable_alias() + "]", query_subject.getTable_alias() + " Fact");
				Map<String, String> m = new HashMap<String, String>();
				mm.put("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact]", m);
				}
			fsvc.createMeasure("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact" + "]", field.getField_name(), "[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]");
			fsvc.modify("measure/regularAggregate", "/O/regularAggregate[0]/O/[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact" + "].[" + field.getField_name() + "]", field.getMeasure().toLowerCase());
			Map<String, String> m = mm.get("[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact]");
			m.put(field.getField_name(), "[DIMENSIONAL].[" + query_subject.getTable_alias() + " Fact" + "].[" + field.getField_name() + "]");
			}
		}
		for(Relation rel: query_subject.getRelations()){
			if(rel.isFin()){
				createMeasures(qsID, rel.getPktable_alias() + "Final", first, mm);
			}
		}
	}
	
	protected void createDimensions(String qsID, Map<String, Map<String, String>> dimensions) {
		
		QuerySubject query_subject = query_subjects.get(qsID);
		
		for(Field field: query_subject.getFields()) {
			if (!field.getDimension().equals("")) {
//				fsvc.createDimension("[DIMENSIONAL].[" + factQs + "]", query_subject.getTable_alias() + " Dimension");		
				if (!dimensions.containsKey(field.getDimension())) {
					Map<String, String> hierarchies = new HashMap<String, String>();
					if (!field.getDimension().equals("Time")) {
						dimensions.put(field.getDimension(), hierarchies);
					} else {
						// create time dimension
						dimensions.put("Time Dimension " + query_subject.getTable_alias() + "." + field.getField_name(), hierarchies);
					}
				} 
			}
		}
		for(Relation rel: query_subject.getRelations()){
			if(rel.isFin()){
				createDimensions(rel.getPktable_alias() + "Final", dimensions);
			}
		}
	}
	
	protected Boolean isDimensionExistHigher(String qsID, String dimension, Boolean testFields) {
		QuerySubject qS = query_subjects.get(qsID);
		
		if (testFields) {
			for(Field field: qS.getFields()) {
				if (dimension.equals(field.getDimension())) {
					return true;
				}
			}
		}
		for(Relation rel: qS.getRelations()){
			if(rel.isFin()){
				if(isDimensionExistHigher(rel.getPktable_alias() + "Final", dimension, true)) {
					return true;
				}
			}
		}		
		return false;
	}
	
	protected void createHierarchies(String qsID, String dimension, String hierarchyString, Map<String, Map<String, String>> dimensions) {
		
		QuerySubject query_subject = query_subjects.get(qsID);
		
		int i = 0;
		int count = 0;
		String hierarchyStringGo = "";
		Boolean isDimensionHigher = isDimensionExistHigher(qsID, dimension, false);
		
		for(Field field: query_subject.getFields()) {
			if (dimension.equals(field.getDimension())) {
			count++;	
			}
		}
		
		for(Field field: query_subject.getFields()) {
			if (dimension.equals(field.getDimension())) {
				i++;
				if (i == 1) {
					hierarchyStringGo = hierarchyString + field.getField_name() + "-" + "[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]" + ";";
				}
				hierarchyString = hierarchyString + field.getField_name() + "-" + "[DATA].[" + query_subject.getTable_alias() + "].[" + field.getField_name() + "]" + ";";
				
				if (i == count) {
					if (i > 1 && isDimensionHigher) {
						Map<String, String> hierarchies = dimensions.get(dimension);
						hierarchies.put(query_subject.getTable_alias() + "." + field.getField_name(), hierarchyString);
						System.out.println("Final : hierarchyString : " + hierarchyString);
					}
					else if(!isDimensionHigher) {
						Map<String, String> hierarchies = dimensions.get(dimension);
						hierarchies.put(query_subject.getTable_alias() + "." + field.getField_name(), hierarchyString);
						System.out.println("Final : hierarchyString : " + hierarchyString);
					}
				}		
			}
		}
		 
		for(Relation rel: query_subject.getRelations()){
			if(rel.isFin()){
				createHierarchies(rel.getPktable_alias() + "Final", dimension, hierarchyStringGo, dimensions);
//				System.out.println("return : hierarchyString : " + hierarchyString);
			}
		}
	}
	
	protected void buildDimensions(Map<String, Map<String, String>> dimensions, Map<String, Map<String, String>> measures){
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
			
			for (Entry<String, String> hierarchies: dimension.getValue().entrySet()) {
				
				fsvc.createEmptyNewHierarchy("[DIMENSIONAL].[" + dimension.getKey() + "]");
				fsvc.createEmptyHierarchy("[DIMENSIONAL].[" + dimension.getKey() + "]", hierarchies.getKey());
				
				String levels[] = StringUtils.split(hierarchies.getValue(), ";");
				
				for (int i = levels.length - 1; i >= 0; i--) {
					String lev[] = StringUtils.split(levels[i], "-");
					String name = lev[0];
					String exp = lev[1];
					String expScope[] = StringUtils.split(exp, ".");
					String tableScope = StringUtils.replace(expScope[1], "[", "");
					tableScope = StringUtils.replace(tableScope, "]", "");
					fsvc.createEmptyHierarchyLevel("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchies.getKey() + "]", name);
					fsvc.createHierarchyLevelQueryItem("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchies.getKey() + "].[" + name + "]", name, exp);
					
					fsvc.createDimensionRole_MC("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchies.getKey() + "].[" + name + "].[" + name + "]");
					fsvc.createDimensionRole_MD("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchies.getKey() + "].[" + name + "].[" + name + "]");
					fsvc.createDimensionRole_BK("[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchies.getKey() + "].[" + name + "].[" + name + "]");
					
					//define scope
					for(Entry<String, Map<String, String>> measureDimension: measures.entrySet()){
						
						String measureDimensionQsTab[] = StringUtils.split(measureDimension.getKey(), ".");
						String measureDimensionQs = StringUtils.replace(measureDimensionQsTab[1], "[", "");
						measureDimensionQs = StringUtils.replace(measureDimensionQs, " Fact]", "");
						
						Boolean isHigher = isQsHigherThanMeasure(measureDimensionQs + "Final", tableScope + "Final");
												
						System.out.println("isQsHigherThanMeasure(" + measureDimensionQs + "Final, " + tableScope + "Final" + ")");
						System.out.println(isHigher);
						if (isHigher) {
							for (Entry<String, String> measure: measureDimension.getValue().entrySet()) {
								fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchies.getKey() + "].[" + name + "]", "0");
								System.out.println("adjustScopeRelationship( " + measureDimension.getKey() + ", " + measure.getValue() + ", " + "[DIMENSIONAL].[" + dimension.getKey() + "]" + ", " + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + hierarchies.getKey() + "].[" + name + "]");
							}
//							fsvc.moveDimension("[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + measureDimensionQs + "]");
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
	//			path = StringUtils.replace(path, ".", "].[");
	//			path = "[DATA].[" + path + "]";
				
				for(Entry<String, Map<String, String>> measureDimension: measures.entrySet()){
					
					String measureDimensionQsTab[] = StringUtils.split(measureDimension.getKey(), ".");
					String measureDimensionQs = StringUtils.replace(measureDimensionQsTab[1], "[", "");
					measureDimensionQs = StringUtils.replace(measureDimensionQs, " Fact]", "");
					
					Boolean isHigher = isQsHigherThanMeasure(measureDimensionQs + "Final", tableScope + "Final");
					
					System.out.println("isQsHigherThanMeasure(" + measureDimensionQs + "Final, " + tableScope + "Final" + ")");
					System.out.println(isHigher);
					if (!isHigher) {
						for (Entry<String, String> measure: measureDimension.getValue().entrySet()) {
							fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + qiName + " (By month)].[" + qiName + " (By month)(All)]", "1");
							System.out.println("adjustScopeRelationship( " + measureDimension.getKey() + ", " + measure.getValue() + ", " + "[DIMENSIONAL].[" + dimension.getKey() + "]" + ", " + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + qiName + " (By month)].[" + qiName + " (By month)(All)]");
							fsvc.adjustScopeRelationship(measureDimension.getKey(), measure.getValue(), "[DIMENSIONAL].[" + dimension.getKey() + "]", "[DIMENSIONAL].[" + dimension.getKey() + "].[" + qiName + " (By week)].[" + qiName + " (By week)(All)]", "1");
							System.out.println("adjustScopeRelationship( " + measureDimension.getKey() + ", " + measure.getValue() + ", " + "[DIMENSIONAL].[" + dimension.getKey() + "]" + ", " + "[DIMENSIONAL].[" + dimension.getKey() + "].[" + qiName + " (By week)].[" + qiName + " (By week)(All)]");						
						}
					}
				}
			}
		}
	}
	
	protected Boolean isQsHigherThanMeasure(String qsIDMeasure, String searchQsID) {
		QuerySubject qS = query_subjects.get(qsIDMeasure);
		
		if (qsIDMeasure.equals(searchQsID)) {
			return true;
		}
		
		for(Relation rel: qS.getRelations()){
			if(rel.isFin()){
				System.out.println("recurs isQsHigherThanMeasure : " + rel.getPktable_alias() + "Final" + ", " + searchQsID);
				if (isQsHigherThanMeasure(rel.getPktable_alias() + "Final", searchQsID)) {
					return true;
				}
			}
		}		
		return false;
	}
	
	protected void moveDimensions(String qsID, Map <String, Map<String, String>> dimensions) {

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
						
						Boolean isHigher = isQsHigherThanMeasure(qsID, tableScope + "Final");
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
							
							Boolean isHigher = isQsHigherThanMeasure(qsID, tableScope + "Final");
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
