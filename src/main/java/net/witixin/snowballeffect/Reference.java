package net.witixin.snowballeffect;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.witixin.snowballeffect.entity.EntityIgloof;
import net.witixin.snowballeffect.item.MagicCoalItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;


@Mod(Reference.MODID)
public class Reference
{
    public static final String MODID = "snowballeffect";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITY_REG = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final RegistryObject<EntityType<EntityIgloof>> IGLOOF = ENTITY_REG.register("igloof", () -> EntityType.Builder.of(EntityIgloof::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10).build("igloof"));

    public static final DeferredRegister<Block> BLOCK_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> MAGIC_TORCH = BLOCK_REG.register("magic_torch_floor", () ->
        new TorchBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel((lightLevel) -> {return 15;}).sound(SoundType.WOOD), ParticleTypes.FLAME));
    public static final RegistryObject<Block> WALL_MAGIC_TORCH = BLOCK_REG.register("magic_torch_standing", () ->
            new WallTorchBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel((lightLevel) -> {return 15;}).sound(SoundType.WOOD).dropsLike(MAGIC_TORCH.get()), ParticleTypes.FLAME));

    public static final DeferredRegister<Item> ITEM_REG = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<MagicCoalItem> MAGIC_COAL = ITEM_REG.register("magic_coal", () -> new MagicCoalItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MAGIC_TORCH_ITEM = ITEM_REG.register("magic_torch",() -> new StandingAndWallBlockItem(MAGIC_TORCH.get(), WALL_MAGIC_TORCH.get(), new Item.Properties()));


    public Reference() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        GeckoLib.initialize();
        ENTITY_REG.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCK_REG.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEM_REG.register(FMLJavaModLoadingContext.get().getModEventBus());
    }


    private void setup(final FMLCommonSetupEvent event)
    {

    }
    private void setupClient(final FMLClientSetupEvent event){
        ItemBlockRenderTypes.setRenderLayer(MAGIC_TORCH.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WALL_MAGIC_TORCH.get(), RenderType.cutout());
    }

    /*
    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo(MODID, "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).
                collect(Collectors.toList()));
    } */

    public static ResourceLocation rl(String s ){
        return new ResourceLocation(MODID, s);
    }

}
