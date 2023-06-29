package net.witixin.snowballeffect;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.witixin.snowballeffect.entity.Igloof;
import net.witixin.snowballeffect.registry.BlockRegistry;
import net.witixin.snowballeffect.registry.EntityRegistry;
import net.witixin.snowballeffect.registry.ItemRegistry;


@Mod(SnowballEffect.MODID)
public class SnowballEffect {
    public static final String MODID = "snowballeffect";

    public static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> SNOWBALL_EFFECT_TAB = TAB_REGISTER.register("snowballeffect", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.snowballeffect.creativetab"))
            .icon(() -> ItemRegistry.MAGIC_COAL.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ItemRegistry.MAGIC_COAL.get());
                output.accept(ItemRegistry.MAGIC_TORCH_ITEM.get());
            })
            .build());

    public static final DeferredRegister<ParticleType<?>> PARTICLE_REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    public static final RegistryObject<SimpleParticleType> MAGIC_TORCH_PARTICLE = PARTICLE_REG.register("magic_coal_particle", () -> new SimpleParticleType(true));

    public SnowballEffect() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        TAB_REGISTER.register(modBus);
        PARTICLE_REG.register(modBus);
        EntityRegistry.get().register(modBus);
        BlockRegistry.get().register(modBus);
        ItemRegistry.get().register(modBus);
        modBus.addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.addListener(this::onDataPackLoad);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SnowballEffectConfig.GENERAL_SPEC, "snowballeffect.toml");
    }

    private void setupClient(final FMLClientSetupEvent event){
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MAGIC_TORCH.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.WALL_MAGIC_TORCH.get(), RenderType.cutout());
    }

    public static ResourceLocation rl(String s){
        return new ResourceLocation(MODID, s);
    }

    private void onDataPackLoad(AddReloadListenerEvent event) {
        event.addListener(new IgloofFeedDataManager("igloof_feed"));
    }

    //TODO change where and how an igloof can eat as well as add a goal to move towards snow.
    // Maybe user configurable with an item or something? Like when it's set to this mode, it can go around and find snow.

}

