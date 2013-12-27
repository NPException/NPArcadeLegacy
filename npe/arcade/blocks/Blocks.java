package npe.arcade.blocks;

import net.minecraft.block.Block;
import npe.arcade.tileentities.TileEntityArcade;
import npe.arcade.tileentities.TileEntityArcadeStool;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Blocks {

	public static Block arcadeBase;
	public static Block arcadeTop;
	public static Block arcadeStool;

	public static void init() {
		arcadeBase = new BlockArcadeBase(BlockInfo.ARCADE_BASE_ID);
		GameRegistry.registerBlock(arcadeBase, BlockInfo.ARCADE_BASE_KEY);

		arcadeTop = new BlockArcadeTop(BlockInfo.ARCADE_TOP_ID);
		GameRegistry.registerBlock(arcadeTop, BlockInfo.ARCADE_TOP_KEY);

		arcadeStool = new BlockArcadeStool(BlockInfo.ARCADE_STOOL_ID);
		GameRegistry.registerBlock(arcadeStool, BlockInfo.ARCADE_STOOL_KEY);
	}

	public static void addNames() {
		LanguageRegistry.addName(arcadeBase, BlockInfo.ARCADE_NAME);
		LanguageRegistry.addName(arcadeTop, BlockInfo.ARCADE_NAME);
	}

	public static void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityArcade.class, BlockInfo.ARCADE_TE_KEY);
		GameRegistry.registerTileEntity(TileEntityArcadeStool.class, BlockInfo.ARCADE_STOOL_TE_KEY);
	}
}
