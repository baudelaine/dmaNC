package com.dma.action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.dma.web.QuerySubject;
import com.dma.web.Tools;
import com.fasterxml.jackson.core.type.TypeReference;

public class Test5 {

	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Path path = Paths.get("/opt/wks/dmaNC/mmm-2019-02-28-13-30-13.json");
		Path output = Paths.get("/opt/wks/dmaNC/output.json");
		
		
		try {
			List<QuerySubject> querySubjects = (List<QuerySubject>) Tools.fromJSON(path.toFile(), new TypeReference<List<QuerySubject>>(){});
			
			Files.write(output, Tools.toJSON(querySubjects).getBytes());

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
