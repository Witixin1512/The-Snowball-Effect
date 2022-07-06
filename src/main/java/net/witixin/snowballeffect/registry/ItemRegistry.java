package net.witixin.snowballeffect.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.witixin.snowballeffect.item.MagicCoalItem;

import static net.witixin.snowballeffect.Reference.MODID;

public class ItemRegistry {

    public static final CreativeModeTab SNOWBALL_EFFECT_TAB = new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), "snowballeffect.creativetab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(MAGIC_COAL.get());
        }
    };

    public static final DeferredRegister<Item> ITEM_REG = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<MagicCoalItem> MAGIC_COAL = ITEM_REG.register("magic_coal", () -> new MagicCoalItem(new Item.Properties().stacksTo(1).tab(SNOWBALL_EFFECT_TAB)));
    public static final RegistryObject<Item> MAGIC_TORCH_ITEM = ITEM_REG.register("magic_torch",() -> new StandingAndWallBlockItem(BlockRegistry.MAGIC_TORCH.get(), BlockRegistry.WALL_MAGIC_TORCH.get(), new Item.Properties().tab(SNOWBALL_EFFECT_TAB)));

    public static DeferredRegister<?> get(){
        return ITEM_REG;
    }
}
