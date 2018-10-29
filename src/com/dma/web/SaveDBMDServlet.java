package com.dma.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetImportedKeysServlet
 */
@WebServlet("/SaveDBMD")
public class SaveDBMDServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveDBMDServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			Path path = Paths.get(request.getSession().getAttribute("projectPath") + "/dbmd.json");
			
			File file = path.toFile();
			if(!file.exists()){file.createNewFile();}
			file.setReadable(true, false);
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(path.toFile()));
		
			@SuppressWarnings("unchecked")
			Map<String, Object> dbmd = (Map<String, Object>) request.getSession().getAttribute("dbmd");
	
			bw.write(Tools.toJSON(dbmd));
			bw.flush();
			bw.close();		
			
			result.put("STATUS", "OK");
		}
		catch(Exception e) {
			result.put("STATUS", "KO");
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

