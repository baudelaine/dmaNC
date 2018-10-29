package com.dma.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
 * Servlet implementation class GetImportedKeysServlet
 */
@WebServlet("/OpenProject")
public class OpenProjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OpenProjectServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String, Object>();

		String user = request.getUserPrincipal().getName();
		
		result.put("USER", user);
		result.put("JSESSIONID", request.getSession().getId());		
		
		Path wks = Paths.get(getServletContext().getRealPath("/datas") + "/" + user);

		Project project = null;
		Path projectPath = null;
		
		try{
			ObjectMapper mapper = new ObjectMapper();
	        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	        project = mapper.readValue(request.getInputStream(), new TypeReference<Project>(){});
		}
		catch(Exception e){
			result.put("STATUS", "KO");
			result.put("REASON", "Input is not a valid Project object");
            result.put("EXCEPTION", e.getClass().getName());
            result.put("MESSAGE", e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            result.put("STACKTRACE", sw.toString());
		}			
		
		if(project != null){
			projectPath = Paths.get(wks + "/" + project.getName());
			if(Files.exists(projectPath)){
				result.put("STATUS", "OK");
				result.put("PROJECT_PATH", projectPath.toString());
				result.put("PROJECTS", request.getSession().getAttribute("projects"));
				request.getSession().setAttribute("currentProject", project);
				request.getSession().setAttribute("projectPath", projectPath.toString());
			}
			else{
				result.put("STATUS", "KO");
				result.put("REASON", projectPath + " not found.");
			}
		}  
		
		
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

