package net.witixin.snowballeffect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.witixin.snowballeffect.client.TorchParticle;
import net.witixin.snowballeffect.entity.EntityIgloof;
import net.witixin.snowballeffect.registry.BlockRegistry;
import net.witixin.snowballeffect.registry.EntityRegistry;
import net.witixin.snowballeffect.registry.ItemRegistry;
import software.bernie.geckolib3.GeckoLib;



@Mod(Reference.MODID)
public class Reference
{
    public static final String MODID = "snowballeffect";

    public static final DeferredRegister<ParticleType<?>> PARTICLE_REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    public static final RegistryObject<SimpleParticleType> MAGIC_TORCH_PARTICLE = PARTICLE_REG.register("magic_coal_particle", () -> new SimpleParticleType(true));

    public Reference() {
        PARTICLE_REG.register(FMLJavaModLoadingContext.get().getModEventBus());
        EntityRegistry.get().register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockRegistry.get().register(FMLJavaModLoadingContext.get().getModEventBus());
        ItemRegistry.get().register(FMLJavaModLoadingContext.get().getModEventBus());
        GeckoLib.initialize();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SEConfig.GENERAL_SPEC, "snowballeffect.toml");
    }


    private void setup(final FMLCommonSetupEvent event)
    {
        EntityIgloof.setupValueMap();
    }
    private void setupClient(final FMLClientSetupEvent event){
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MAGIC_TORCH.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.WALL_MAGIC_TORCH.get(), RenderType.cutout());
    }


    public static ResourceLocation rl(String s ){
        return new ResourceLocation(MODID, s);
    }

}
