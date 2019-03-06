package com.dma.web;

import java.io.IOException;
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
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetTablesServlet
 */
@WebServlet("/GetPKRelations")
public class GetPKRelationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPKRelationsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String table = request.getParameter("table");
		String alias = request.getParameter("alias");
		String type = request.getParameter("type");
		boolean withRecCount = false;

		List<Object> result = new ArrayList<Object>();
		Map<String, Object> dbmd = null;
		
		Connection con = null;
		ResultSet rst = null;
		Statement stmt = null;
		DatabaseMetaData metaData = null;
		String schema = "";
		Map<String, String> tableAliases = null;


		try {
			
			con = (Connection) request.getSession().getAttribute("con");
			schema = (String) request.getSession().getAttribute("schema");
			dbmd = (Map<String, Object>) request.getSession().getAttribute("dbmd");
			withRecCount = (Boolean) request.getServletContext().getAttribute("withRecCount");
			tableAliases = (Map<String, String>) request.getSession().getAttribute("tableAliases");
			metaData = con.getMetaData();
			
		    Map<String, Relation> map = new HashMap<String, Relation>();
		    
			if(schema.equalsIgnoreCase("MAXIMO")) {
				Path path = Paths.get(request.getServletContext().getRealPath("res/maximo.json"));
				String relationsQuery = (String) Tools.fromJSON(path.toFile()).get("relationsQuery");
	    		stmt = con.createStatement();
	    		stmt.execute("SET SCHEMA " + schema);
	    		rst = stmt.executeQuery(relationsQuery.replace(";", "") + " WHERE OBJECTNAME = '" + table + "'");
	    	}
			else {
				rst = metaData.getExportedKeys(con.getCatalog(), schema, table);
			}
		    
		    while (rst.next()) {
		    	
		    	String key_name = rst.getString("FK_NAME");
		    	String fk_name = rst.getString("FK_NAME");
		    	String pk_name = rst.getString("PK_NAME");
		    	String key_seq = rst.getString("KEY_SEQ");
		    	String fkcolumn_name = rst.getString("FKCOLUMN_NAME");
		    	String pkcolumn_name = rst.getString("PKCOLUMN_NAME");
		        String fktable_name = rst.getString("FKTABLE_NAME");
		        String pktable_name = rst.getString("PKTABLE_NAME");
		        String _id = key_name + "P";
		        boolean isAlias = false;
		        
		        if(tableAliases != null){
			        if(tableAliases.containsKey(fktable_name)){
			        	fktable_name = tableAliases.get(fktable_name);
			        	isAlias = true;
			        }
	
			        if(tableAliases.containsKey(pktable_name)){
			        	pktable_name = tableAliases.get(pktable_name);
			        }
		        }
		        
		        // Jump to other key if fktable is an alias
		        if(isAlias){continue;}
		        
		        if(!map.containsKey(_id)){
		        	
		        	System.out.println("+++ add relation +++");
		        	
		        	Relation relation = new Relation();
		        	
		        	relation.set_id(_id);
		        	relation.setKey_name(key_name);
		        	relation.setFk_name(fk_name);
		        	relation.setPk_name(pk_name);
		        	relation.setTable_name(pktable_name);
		        	relation.setTable_alias(alias);
		        	relation.setPktable_name(fktable_name);
		        	relation.setPktable_alias(fktable_name);
		        	relation.setRelashionship("[" + type.toUpperCase() + "].[" + alias + "].[" + pkcolumn_name + "] = [" + fktable_name + "].[" + fkcolumn_name + "]");
		        	relation.setWhere(pktable_name + "." + pkcolumn_name + " = " + fktable_name + "." + fkcolumn_name);
		        	relation.setKey_type("P");
		        	relation.setType(type.toUpperCase());
		        	relation.set_id("PK_" + relation.getPktable_alias() + "_" + alias + "_" + type.toUpperCase());
		        	relation.setAbove(pkcolumn_name);
		        	
		        	String[] types = {"TABLE"};
		    		ResultSet rst0 = metaData.getTables(con.getCatalog(), schema, fktable_name, types);
		    		while (rst0.next()) {
		    	    	relation.setLabel(rst0.getString("REMARKS"));
		    	    }
		    		if(rst0 != null){rst0.close();}
		        	
		    		if(dbmd != null){
		    			Map<String, Object> o = (Map<String, Object>) dbmd.get(fktable_name);
		    			if(o != null){
			    			relation.setLabel((String) o.get("table_remarks"));
			    			relation.setDescription((String) o.get("table_description"));
		    			}
		    		}
		        	
		        	Seq seq = new Seq();
		        	seq.setTable_name(pktable_name);
		        	seq.setPktable_name(fktable_name);
		        	seq.setColumn_name(pkcolumn_name);
		        	seq.setPkcolumn_name(fkcolumn_name);
		        	seq.setKey_seq(Short.parseShort(key_seq));
		        	relation.addSeq(seq);
		        	
		        	map.put(_id, relation);

		        }
		        else{
		        	
		        	Relation relation = map.get(_id);
		        	if(!relation.getSeqs().isEmpty()){
			        	System.out.println("+++ update relation +++");
		        		Seq seq = new Seq();
			        	seq.setTable_name(pktable_name);
			        	seq.setPktable_name(fktable_name);
			        	seq.setColumn_name(pkcolumn_name);
			        	seq.setPkcolumn_name(fkcolumn_name);
			        	seq.setKey_seq(Short.parseShort(key_seq));
			        	
			        	relation.addSeq(seq);
			        	
			        	StringBuffer sb = new StringBuffer((String) relation.getRelationship());
			        	sb.append(" AND [" + type.toUpperCase() + "].[" + alias + "].[" + pkcolumn_name + "] = [" + fktable_name + "].[" + fkcolumn_name + "]");
			        	relation.setRelashionship(sb.toString());
			        	
			        	sb = new StringBuffer((String) relation.getWhere());
			        	sb.append(" AND " + fktable_name + "." + fkcolumn_name + " = " + pktable_name + "." + pkcolumn_name);
			        	relation.setWhere(sb.toString());
		        	}
		        	
		        }
	        	
		        	
		    }
		    
		    if(rst != null){rst.close();}
		    if (stmt != null) { stmt.close();}
		    
		    if(withRecCount){
		    	
	            long tableRecCount = 0;
	    		stmt = null;
	    		ResultSet rs = null;
	            try{
		    		String query = "SELECT COUNT(*) FROM ";
		    		if(!schema.isEmpty()){
		    			query += schema + ".";
		    		}
		    		query += table;
		    		
		    		stmt = con.createStatement();
		            rs = stmt.executeQuery(query);
		            while (rs.next()) {
		            	tableRecCount = rs.getLong(1);
		            }
			    }
	            catch(SQLException e){
	            	System.out.println("CATCHING SQLEXEPTION...");
	            	System.out.println(e.getSQLState());
	            	System.out.println(e.getMessage());
	            	
	            }
	            finally {
		            if (stmt != null) { stmt.close();}
		            if(rst != null){rst.close();}
					
				}
		    	
	            System.out.println("tableRecCount=" + tableRecCount);
		    	
		    	for(Entry<String, Relation> relation: map.entrySet()){
		    		Relation rel = relation.getValue();
		    		
		    		Set<String> tableSet = new HashSet<String>();
		    		for(Seq seq: rel.getSeqs()){
		    			if(!schema.isEmpty()){
			    			tableSet.add(schema + "." + seq.pktable_name);
			    			tableSet.add(schema + "." + seq.table_name);
		    			}
		    			else{
			    			tableSet.add(schema + seq.pktable_name);
			    			tableSet.add(schema + seq.table_name);
		    			}
		    		}
		    		
		    		System.out.println("tableSet=" + tableSet);
		    		
		    		StringBuffer sb = new StringBuffer();;
		    		
		    		for(String tbl: tableSet){
		    			sb.append(", " + tbl);
		    		}
		    		String tables = sb.toString().substring(1);
		    		
		            long recCount = 0;
		    		stmt = null;
		    		rs = null;
		            try{
			    		String query = "SELECT COUNT(*) FROM " + tables + " WHERE " + rel.where;
			    		System.out.println(query);
			    		stmt = con.createStatement();
			            rs = stmt.executeQuery(query);
			            while (rs.next()) {
			            	recCount = rs.getLong(1);
			            }
			            rel.setRecCount(recCount);
			    		float percent = (Math.round(((float)recCount / tableRecCount) * 100));
			            rel.setRecCountPercent((int) percent);
			            System.out.println("recCount=" + recCount);
			            System.out.println("percent=" + percent);
		            }
		            catch(SQLException e){
		            	System.out.println("CATCHING SQLEXEPTION...");
		            	System.out.println(e.getSQLState());
		            	System.out.println(e.getMessage());
		            	
		            }
		            finally {
			            if (stmt != null) { stmt.close();}
			            if(rst != null){rst.close();}
						
					}
		    		
		    	}
		    }		    
		    
		    result = new ArrayList<Object>(map.values());
			
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
