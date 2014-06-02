package mods.cartlivery.client;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
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
public class LiveryTextureRegistry implements IResourceManagerReloadListener {

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
	public void onResourceManagerReload(IResourceManager var1) {
		// release OpenGL resources
		for (LiveryTextureInfo info : map.values()) Minecraft.getMinecraft().renderEngine.deleteTexture(info.texture);
		map.clear();
		
		// list liveries in currently loaded resource packs
		ResourcePackRepository repo = Minecraft.getMinecraft().getResourcePackRepository();
		List<ResourcePackRepository.Entry> packList = repo.getRepositoryEntries();
		for (ResourcePackRepository.Entry loadedPack : packList) {
			try {
				LiveryListMetadata metadata = (LiveryListMetadata) loadedPack.getResourcePack().getPackMetadata(serializer, "cartlivery");
				for (String liveryName : metadata.definedLiveries) {
					registerLivery(liveryName, I18n.format("cartlivery.respack", loadedPack.getResourcePackName()));
				}
			} catch (IOException e) {
				// probably no pack.mcmeta - ignore it, it will be logged anyway 
			}
		}
		
		// finally add built-in liveries
		for(String builtInName : builtInLiveries.keySet()) registerLivery(builtInName, I18n.format("cartlivery.builtin", builtInLiveries.get(builtInName)));
	}
	
	public static class LiveryListMetadata implements IMetadataSection {
		public Collection<String> definedLiveries = Sets.newHashSet();
	}
	
	public static class LiveryListMetadataSerializer extends IMetadataSerializer {
		@Override
		public IMetadataSection parseMetadataSection(String type, JsonObject json) {
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
