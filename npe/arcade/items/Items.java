package npe.arcade.items;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Items {

    public static Item arcade;

    public static void init() {
        arcade = new ItemArcade(ItemInfo.ARCADE_ID);
    }

    public static void addNames() {
        LanguageRegistry.addName(arcade, ItemInfo.ARCADE_NAME);
    }

    public static void registerRecipes() {
        GameRegistry.addRecipe(new ItemStack(arcade),
                new Object[] { //
                " ii", //
                "idg", //
                "ixi", //
                'i', Item.ingotIron, //
                'd', Item.diamond, //
                'g', Block.thinGlass, //
                'x', Block.blockRedstone
                });
    }
}
