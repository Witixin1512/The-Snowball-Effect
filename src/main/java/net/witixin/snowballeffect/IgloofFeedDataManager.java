package net.witixin.snowballeffect;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.witixin.snowballeffect.entity.Igloof;
import org.apache.logging.log4j.LogManager;

import java.util.Map;
public class IgloofFeedDataManager extends SimpleJsonResourceReloadListener {

    public IgloofFeedDataManager(String id){
        super(new Gson(), id);
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager p_10771_, ProfilerFiller p_10772_){
        Igloof.clearEdibles();
        return super.prepare(p_10771_, p_10772_);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profilerFiller){
        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
            try {
                JsonObject root = entry.getValue().getAsJsonObject();
                float value = root.get("feed_amount").getAsFloat();
                if (value <= 0.0) return;
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(root.get("feed_block").getAsString()));
                Igloof.registerEdibleBlock(block, value);
            } catch (Exception e) {
                LogManager.getLogger(SnowballEffect.MODID).error("Error reading json file with name: {}, due to the following error: {}", entry.getKey().toString(), e.toString());
            }
        }
    }

}
