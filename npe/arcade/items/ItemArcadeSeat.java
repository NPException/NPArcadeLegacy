package npe.arcade.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import npe.arcade.ArcadeMod;
import npe.arcade.blocks.BlockInfo;
import npe.arcade.entities.EntityArcadeSeat;
import npe.arcade.tileentities.TileEntityArcade;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArcadeSeat extends Item {

	public ItemArcadeSeat(int id) {
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

				EntityArcadeSeat seat = new EntityArcadeSeat(world);

				seat.posX = posX + 0.5;
				seat.posY = posY;
				seat.posZ = posZ + 0.5;

				// check adjacent blocks for arcademachine and occupy before spawning
				tryOccupy(world, posX + 1, posY + 1, posZ, 1, seat);
				tryOccupy(world, posX - 1, posY + 1, posZ, 3, seat);
				tryOccupy(world, posX, posY + 1, posZ + 1, 2, seat);
				tryOccupy(world, posX, posY + 1, posZ - 1, 0, seat);

				world.spawnEntityInWorld(seat);

				if (!player.capabilities.isCreativeMode) {
					stack.stackSize--;
				}

				return true;
			}
		}
		return false;
	}

	private void tryOccupy(World world, int x, int y, int z, int requiredFacing, EntityArcadeSeat seat) {
		if (seat.getOccupiedArcadeMachine() != null) {
			return;
		}
		if (world.getBlockId(x, y, z) == BlockInfo.ARCADE_TOP_ID) {
			if (world.getBlockMetadata(x, y, z) == requiredFacing) {
				TileEntityArcade arcade = (TileEntityArcade)world.getBlockTileEntity(x, y, z);
				seat.occupyArcade(arcade);
			}
		}
	}
}
