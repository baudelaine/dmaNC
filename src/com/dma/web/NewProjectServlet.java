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
import java.util.Calendar;
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
@WebServlet("/NewProject")
public class NewProjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewProjectServlet() {
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
		

		Project newProject = null;
		
		try{
			ObjectMapper mapper = new ObjectMapper();
	        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			newProject = mapper.readValue(request.getInputStream(), new TypeReference<Project>(){});
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

		Path wks = Paths.get(getServletContext().getRealPath("/datas") + "/" + user);
		Path projectPath = null;
		
		if(newProject != null){
			projectPath = Paths.get(wks + "/" + newProject.getName());
			if(Files.exists(projectPath)){
				result.put("STATUS", "KO");
				result.put("REASON", newProject.getName() + " already exists.");
//				result.put("TROUBLESHOOTING", "Choose a project name which does not exist already.");
				newProject = null;
			}
		}
			
		if(newProject != null){
			
			Calendar c = Calendar.getInstance();
//			c.setTimeInMillis(System.currentTimeMillis());

			java.text.SimpleDateFormat mois = new java.text.SimpleDateFormat("MM");
			
			String date = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH);
			date = c.get(Calendar.YEAR) + "-" + mois.format(c.getTime()) + "-" + c.get(Calendar.DAY_OF_MONTH);
			String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);
			
			newProject.setTimestamp(date + "-" + time);
			
			Map<String, Project> projects = new HashMap<String, Project>();
			projects.put(newProject.getName(), newProject);
			Path projectsFile = Paths.get(getServletContext().getRealPath("/datas") + "/" + user + "/projects.json");
			if(Files.exists(projectsFile)){
				BufferedReader br = null;
				Map<String, Project> existingProjects = null;
				try{
					br = new BufferedReader(new FileReader(projectsFile.toFile()));
					ObjectMapper mapper = new ObjectMapper();
			        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
					existingProjects = mapper.readValue(br, new TypeReference<Map<String, Project>>(){});
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
				if(existingProjects != null){
					projects.putAll(existingProjects);
				}
			}
			try{
				Files.write(projectsFile, Tools.toJSON(projects).getBytes());
			}
			catch(Exception e){
				result.put("STATUS", "KO");
				result.put("REASON", "Writing List<Project> object to " + projectsFile + " failed.");
	            result.put("EXCEPTION", e.getClass().getName());
	            result.put("MESSAGE", e.getMessage());
	            StringWriter sw = new StringWriter();
	            e.printStackTrace(new PrintWriter(sw));
	            result.put("STACKTRACE", sw.toString());
			}
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
				
				Files.createDirectories(projectPath);
				Files.setPosixFilePermissions(projectPath, perms);
				Path modelPath = Paths.get(projectPath + "/models");
				Files.createDirectories(modelPath);
				Files.setPosixFilePermissions(modelPath, perms);
				Path queriesPath = Paths.get(projectPath + "/queries");
				Files.createDirectories(queriesPath);
				Files.setPosixFilePermissions(queriesPath, perms);
				
				result.put("STATUS", "OK");
				result.put("PROJECT_PATH", projectPath.toString());
				result.put("PROJECTS", projects);
				request.getSession().setAttribute("currentProject", newProject);
				request.getSession().setAttribute("projectPath", projectPath.toString());
			}
			catch(Exception e){
				result.put("STATUS", "KO");
				result.put("REASON", "Creating " + projectPath + " failed.");
	            result.put("EXCEPTION", e.getClass().getName());
	            result.put("MESSAGE", e.getMessage());
	            StringWriter sw = new StringWriter();
	            e.printStackTrace(new PrintWriter(sw));
	            result.put("STACKTRACE", sw.toString());
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

