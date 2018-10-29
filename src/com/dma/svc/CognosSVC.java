package com.dma.svc;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.cognos.developer.schemas.bibus._3.AddOptions;
import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.BiBusHeader;
import com.cognos.developer.schemas.bibus._3.Folder;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.SearchPathSingleObject;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.cognos.developer.schemas.bibus._3.TokenProp;
import com.cognos.developer.schemas.bibus._3.UpdateActionEnum;
import com.cognos.developer.schemas.bibus._3.XmlEncodedXML;
import com.cognos.org.apache.axis.client.Stub;
import com.cognos.org.apache.axis.message.SOAPHeaderElement;
import com.dma.cognos.CRNConnect;
import com.dma.web.QuerySubject;

import sapphire.util.Logger;

public class CognosSVC {

	private CRNConnect crnConnect;
	private String modelPath;
	private Map<String, Element> actionsMap;
	private int i;
	private String pathToXML;


	public CognosSVC (String cognosDispatcher) {
		crnConnect = new CRNConnect();
		crnConnect.setDispatcher(cognosDispatcher);
		crnConnect.connectToCognosServer();
		actionsMap = new HashMap<String, Element>();
		i=1;
	}
	
	public void setPathToXML(String pathToXML){
		this.pathToXML = pathToXML;
	}
	
	public String getPathToXML(){
		return this.pathToXML;
	}
	
	public Document doc = null;

