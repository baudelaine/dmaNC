package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Servlet implementation class GetImportedKeysServlet
 */
@WebServlet("/GetDimensions")
public class GetDimensionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetDimensionsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("SESSIONID", request.getSession().getId());		
		result.put("CLIENT", request.getRemoteAddr() + ":" + request.getRemotePort());
		result.put("SERVER", request.getLocalAddr() + ":" + request.getLocalPort());
		result.put("FROM", this.getServletName());
		
		try{

			Map<String, Object> parms = Tools.fromJSON(request.getInputStream());
			
			if(parms != null && parms.get("dimensions") != null && parms.get("qss") != null) {
			
				Map<String, QuerySubject> query_subjects = 
						(Map<String, QuerySubject>) Tools.fromJSON(parms.get("qss").toString(), new TypeReference<Map<String, QuerySubject>>(){});
				
				
				List<String> dims = (List<String>) Tools.fromJSON(parms.get("dimensions").toString(), new TypeReference<List<String>>(){});
				
				Map<String, Dimension> dimensions = new HashMap<String, Dimension>();
				for(String dim: dims) {
					Dimension dimension = new Dimension();
					dimension.setName(dim);
					
					for(Entry<String, QuerySubject> query_subject: query_subjects.entrySet()){
						
						if (query_subject.getValue().getType().equalsIgnoreCase("Final")){
							
							String qsAlias = query_subject.getValue().getTable_alias();  // table de gauche, celle ou tu es actuellement
							String gDirName = ""; // prefix qu'on cherche, il vaut cher
							String qsFinalName = query_subject.getValue().getTable_alias();   //CONSTANTE, nom du QS final auquel l'arbre ref est accroché, le tronc, on peut le connaitre à tout moment de f1
							String qSleftType = "Final";
							
							dimension.getDimensionDetails().setQsFinalName(qsFinalName);
					    	List<String> orders = dimension.getDimensionDetails().getOrders();
					    	List<String> BKs = dimension.getDimensionDetails().getBKs();							
							
							for(Field field: query_subjects.get(qsFinalName + qSleftType).getFields()){
							    if (field.getDimension().equals(dimension.getName())) {
							    	orders.add(field.getField_name());
							    }
							    BKs.add(field.getField_name());
							}
							
							
							Tools.recurse0(qsAlias, gDirName, qsFinalName, qSleftType, dimension, query_subjects);
																	
						}
						
					}				
					
					
					dimensions.put(dim, dimension);
				}
				
				result.put("STATUS", "OK");
				result.put("DATA", dimensions);
			
			}
			else {
			
				result.put("STATUS", "KO");
				result.put("ANSWER", "Input parameters are not valid.");
				throw new Exception();
			}
			
		}
		catch(Exception e){
			result.put("STATUS", "KO");
            result.put("EXCEPTION", e.getClass().getName());
            result.put("MESSAGE", e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            result.put("STACKTRACE", sw.toString());
            e.printStackTrace(System.err);
		}			
		
		finally {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(Tools.toJSON(result));
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	

}

