package com.dma.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class test4 {

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		// TODO Auto-generated method stub

		Map<String, Project> ps = new HashMap<String, Project>();
		
		Resource r = new Resource();
		r.setJndiName("jdbc/DB2");
		r.setDbName("FRMARK");
		r.setDbEngine("DB2");
		
		Project p = new Project();
		p.setName("PRJ0");
		p.setDbSchema("DB2INST1");
		p.setResource(r);
		
		ps.put(p.getName(), p);

		r = new Resource();
		r.setJndiName("jdbc/ORA");
		r.setDbName("lvso112");
		r.setDbEngine("ORA");
		
		p = new Project();
		p.setName("PRJ1");
		p.setDbSchema("DANONE");
		p.setResource(r);
		
		ps.put(p.getName(), p);

		r = new Resource();
		r.setJndiName("jdbc/MYSQL");
		r.setDbName("employees");
		r.setDbEngine("MYSQL");
		
		p = new Project();
		p.setName("PRJ2");
		p.setDbSchema("");
		p.setResource(r);
		
		ps.put(p.getName(), p);

		r = new Resource();
		r.setJndiName("jdbc/PGSQL");
		r.setDbName("dvdrental");
		r.setDbEngine("PGSQL");
		
		p = new Project();
		p.setName("PRJ3");
		p.setDbSchema("");
		p.setResource(r);

		ps.put(p.getName(), p);
		
		for(Entry<String, Project> e: ps.entrySet()){
			Project prj = e.getValue();
			Resource res = prj.getResource();
			res.setDescription(res.getDbName() + " (" + res.getJndiName() + " - " + res.getDbEngine() + ")");
			
		}
		
		System.out.println(Tools.toJSON(ps));
		
	}

}
