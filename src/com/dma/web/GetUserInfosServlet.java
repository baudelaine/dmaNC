package com.dma.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
@WebServlet("/GetUserInfos")
public class GetUserInfosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserInfosServlet() {
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

		if(!Files.exists(wks)){
			try{
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
				
				Files.createDirectories(wks);
				Files.setPosixFilePermissions(wks, perms);
			}
			catch(Exception e){
				result.put("STATUS", "KO");
				result.put("REASON", "Creating " + wks + " failed.");
	            result.put("EXCEPTION", e.getClass().getName());
	            result.put("MESSAGE", e.getMessage());
	            StringWriter sw = new StringWriter();
	            e.printStackTrace(new PrintWriter(sw));
	            result.put("STACKTRACE", sw.toString());
			}
		}
		else{
			Path projectsFile = Paths.get(getServletContext().getRealPath("/datas") + "/" + user + "/projects.json");
			if(Files.exists(projectsFile)){
				BufferedReader br = null;
				try{
					br = new BufferedReader(new FileReader(projectsFile.toFile()));
					ObjectMapper mapper = new ObjectMapper();
			        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
					Map<String, Project> projects = mapper.readValue(br, new TypeReference<Map<String, Project>>(){});
					result.put("STATUS", "OK");
					result.put("PROJECTS", projects);
					request.getSession().setAttribute("projects", projects);
					result.put("WKS", wks.toString());
				}
				catch(Exception e){
					result.put("STATUS", "KO");
					result.put("REASON", projectsFile + " is not a valid Map<String, Project> object");
		            result.put("EXCEPTION", e.getClass().getName());
		            result.put("MESSAGE", e.getMessage());
		            StringWriter sw = new StringWriter();
		            e.printStackTrace(new PrintWriter(sw));
		            result.put("STACKTRACE", sw.toString());
				}
				finally {
					if(br != null){br.close();}				
				}
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

