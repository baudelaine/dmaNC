package com.dma.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class GetImportedKeysServlet
 */
@WebServlet("/SaveModel")
public class SaveModelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveModelServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		Map<String, Object> parms = Tools.fromJSON(request.getInputStream());
		
		String modelName = (String) parms.get("modelName");

		System.out.println("querySubjects=" + Tools.fromJSON(parms.get("data").toString()));
		
		@SuppressWarnings("unchecked")
		List<QuerySubject> querySubjects = (List<QuerySubject>) Tools.fromJSON(parms.get("data").toString(), new TypeReference<List<QuerySubject>>(){});		

		
		
		// Start mapping dimension, order, bk and hierarchName String to dimensions List<Map<String, String>>
		
//		for(QuerySubject querySubject: querySubjects) {
//
//			for(Field field: querySubject.getFields()){
//				List<Map<String, String>> dimensions = new ArrayList<Map<String, String>>();
//				List<String> dimensionList = Arrays.asList(field.getDimension().split(","));
//				List<String> orderList = Arrays.asList(field.getOrder().split(","));
//				List<String> bkList = Arrays.asList(field.getBK().split(","));
//				List<String> hierarchyNameList = Arrays.asList(field.getHierarchyName().split(","));
//
//				int index = 0;
//				for(String dimensionItem: dimensionList) {
//					if(!dimensionItem.isEmpty()) {
//						Map<String, String> dimension = new HashMap<String, String>();
//						dimension.put("dimension", dimensionItem);
//						dimension.put("order","");
//						dimension.put("bk","");
//						dimension.put("hierarchyName","");
//						
//						try {
//							dimension.put("order", orderList.get(index).split("-")[1]);
//						}
//						catch(ArrayIndexOutOfBoundsException e) {}
//						
//						try {
//							dimension.put("bk", bkList.get(index).split("-")[1]);
//						}
//						catch(ArrayIndexOutOfBoundsException e) {}
//						
//						try {
//							dimension.put("hierarchyName", hierarchyNameList.get(index).split("-")[1]);
//						}
//						catch(ArrayIndexOutOfBoundsException e) {}
//						
//						dimensions.add(dimension);
//					}
//					index++;
//				}
//				field.setDimensions(dimensions);
//			}
//		
//		}
		
		// End mapping dimension, order, bk and hierarchName String to dimensions List<Map<String, String>>
		
		// Start mapping label and description string to labels and description Map<String, String> 

//		String lang = ((String) request.getServletContext().getAttribute("cognosLocales"));
//		
//		if(lang != null && !lang.isEmpty()) {
//			lang = lang.split(",")[0].trim();
//			for(QuerySubject querySubject: querySubjects) {
//				
//				querySubject.getLabels().put(lang, querySubject.getLabel());
//				querySubject.getDescriptions().put(lang, querySubject.getDescription());
//
//				for(Field field: querySubject.getFields()){
//					field.getLabels().put(lang, field.getLabel());
//					field.getDescriptions().put(lang, field.getDescription());
//				}
//			
//			}
//			
//		}

		// Start mapping label and description string to labels and description Map<String, String> 
		
		
		Calendar c = Calendar.getInstance();
//		c.setTimeInMillis(System.currentTimeMillis());

		java.text.SimpleDateFormat mois = new java.text.SimpleDateFormat("MM");
		
		String date = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH);
		date = c.get(Calendar.YEAR) + "-" + mois.format(c.getTime()) + "-" + c.get(Calendar.DAY_OF_MONTH);
		String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);

		Path path = Paths.get((String) request.getSession().getAttribute("projectPath"));
		
		String fileName = path + "/models/" + modelName + "-" + date + "-" + time + ".json";
		Path output = Paths.get(fileName);

		
		
		Files.write(output, Tools.toJSON(querySubjects).getBytes());
		
		List<Object> result = new ArrayList<Object>();
		    
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

}

