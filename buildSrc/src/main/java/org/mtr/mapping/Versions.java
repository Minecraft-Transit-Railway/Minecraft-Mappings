package org.mtr.mapping;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jonafanho.apitools.ModId;
import com.jonafanho.apitools.ModLoader;
import com.jonafanho.apitools.ModProvider;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class Versions {

	public static String getFabricVersion(String minecraftVersion) {
		return getJson("https://meta.fabricmc.net/v2/versions/loader/" + minecraftVersion).getAsJsonArray().get(0).getAsJsonObject().getAsJsonObject("loader").get("version").getAsString();
	}

	public static String getYarnVersion(String minecraftVersion) {
		return getJson("https://meta.fabricmc.net/v2/versions/yarn/" + minecraftVersion).getAsJsonArray().get(0).getAsJsonObject().get("version").getAsString();
	}

	public static String getFabricApiVersion(String minecraftVersion) {
		final String modIdString = "fabric-api";
		return new ModId(modIdString, ModProvider.MODRINTH).getModFiles(minecraftVersion, ModLoader.FABRIC, "").get(0).fileName.split(".jar")[0].replace(modIdString + "-", "");
	}

	public static String getForgeVersion(String minecraftVersion) {
		return getJson("https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json").getAsJsonObject().getAsJsonObject("promos").get(minecraftVersion + "-latest").getAsString();
	}

	private static JsonElement getJson(String url) {
		for (int i = 0; i < 5; i++) {
			try {
				return JsonParser.parseString(IOUtils.toString(new URL(url), StandardCharsets.UTF_8));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new JsonObject();
	}
}
