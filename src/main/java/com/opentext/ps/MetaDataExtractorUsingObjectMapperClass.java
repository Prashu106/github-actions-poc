package com.opentext.ps;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MetaDataExtractorUsingObjectMapperClass {

	ObjectMapper mapper = new ObjectMapper();
	
	static HashMap<String,Object> map = new HashMap<>();
	
	public static void main(String[] args) throws IOException {

		
		Path path = Paths.get("./testdata/CAM_sample_json2.json");
		String json = Files.readString(path, StandardCharsets.UTF_8);

		InputStream fis = new FileInputStream("./testdata/CAM_sample_json2.json");

        // create JsonReader object
        JsonReader jsonReader = Json.createReader(fis);

        // get JsonObject from JsonReader
        JsonObject jsonObject = jsonReader.readObject();
		
        JsonArray jsonArray =jsonObject.asJsonObject()
        						.get("asset_resource").asJsonObject()
        						.get("asset").asJsonObject()
        						.get("metadata").asJsonObject()
        						.get("metadata_element_list").asJsonArray();
        
        for (JsonValue jsonValue : jsonArray) {
        	JsonArray metadataFieldsInAFieldGroup =jsonValue.asJsonObject().get("metadata_element_list").asJsonArray();
        	iterateOverEachFieldInAFieldGroup(metadataFieldsInAFieldGroup);
		}
        
        /*
        
        for (JsonValue jsonValue : jsonArray) {
			
		}
        
        
		AssetResource assetResource = mapper.readValue(json, AssetResource.class);

		JsonNode metadata = assetResource.getMetadata();
		
		JsonNode metadata1= metadata.get("metadata");
		
		JsonNode metadata2= metadata1.get("metadata_element_list");
		
		Iterator<JsonNode> metadata2Iterator =metadata2.iterator();
		
		while (metadata2Iterator.hasNext()) {
			JsonNode fieldGroup = (JsonNode) metadata2Iterator.next();
			System.out.println(fieldGroup.get("metadata_element_list"));
			
		}
		
		JsonNode metadata3= metadata2.get("id");
		
		
		System.out.println();
		
		for (Map.Entry<String, Object> entry : asset.entrySet()) {
			String key = entry.getKey();
			HashMap<String, Object> val = (HashMap<String, Object>) entry.getValue();

			for (Map.Entry<String, Object> entry1 : val.entrySet()) {
				if (entry1.getKey().equalsIgnoreCase("metadata")) {
					HashMap<String, Object> val1 = (HashMap<String, Object>) entry1.getValue();
					for (Map.Entry<String, Object> entry2 : val1.entrySet()) {
						if (entry2.getKey().equalsIgnoreCase("metadata_element_list")) {
							ArrayList<Object> val2 =  (ArrayList<Object>) entry2.getValue();
								System.out.println();
						}
					}

				}
			}
		}
		*/
	}
	
	public static void iterateOverEachFieldInAFieldGroup(JsonArray metadataFieldsInAFieldGroup) {
    	
		for (JsonValue jsonValue : metadataFieldsInAFieldGroup) {
//			System.out.println(jsonValue.asJsonObject());
			if (jsonValue.asJsonObject().containsKey("value")) {
				scalarField(jsonValue.asJsonObject());	
			}else {
				tabularField(jsonValue.asJsonObject());
			}
		}
		System.out.println(map);
    }
	
	public static void scalarField(JsonObject jsonObject) {
//		System.out.println(jsonObject.get("id") +" is a scalar field");
	}
	public static void tabularField(JsonObject jsonObject) {
		JsonArray arr = jsonObject.get("metadata_element_list").asJsonArray();
		valuesOftabularField(arr);
//		System.out.println(jsonObject.get("id") +" is a tabular field")
	}
	
	public static void valuesOftabularField(JsonArray array) {
		for (JsonValue jsonValue : array) {
			JsonObject obj = jsonValue.asJsonObject();
			if(	obj.get("id").toString().replaceAll("\"","")
					  .equalsIgnoreCase("BMW.TF.TAGS_EN_AG")) {
//					System.out.println(obj);
				String id = obj.get("id").toString();
					if(obj.containsKey("values"))
					{
						JsonArray arr = obj.get("values").asJsonArray();
//						System.out.println(arr);
						List<String> arrayOfString = new ArrayList<>();
						for (JsonValue jsonValue2 : arr) {
						System.out.println(jsonValue2);	
						arrayOfString.add(
						jsonValue2.asJsonObject().get("value")
						.asJsonObject().get("field_value").asJsonObject()
						.get("value").toString());
						}
						map.put(id, arrayOfString);
					}
			}
		}
	}
	
}
