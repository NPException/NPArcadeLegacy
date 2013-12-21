package npe.arcade.config;

import java.io.File;

import net.minecraftforge.common.Configuration;
import npe.arcade.blocks.BlockInfo;
import npe.arcade.items.ItemInfo;

public class ConfigHandler {
	public static void init(File configfile) {
		final Configuration config = new Configuration(configfile);
		config.load();

		// item config
		ItemInfo.ARCADE_ID = config.getItem(ItemInfo.ARCADE_KEY, ItemInfo.ARCADE_DEFAULT).getInt() - 256;
		ItemInfo.ARCADE_SEAT_ID = config.getItem(ItemInfo.ARCADE_SEAT_KEY, ItemInfo.ARCADE_SEAT_DEFAULT).getInt() - 256;

		// block config
		BlockInfo.ARCADE_BASE_ID = config.getBlock(BlockInfo.ARCADE_BASE_KEY, BlockInfo.ARCADE_BASE_DEFAULT_ID).getInt();
		BlockInfo.ARCADE_TOP_ID = config.getBlock(BlockInfo.ARCADE_TOP_KEY, BlockInfo.ARCADE_TOP_DEFAULT_ID).getInt();
		BlockInfo.ARCADE_STOOL_ID = config.getBlock(BlockInfo.ARCADE_STOOL_KEY, BlockInfo.ARCADE_STOOL_DEFAULT_ID).getInt();

		config.save();
	}
}
