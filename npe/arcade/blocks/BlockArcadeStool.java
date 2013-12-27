package npe.arcade.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import npe.arcade.items.ItemInfo;
import npe.arcade.items.Items;
import npe.arcade.tileentities.TileEntityArcadeStool;

public class BlockArcadeStool extends BlockContainer {

	public BlockArcadeStool(int id) {
		super(id, Material.wood);
		setHardness(2f);
		setStepSound(Block.soundWoodFootstep);
		setUnlocalizedName(ItemInfo.ARCADE_SEAT_UNLOCALIZED_NAME);
		setBlockBounds(0.2f, 0f, 0.2f, 0.8f, 0.625f, 0.8f);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityArcadeStool) {
			((TileEntityArcadeStool)te).activateByPlayer(player);
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityArcadeStool();
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>(1);
		list.add(new ItemStack(Items.arcadeSeat));
		return list;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(Items.arcadeSeat);
	}
}
