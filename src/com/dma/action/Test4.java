package com.dma.action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dma.web.Field;
import com.dma.web.QuerySubject;
import com.dma.web.Tools;
import com.fasterxml.jackson.core.type.TypeReference;

public class Test4 {

	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Path path = Paths.get("/opt/wks/dmaNC/foodmart_multi_BK-2019-02-3-12-22-49.json");
		Path output = Paths.get("/opt/wks/dmaNC/output.json");
		
		try {
			List<QuerySubject> querySubjects = (List<QuerySubject>) Tools.fromJSON(path.toFile(), new TypeReference<List<QuerySubject>>(){});
			
			
			for(QuerySubject querySubject: querySubjects) {

				for(Field field: querySubject.getFields()){
					List<Map<String, String>> dimensions = new ArrayList<Map<String, String>>();
					List<String> dimensionList = Arrays.asList(field.getDimension().split(","));
					List<String> orderList = Arrays.asList(field.getOrder().split(","));
					List<String> bkList = Arrays.asList(field.getBK().split(","));
					List<String> hierarchyNameList = Arrays.asList(field.getHierarchyName().split(","));

					int index = 0;
					for(String dimensionItem: dimensionList) {
						if(!dimensionItem.isEmpty()) {
							Map<String, String> dimension = new HashMap<String, String>();
							dimension.put("dimension", dimensionItem);
							dimension.put("order","");
							dimension.put("bk","");
							dimension.put("hierarchyName","");
							
							try {
								dimension.put("order", orderList.get(index).split("-")[1]);
							}
							catch(ArrayIndexOutOfBoundsException e) {}
							
							try {
								dimension.put("bk", bkList.get(index).split("-")[1]);
							}
							catch(ArrayIndexOutOfBoundsException e) {}
							
							try {
								dimension.put("hierarchyName", hierarchyNameList.get(index).split("-")[1]);
							}
							catch(ArrayIndexOutOfBoundsException e) {}
							
							dimensions.add(dimension);
						}
						index++;
					}
					field.setDimensions(dimensions);
					System.out.println(Tools.toJSON(dimensions));
				}
			
			}
			
			Files.write(output, Tools.toJSON(querySubjects).getBytes());

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
