package com.dma.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
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
 * Servlet implementation class GetTablesServlet
 */
@WebServlet("/GetDataType")
public class GetDataTypeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetDataTypeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Connection con = null;
		ResultSet rst = null;
		List<Object> result = new ArrayList<Object>();

		try {

//			result.add(new HashMap<String, Object>().put("Empty", "Empty"));
			
			con = (Connection) request.getSession().getAttribute("con");
			
		    DatabaseMetaData metaData = con.getMetaData();
		    rst = metaData.getTypeInfo();	

		    
		    while (rst.next()) {
		    	String type_name = rst.getString("TYPE_NAME");
			    Map<String, Object> dataType = new HashMap<>();
			    dataType.put("value", type_name);
			    dataType.put("text", type_name);
			    result.add(dataType);
		    }		    
		    
		    rst.close();
		    
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
