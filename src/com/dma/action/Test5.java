package com.dma.action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.dma.web.Field;
import com.dma.web.QuerySubject;
import com.dma.web.Tools;
import com.fasterxml.jackson.core.type.TypeReference;

public class Test5 {

	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Path path = Paths.get("/opt/wks/dmaNC/testDevSeb-2019-02-11-15-37-27.json");
		Path output = Paths.get("/opt/wks/dmaNC/output.json");
//		String lang = "de,  fr".split(",")[0].trim();
		String lang = null;
		lang = lang.split(",")[0].trim();
		
		
		try {
			List<QuerySubject> querySubjects = (List<QuerySubject>) Tools.fromJSON(path.toFile(), new TypeReference<List<QuerySubject>>(){});
			
			
			for(QuerySubject querySubject: querySubjects) {

				for(Field field: querySubject.getFields()){
					System.out.println(field.getLabel());
					System.out.println(field.getDescription());
					field.getLabels().put(lang, field.getLabel());
					field.getDescriptions().put(lang, field.getDescription());
					field.setLabel("");
					field.setDescription("");
				}
			
			}
			
			Files.write(output, Tools.toJSON(querySubjects).getBytes());

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
