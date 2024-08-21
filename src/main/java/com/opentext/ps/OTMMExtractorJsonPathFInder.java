package com.opentext.ps;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OTMMExtractorJsonPathFInder {

	public static void main(String[] args) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(new File("./testdata/CAM_sample.json"));
		JsonNode fieldGroups = jsonNode.get("asset_resource").get("asset").get("metadata").get("metadata_element_list");
		int fieldGroupIndex = 0;
		while (fieldGroups.has(fieldGroupIndex)) {
			int metadataFieldsIndex = 0;
			JsonNode singleFieldGroup = fieldGroups.get(fieldGroupIndex).get("metadata_element_list");
			while (singleFieldGroup.has(metadataFieldsIndex)) {

				JsonNode mtfield = singleFieldGroup.get(metadataFieldsIndex);
				if (mtfield.get("id").asText().equalsIgnoreCase("BMW.TFG.MOTORISATION_EN_AG")) {
					System.out.println(mtfield.isNull());
				} else {

				}
				metadataFieldsIndex++;
			}
			fieldGroupIndex++;
		}

	}
}
