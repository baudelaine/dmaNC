package com.dma.action;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException;

public class Test7 {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub

		String schema = "DB2INST1";
		String lang = "fr";
		
		try{
			Class.forName("com.ibm.db2.jcc.DB2Driver");
		}
		catch(ClassNotFoundException cnfe){
			cnfe.printStackTrace(System.err);
		}
		
		System.out.println("DB2 driver loaded.");
		Connection conn = DriverManager.getConnection("jdbc:db2://localhost:50000/SAMPLE", "db2inst1", "spcspc");
		PreparedStatement stmt = conn.prepareStatement("select current date from sysibm.sysdummy1");
		ResultSet rst = stmt.executeQuery();
		rst.next();
		System.out.println(rst.getString(1));
		rst.close();
		if(stmt != null) {stmt.close();}
		
	    DatabaseMetaData metaData = conn.getMetaData();
	    //String[] types = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
	    String[] types = {"TABLE"};
	    ResultSet rst0 = metaData.getTables(conn.getCatalog(), schema, "%", types);	
		
	    while (rst0.next()) {

	    	String table_name = (rst0.getString("TABLE_NAME"));
	    	System.out.println(table_name);
	    	String sql = "INSERT INTO DB2INST1.TABLELABELS (TABLE,LANG,LABEL,DESCRIPTION) VALUES(?, ?, ?, ?)";
	    	stmt = conn.prepareStatement(sql);
	    	stmt.setString(1, table_name);
	    	stmt.setString(2, lang);
	    	stmt.setString(3, "Ce champs est le label " + lang + " de la table " + table_name);
	    	stmt.setString(4, "Ce champs est la description " + lang + " de la table " + table_name);
	    	try {
	    		stmt.execute();
		    }
			catch(SqlIntegrityConstraintViolationException e){
				System.out.println("Record already exists");
			}	    	
	    	if(stmt != null) {stmt.close();}

		    ResultSet rst1 = metaData.getColumns(conn.getCatalog(), schema, table_name, "%");
		    while(rst1.next()){
		    	String column_name =  rst1.getString("COLUMN_NAME");
		    	System.out.println("-- " + column_name);
		    	sql = "INSERT INTO DB2INST1.COLUMNLABELS (TABLE,COLUMN,LANG,LABEL,DESCRIPTION) VALUES(?, ?, ?, ?, ?)";
		    	stmt = conn.prepareStatement(sql);
		    	stmt.setString(1, table_name);
		    	stmt.setString(2, column_name);
		    	stmt.setString(3, lang);
		    	stmt.setString(4, "Ce champs est le label " + lang + " de la colonne " + column_name + " de la table " + table_name);
		    	stmt.setString(5, "Ce champs est la description " + lang + " de la colonne " + column_name + " de la table " + table_name);
		    	try {
		    		stmt.execute();
			    }
				catch(SqlIntegrityConstraintViolationException e){
					System.out.println("Record already exists");
				}	    	
		    	if(stmt != null) {stmt.close();}
		    }
		    if(rst1 != null){rst1.close();}
		
	    }
	    if(rst0 != null){rst0.close();}
	}

}
