package com.dma.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class GetTablesServlet
 */
@WebServlet("/GetDBMDFromCache")
public class GetDatabaseMetaDatasFromCache extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetDatabaseMetaDatasFromCache() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			Path path = Paths.get((String) request.getSession().getAttribute("projectPath") + "/dbmd.json");
			
			if(Files.exists(path)){
				
				System.out.println("Load Database Meta Datas from cache...");
				
				BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
				
				ObjectMapper mapper = new ObjectMapper();
		        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				result = mapper.readValue(br, new TypeReference<Map<String, Object>>(){});

				request.getSession().setAttribute("dbmd", result);
				
				if(br != null){br.close();}
			}
			    
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(Tools.toJSON(result));
				
			
		}
		catch (Exception e){
			e.printStackTrace(System.err);
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