	public boolean logon(String cognosLogin, String cognosPassword, String cognosNamespace) {
		try {
			StringBuilder credentialXML = new StringBuilder();

			credentialXML.append("<credential>");
			credentialXML.append("<namespace>").append(cognosNamespace).append("</namespace>");
			credentialXML.append("<username>").append(cognosLogin).append("</username>");
			credentialXML.append("<password>").append(cognosPassword).append("</password>");
			credentialXML.append("</credential>");

			String encodedCredentials = credentialXML.toString();

			crnConnect.getCMService().logon(new XmlEncodedXML(encodedCredentials), null);

			// TODO Set the BiBusHeader
			SOAPHeaderElement temp = ((Stub) crnConnect.getCMService())
					.getResponseHeader("http://developer.cognos.com/schemas/bibus/3/", "biBusHeader");
			BiBusHeader cmBiBusHeader = (BiBusHeader) temp
					.getValueAsType(new QName("http://developer.cognos.com/schemas/bibus/3/", "biBusHeader"));
			((Stub) crnConnect.getCMService()).setHeader("http://developer.cognos.com/schemas/bibus/3/", "biBusHeader",
					cmBiBusHeader);
			System.out.println("Logon successful as " + cognosLogin);

		} catch (RemoteException ex) {
			lg(ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			lg(ex.getMessage());
		}
		return true;

	}

	public void logoff() {

		try {
			crnConnect.getCMService().logoff();
			System.out.println("logoff");
		} catch (RemoteException ex) {
			lg(ex.getMessage());
			ex.printStackTrace();
		}

	}
	
	public void openModel(String modelName, String cognosFolder) {
		
		modelPath = cognosFolder + "/" + modelName + "/" + modelName + ".cpf";
		try {
			File xmlFile = new File(pathToXML + "/openModel.xml");
			SAXReader reader = new SAXReader();
			Document document = reader.read(xmlFile);

			Node node = document.selectSingleNode("//@model");
			node.setText(modelPath);
			XmlEncodedXML xex = new XmlEncodedXML(node.getParent().asXML());
			String res = crnConnect.getMetadataService().updateMetadata(xex).toString();
			System.out.println("openModel" + modelPath + " successful");
		} catch (DocumentException ex) {
			lg(ex.getMessage());
		} catch (RemoteException ex) {
			lg(ex.getMessage());
		}

	}

	public void saveModel() {

		try {
			File xmlFile = new File(pathToXML + "/saveModel.xml");
			SAXReader reader = new SAXReader();
			Document document = reader.read(xmlFile);
			Node node = document.selectSingleNode("//@model");
			node.setText(modelPath);
			XmlEncodedXML xex = new XmlEncodedXML(node.getParent().asXML());
			String res = crnConnect.getMetadataService().updateMetadata(xex).toString();
			System.out.println("saveModel " + modelPath);
		} catch (DocumentException ex) {
			lg(ex.getMessage());
		} catch (RemoteException ex) {
			lg(ex.getMessage());
		}

	}

	
	public void closeModel() {

		try {
			File xmlFile = new File(pathToXML + "/closeModel.xml");
			SAXReader reader = new SAXReader();
			Document document = reader.read(xmlFile);
			Node node = document.selectSingleNode("//@model");
			node.setText(modelPath);
			XmlEncodedXML xex = new XmlEncodedXML(node.getParent().asXML());
			String res = crnConnect.getMetadataService().updateMetadata(xex).toString();
			System.out.println("closeModel " + modelPath);
		} catch (DocumentException ex) {
			lg(ex.getMessage());
		} catch (RemoteException ex) {
			lg(ex.getMessage());
		}

	}

	public void executeModel(Document D) {
		
		Document scriptDocument = D;

		// 1/ remove root
		List<Node> nodes = scriptDocument.selectNodes("//transaction/action");
		
		for (Node n : nodes) {

			Element e = (Element) n.detach();
			
			actionsMap.put(String.valueOf(i), e);
			i++;			
		} 
	}

	public void executeAllActions() {
		try {
			File rootFile = new File(pathToXML + "/executeModel.xml");

			SAXReader reader = new SAXReader();

			Document rootDocument = reader.read(rootFile);
			Element root = rootDocument.getRootElement();

			Node node = rootDocument.selectSingleNode("//@model");
			node.setText(modelPath);
			
			int j=1;
			List<Element> lst = new ArrayList<Element>();
			root.addElement("transaction");
			Element t = (Element) root.selectSingleNode("//transaction");
			
			while (actionsMap.get(String.valueOf(j)) != null) {

				Element e = actionsMap.get(String.valueOf(j));
				e.addAttribute("seq", String.valueOf(j));
				lst.add(e);
				j++;
				
			}
			
			t.setContent(lst);
			// t.addAttribute("commit", "y");   //pour balise transaction
			XmlEncodedXML xex = new XmlEncodedXML(root.asXML());
			//System.out.println(root.asXML());
			String res = crnConnect.getMetadataService().updateMetadata(xex).toString();
			
		} catch (DocumentException ex) {
			lg(ex.getMessage());
			ex.printStackTrace();
			saveModel();
			closeModel();
			logoff();   // ajout test Nico
//			System.exit(0);
		} catch (RemoteException ex) {
			lg(ex.getMessage());
			ex.printStackTrace();
			saveModel();
			closeModel();
			logoff();  // ajout test Nico
//			System.exit(0);
		}
	}
	
	public void createPublicFolder(String newFolderName) {
		PropEnum[] properties = new PropEnum[] { PropEnum.searchPath, PropEnum.defaultName };

		AddOptions add = new AddOptions();
		add.setUpdateAction(UpdateActionEnum.replace);

		// create new folder object
		Folder aFolder = new Folder();

		TokenProp tp = new TokenProp();
		tp.setValue(newFolderName);

		aFolder.setDefaultName(tp);

		SearchPathMultipleObject searchPath = new SearchPathMultipleObject();
		searchPath.set_value("/content/folder[@name='" + newFolderName + "']");

		try {
			BaseClass[] folder = crnConnect.getCMService().query(searchPath, properties, new Sort[] {}, new QueryOptions());
			// Check if the folder is already exist if not create it.
			if (folder.length == 0) {
				SearchPathSingleObject searchPathSing = new SearchPathSingleObject();
				searchPathSing.set_value("/content");
				crnConnect.getCMService().add(searchPathSing, new BaseClass[] { aFolder }, add);

				System.out.println("New folder [" + newFolderName + "] has been created.");
			} else {
				System.out.println(newFolderName + " already exists.");
			}
		} catch (Exception e) {
			System.out.println("Exception ");
		}
	}


	
	public static void lg(String msg) {
		Logger.logInfo(" BuildModel.java ", msg);
	}
}
