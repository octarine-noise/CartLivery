package mods.cartlivery.client;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.client.resources.ResourceManagerReloadListener;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepositoryEntry;
import net.minecraft.client.resources.data.MetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LiveryTextureRegistry implements ResourceManagerReloadListener {

	public static Map<String, LiveryTextureInfo> map = Maps.newHashMap();
	public static Map<String, String> builtInLiveries = Maps.newHashMap();
	public static LiveryListMetadataSerializer serializer = new LiveryListMetadataSerializer();
	
	public static void registerLivery(String name, String packName) {
		if (name == null || name.isEmpty()) return;
		
		if (!map.keySet().contains(name)) {
			LiveryTextureInfo info = new LiveryTextureInfo();
			info.packName = packName;
			info.texture = new ResourceLocation("cartlivery", "textures/livery/" + name + ".png");
			map.put(name, info);
			Minecraft.getMinecraft().renderEngine.loadTexture(info.texture, new SimpleTexture(info.texture));
		}
	}
	
	public static ResourceLocation getTexture(String pattern) {
		return map.keySet().contains(pattern) ? map.get(pattern).texture : null;
	}
	
	public static Collection<String> getAvailableLiveries() {
		List<String> result = Lists.newArrayList(map.keySet());
		Collections.sort(result);
		return result;
	}
	
	public static String cycle(String current, boolean increment) {
		Collection<String> available = getAvailableLiveries();
		if (available.size() == 0) return "";
		
		int index = Iterables.indexOf(available, Predicates.equalTo(current));
		index += (increment ? 1 : -1);
		if (index >= available.size() || index == -1) return "";
		if (index < -1) index = available.size() - 1;
		return Iterables.get(available, index);
	}

	@SuppressWarnings("unchecked")
	public void onResourceManagerReload(ResourceManager var1) {
		// TextureManager in 1.6.4 does not support unloading
//		for (LiveryTextureInfo info : map.values()) Minecraft.getMinecraft().renderEngine.deleteTexture(info.texture);
		map.clear();
		
		// list liveries in currently loaded resource packs
		ResourcePackRepository repo = Minecraft.getMinecraft().getResourcePackRepository();
		List<ResourcePackRepositoryEntry> packList = repo.getRepositoryEntries();
		for (ResourcePackRepositoryEntry loadedPack : packList) {
			try {
				LiveryListMetadata metadata = (LiveryListMetadata) loadedPack.getResourcePack().getPackMetadata(serializer, "cartlivery");
				for (String liveryName : metadata.definedLiveries) {
					registerLivery(liveryName, String.format(I18n.getString("cartlivery.respack"), loadedPack.getResourcePackName()));
				}
			} catch (Exception e) {
				// probably no pack.mcmeta - ignore it, it will be logged anyway 
			}
		}
		
		// finally add built-in liveries
		for(String builtInName : builtInLiveries.keySet()) registerLivery(builtInName, String.format(I18n.getString("cartlivery.builtin"), builtInLiveries.get(builtInName)));
	}
	
	public static class LiveryListMetadata implements MetadataSection {
		public Collection<String> definedLiveries = Sets.newHashSet();
	}
	
	public static class LiveryListMetadataSerializer extends MetadataSerializer {
		@Override
		public MetadataSection parseMetadataSection(String type, JsonObject json) {
			LiveryListMetadata result = new LiveryListMetadata();
			if (json.has("cartlivery") && json.get("cartlivery").isJsonObject()) {
				JsonObject clMetaSection = json.getAsJsonObject("cartlivery");
				if (clMetaSection.has("liverylist") && clMetaSection.get("liverylist").isJsonArray()) {
					for (JsonElement liveryElem : clMetaSection.getAsJsonArray("liverylist")) {
						if (liveryElem.isJsonPrimitive()) result.definedLiveries.add(liveryElem.getAsString());
					}
				}
			}
			return result;
		}
	}
}
