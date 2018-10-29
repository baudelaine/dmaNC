package com.dma.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
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
@WebServlet("/SaveQueries")
public class SaveQueriesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveQueriesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		Map<String, Object> parms = Tools.fromJSON(request.getInputStream());
		
		String modelName = (String) parms.get("backupName");

		Calendar c = Calendar.getInstance();
//		c.setTimeInMillis(System.currentTimeMillis());

		java.text.SimpleDateFormat mois = new java.text.SimpleDateFormat("MM");
		
		String date = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH);
		date = c.get(Calendar.YEAR) + "-" + mois.format(c.getTime()) + "-" + c.get(Calendar.DAY_OF_MONTH);
		String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);

		Path path = Paths.get((String) request.getSession().getAttribute("projectPath"));

		String fileName = path + "/queries/" + modelName + "-" + date + "-" + time + ".json";
		System.out.println("fileName=" + fileName);
		Path output = Paths.get(fileName);

		Files.write(output, Tools.toJSON(parms.get("data")).getBytes());
		
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

