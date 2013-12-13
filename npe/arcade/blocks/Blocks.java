package npe.arcade.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import npe.arcade.tileentities.TileEntityArcade;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Blocks {

    public static Block arcade;

    public static void init() {
        arcade = new BlockArcade(BlockInfo.ARCADE_ID);
        GameRegistry.registerBlock(arcade, BlockInfo.ARCADE_KEY);
    }

    public static void addNames() {
        LanguageRegistry.addName(arcade, BlockInfo.ARCADE_NAME);
    }

    public static void registerRecipes() {
        GameRegistry.addRecipe(new ItemStack(arcade),
                new Object[] { //
                " ii", // 
                "irg", // 
                "iii", //
                'i', Item.ingotIron, //
                'r', Item.redstone, //
                'g', Block.thinGlass //
                });
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityArcade.class, BlockInfo.ARCADE_TE_KEY);
    }
}
