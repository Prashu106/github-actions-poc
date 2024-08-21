package com.opentext.ps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class OTMMMetadataExtractor {

    static HashMap<String, Object> map = new HashMap<>();
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            Path path = Paths.get("./testdata/CAM_sample_json2.json");
            String json = Files.readString(path, StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            String metadataModelId = node
                                    .get("asset_resource")
                                    .get("asset")
                                    .get("metadata_model_id")
                                    .asText();
//            System.out.println( "metadata_model_id: " + metadataModelId );
            JsonNode fieldGroups = node
                                   .get("asset_resource")
                                   .get("asset")
                                   .get("metadata")
                                   .get("metadata_element_list");
            List<String> nonExistingFieldGroup = new ArrayList<>();
            nonExistingFieldGroup.add("BMW.FG.ASSET_INFORMATION");
            nonExistingFieldGroup.add("BMW.FG.GENERAL_ASSET_INFORMATION");
            Iterator<JsonNode> fieldGroupsIterator = fieldGroups.iterator();
            while (fieldGroupsIterator.hasNext()) {
                JsonNode fieldGroup = (JsonNode) fieldGroupsIterator.next();
                processFieldGroupsOneByOne(fieldGroup);
            }
            
            printMap();
            writeToFile(mapper);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
        private static void processFieldGroupsOneByOne(JsonNode fieldGroup) {
        JsonNode metadataFieldsInsideFieldGroup = fieldGroup
                .get("metadata_element_list");

        Iterator<JsonNode> metadataFieldsInsideFieldGroupIterator = metadataFieldsInsideFieldGroup
                .iterator();

        while (metadataFieldsInsideFieldGroupIterator.hasNext()) {
            JsonNode MetadataField = (JsonNode) metadataFieldsInsideFieldGroupIterator.next();
            extractFieldValueAndId(MetadataField);
        }
    }
    
    private static void extractFieldValueAndId(JsonNode ScalarOrTabularMetaDataField) {
        
        // Tabular Field
        if(ScalarOrTabularMetaDataField.has("metadata_element_list")) {
//            System.out.println(ScalarOrTabularMetaDataField.get("id") +" is a tabular field");
            mapToKeyValuePairForTabularField(ScalarOrTabularMetaDataField);
        }
        //Scalar Field
        if(ScalarOrTabularMetaDataField.has("value")) {
//            System.out.println(ScalarOrTabularMetaDataField.get("id")+ " is a scalar field");
//            mapToKeyValuePairForScalarField(ScalarOrTabularMetaDataField);
        }

    }

    private static void mapToKeyValuePairForScalarField(JsonNode field) {
        String id = field.get("id").toString();
        JsonNode value = field.get("value");
            Object val = getValue(value);
            putIntoMap(id, val);
    }
    
    private static void mapToKeyValuePairForTabularField(JsonNode metaDataField) {
        JsonNode value = metaDataField.get("metadata_element_list");
        Iterator<JsonNode> innerValue = value.iterator();
        while (innerValue.hasNext()) {
            JsonNode jsonNode = (JsonNode) innerValue.next();
            String id = jsonNode.get("id").toString();
            JsonNode node = jsonNode.get("value");
            Object val = getValue(node);
            putIntoMap(id, val);
                
        }
        
    }
    
    private static Object getValue(JsonNode node) {

        if (node == null) {
            return null;
        }
        return domainValueCheck(node);
    }
    
    private static Object domainValueCheck(JsonNode node) {
        boolean isDomainValue = node.get("domain_value").asBoolean();
        boolean isCascadingDomainValue = node.get("cascading_domain_value").asBoolean();
        if (isDomainValue) {
            return node.get("value").get("field_value").get("value");
        }
        
        if (isCascadingDomainValue) {
            return node.get("value").get("field_value").get("value");
        }
        
        else {
            if( node.has("value") )
                return node.get("value").get("value");
            return null;
        }
    }
    
    private static void printMap() {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            System.out.println(key+"  "+val);
            System.out.println();
            
        }
    }
    
    private static void writeToFile(ObjectMapper mapper) throws IOException {
        // TODO Auto-generated method stub
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String jsonToFile =mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        Path path1
        = Paths.get("./testData/extractedJson.json");
        Files.writeString(path1, jsonToFile.replace("\\\"", ""), StandardCharsets.UTF_8);
        
    }
     private static void putIntoMap(String id, Object val) {
         if( !id.contains("ARTESIA") && val !=null){
                map.put(id, val);
        }
     }
}
