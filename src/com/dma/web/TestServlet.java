package com.dma.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class GetImportedKeysServlet
 */
@WebServlet("/Test")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		Map<String, Object> parms = Tools.fromJSON(request.getInputStream());
		
		String projectName = (String) parms.get("projectName");
		String data = (String) parms.get("data");
		
		ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        List<QuerySubject> qsList = Arrays.asList(mapper.readValue(data, QuerySubject[].class));
        
        Map<String, QuerySubject> query_subjects = new HashMap<String, QuerySubject>();
        
        for(QuerySubject qs: qsList){
        	query_subjects.put(qs.get_id(), qs);
        }

		Map<String, Object> result = new HashMap<String, Object>();
		
		result.put("QSCount", query_subjects.size());
		
		Path cognosModelsPath = Paths.get((String) request.getServletContext().getAttribute("cognosModelsPath"));
		if(!Files.isWritable(cognosModelsPath)){
			result.put("status", "KO");
			result.put("message", "cognosModelsPath '" + cognosModelsPath + "' not writeable." );
			result.put("troubleshooting", "Check that '" + cognosModelsPath + "' exists on server and is writable.");
		}
		
		Path projectPath = Paths.get(cognosModelsPath + "/" + projectName);
		
		if(Files.exists(projectPath)){
			Files.walk(Paths.get(projectPath.toString()))
            .map(Path::toFile)
            .sorted((o1, o2) -> -o1.compareTo(o2))
            .forEach(File::delete);
		}
		
		Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
		FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(perms);
		Files.createDirectories(projectPath, attrs);
		
		Path zip = Paths.get(getServletContext().getRealPath("/res/model.zip"));
		if(!Files.exists(zip)){
			result.put("status", "KO");
			result.put("message", "Generic model '" + zip + "' not found." );
			result.put("troubleshooting", "Check that '" + zip + "' exists on server.");
		}
		
		
		BufferedOutputStream dest = null;
		int BUFFER = Long.bitCount(Files.size(zip));
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(Files.newInputStream(zip))); 
		ZipEntry entry;
		while((entry = zis.getNextEntry()) != null) {
			System.out.println("Extracting: " + entry);
            int count;
            byte datas[] = new byte[BUFFER];
            // write the files to the disk
            FileOutputStream fos = new FileOutputStream(projectPath + "/" + entry.getName());
            dest = new BufferedOutputStream(fos, BUFFER);
            while ((count = zis.read(datas, 0, BUFFER)) 
              != -1) {
               dest.write(datas, 0, count);
            }
            dest.flush();
            dest.close();
        }
		zis.close();
		
		Path cpf = Paths.get(projectPath + "/model.cpf");
		Path renamedCpf = Paths.get(projectPath + "/" + projectName + ".cpf"); 
		if(Files.exists(cpf)){
			Files.move(cpf, renamedCpf);
			result.put("status", "OK");
			result.put("message", renamedCpf + " found in " + projectPath + ".");
			result.put("troubleshooting", "");
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

