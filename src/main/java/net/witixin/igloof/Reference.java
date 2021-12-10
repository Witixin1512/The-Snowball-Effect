package net.witixin.igloof;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.witixin.igloof.entity.EntityIgloof;
import net.witixin.igloof.item.MagicCoalItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;


@Mod(Reference.MODID)
public class Reference
{
    public static final String MODID = "igloof";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITY_REG = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final RegistryObject<EntityType<EntityIgloof>> IGLOOF = ENTITY_REG.register("igloof", () -> EntityType.Builder.of(EntityIgloof::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10).build("igloof"));

    public static final DeferredRegister<Item> ITEM_REG = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<MagicCoalItem> MAGIC_COAL = ITEM_REG.register("magic_coal", () -> new MagicCoalItem(new Item.Properties().stacksTo(1)));



    public Reference() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ENTITY_REG.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEM_REG.register(FMLJavaModLoadingContext.get().getModEventBus());
        GeckoLib.initialize();
    }


    private void setup(final FMLCommonSetupEvent event)
    {

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
