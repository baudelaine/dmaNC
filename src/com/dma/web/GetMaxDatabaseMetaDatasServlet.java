package com.dma.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
 * Servlet implementation class GetTablesServlet
 */
@WebServlet("/GetMaxDBMD")
public class GetMaxDatabaseMetaDatasServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMaxDatabaseMetaDatasServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("resource")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Connection con = null;
		Map<String, Object> result = new HashMap<String, Object>();
		String schema = "";

		try {
			
			Path path = Paths.get((String) request.getSession().getAttribute("projectPath") + "/dbmd.json");
			
			if(Files.exists(path)){
				
				System.out.println("Load Database Meta Datas from cache...");
				
				BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
				
				ObjectMapper mapper = new ObjectMapper();
		        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				result = mapper.readValue(br, new TypeReference<Map<String, Object>>(){});

				request.getSession().setAttribute("dbmd", result);
				
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(Tools.toJSON(result));
				
				if(br != null){br.close();}
			}
			else{			
			
				con = (Connection) request.getSession().getAttribute("con");
				schema = (String) request.getSession().getAttribute("schema");
				
			    DatabaseMetaData metaData = con.getMetaData();
			    //String[] types = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
			    String[] types = {"TABLE"};
			    ResultSet rst0 = metaData.getTables(con.getCatalog(), schema, "%", types);	
	
			    
			    while (rst0.next()) {

			    	String table_name = rst0.getString("TABLE_NAME");
			    	
			    	ResultSet rst = null;
			    	Statement stmt = null;
			    	int FKSeqCount = 0;
			    	Set<String> FKSet = new HashSet<String>();
			    	
			    	if(schema.equalsIgnoreCase("MAXIMO")) {
						path = Paths.get(request.getServletContext().getRealPath("res/maximo.json"));
						String relationsQuery = (String) Tools.fromJSON(path.toFile()).get("relationsQuery");
			    		stmt = con.createStatement();
//			    		stmt.execute("SET SCHEMA " + schema);
			    		rst = stmt.executeQuery(relationsQuery.replace(";", "") + " WHERE TARGETOBJ = '" + table_name + "'");
			    	}
			    	else {
				    	rst = metaData.getImportedKeys(con.getCatalog(), schema, table_name);
			    	}
			    	while(rst.next()){
			    		String FKName = rst.getString("FK_NAME");
			    		FKSet.add(FKName);
			    		FKSeqCount++;
			    	}
		            if(rst != null){rst.close();}
		    		if(stmt != null){stmt.close();}

			    	int PKSeqCount = 0;
			    	Set<String> PKSet = new HashSet<String>();
			    	if(schema.equalsIgnoreCase("MAXIMO")) {
						path = Paths.get(request.getServletContext().getRealPath("res/maximo.json"));
						String relationsQuery = (String) Tools.fromJSON(path.toFile()).get("relationsQuery");
			    		stmt = con.createStatement();
//			    		stmt.execute("SET SCHEMA " + schema);
			    		rst = stmt.executeQuery(relationsQuery.replace(";", "") + " WHERE OBJECTNAME = '" + table_name + "'");
			    	}
			    	else {
			    		rst = metaData.getExportedKeys(con.getCatalog(), schema, table_name);
			    	}
			    	while(rst.next()){
			    		String PKName = rst.getString("FK_NAME");
			    		PKSet.add(PKName);
			    		PKSeqCount++;
			    	}
		            if(rst != null){rst.close();}
		    		if(stmt != null){stmt.close();}

				    rst = metaData.getPrimaryKeys(con.getCatalog(), schema, table_name);
				    Set<String> pks = new HashSet<String>();
				    
				    while (rst.next()) {
				    	pks.add(rst.getString("COLUMN_NAME"));
				    }
			        if(rst != null){rst.close();}

				    rst = metaData.getIndexInfo(con.getCatalog(), schema, table_name, false, true);
				    Set<String> indexes = new HashSet<String>();
				    
				    while (rst.next()) {
				    	indexes.add(rst.getString("COLUMN_NAME"));
				    }
			        if(rst != null){rst.close();}
			    	
			    	long recCount = 0;
		    		Statement stm = null;
		    		ResultSet rs = null;
		            try{
			    		String query = "SELECT COUNT(*) FROM ";
			    		if(!schema.isEmpty()){
			    			query += schema + ".";
			    		}
			    		query += table_name;
			    		stm = con.createStatement();
			            rs = stm.executeQuery(query);
			            while (rs.next()) {
			            	recCount = rs.getLong(1);
			            }
		            }
		            catch(SQLException e){
		            	System.out.println("CATCHING SQLEXEPTION...");
		            	System.out.println(e.getSQLState());
		            	System.out.println(e.getMessage());
		            	
		            }
		            finally {
			            if (stm != null) { stm.close();}
			            if(rs != null){rs.close();}
						
					}
			    	
			    	String table_type = rst0.getString("TABLE_TYPE");
			    	String table_remarks = rst0.getString("REMARKS");
			    	String table_schema = rst0.getString("TABLE_SCHEM");
			    	Map<String, Object> table = new HashMap<String, Object>();
			    	table.put("table_name", table_name);
			    	table.put("table_schema", table_schema);
			    	table.put("table_type", table_type);
			    	table.put("table_remarks", table_remarks);
			    	if(table_remarks == null) {table.put("table_remarks", "");}
			    	table.put("table_recCount", recCount);
			    	table.put("table_description", "");
		    		table.put("table_primaryKeyFieldsCount", pks.size());
		    		table.put("table_importedKeysCount", FKSet.size());
		    		table.put("table_importedKeysSeqCount", FKSeqCount);
		    		table.put("table_exportedKeysCount", PKSet.size());
		    		table.put("table_exportedKeysSeqCount", PKSeqCount);
		    		table.put("table_indexesCount", indexes.size());
		    		String stats = "(" + pks.size() + ") (" + FKSet.size() + ") (" + FKSeqCount + ") (" + PKSet.size() +
		    				") (" + PKSeqCount + ") (" +  indexes.size() + ") (" + recCount + ")";  
			    	table.put("table_stats", stats);
				    
				    ResultSet rst1 = metaData.getColumns(con.getCatalog(), schema, table_name, "%");
				    Map<String, Object> fields = new HashMap<String, Object>();
				    while(rst1.next()){
					    Map<String, Object> field = new HashMap<>();
				    	field.put("column_name", rst1.getString("COLUMN_NAME"));
				    	field.put("column_type", rst1.getString("TYPE_NAME"));
				    	field.put("column_remarks", rst1.getString("REMARKS"));
				    	if(rst1.getString("REMARKS") == null) {field.put("column_remarks", "");}
			        	field.put("column_size", rst1.getInt("COLUMN_SIZE"));
				    	field.put("column_description", "");
			        	if(pks.contains(rst1.getString("COLUMN_NAME"))){
			    			field.put("column_isPrimaryKey", true);
			    		}
			        	else{
			    			field.put("column_isPrimaryKey", false);
			        	}
			        	if(indexes.contains(rst1.getString("COLUMN_NAME"))){
			        		field.put("column_isIndexed", true);
			        	}
			        	else{
			        		field.put("column_isIndexed", false);
			        	}
				    	
			        	field.put("column_isNullable", rst1.getString("IS_NULLABLE"));
				    	field.put("column_isFiltered", false);
				    	field.put("filtered", false);
					    fields.put(rst1.getString("COLUMN_NAME"), field);
				    }
				    if(rst1 != null){rst1.close();}
				    table.put("columns", fields);
				    result.put(table_name, table);
				    
			    }		    
			    
			    if(rst0 != null){rst0.close();}
			    
				request.getSession().setAttribute("dbmd", result);
			    
			    response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(Tools.toJSON(result));
				
			}
			
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
