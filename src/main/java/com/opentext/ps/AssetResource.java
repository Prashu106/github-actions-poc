package com.opentext.ps;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class AssetResource {
	
	
	JsonNode asset;
	Object metadata;

	@JsonProperty("asset_resource")
	private void getAssetResourceFromJson(JsonNode brand) {
		asset =  brand.get("asset");
	}
	
	public JsonNode getMetadata() {
		return asset;
	}
	
	
	
}
