package npe.arcade.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import npe.arcade.ArcadeMod;
import npe.arcade.blocks.BlockInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArcadeStool extends Item {

	public ItemArcadeStool(int id) {
		super(id);
		setCreativeTab(ArcadeMod.CREATIVE_TAB);
		setUnlocalizedName(ItemInfo.ARCADE_SEAT_UNLOCALIZED_NAME);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {
		itemIcon = register.registerIcon(ItemInfo.TEXTURE_LOCATION + ":" + ItemInfo.ARCADE_SEAT_ICON);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			ForgeDirection direction = ForgeDirection.getOrientation(side);
			int posX = x + direction.offsetX;
			int posY = y + direction.offsetY;
			int posZ = z + direction.offsetZ;

			if (world.isAirBlock(posX, posY, posZ)) {

				world.setBlock(posX, posY, posZ, BlockInfo.ARCADE_STOOL_ID);

				if (!player.capabilities.isCreativeMode) {
					stack.stackSize--;
				}

				return true;
			}
		}
		return false;
	}
}
