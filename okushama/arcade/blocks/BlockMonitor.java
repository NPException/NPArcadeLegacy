package okushama.arcade.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import npe.arcade.blocks.Blocks;
import okushama.arcade.tileentities.TileEntityMonitor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMonitor extends BlockContainer {

	public BlockMonitor(int id) {
		super(2078, Material.iron);
		setHardness(1f);
		setStepSound(Block.soundMetalFootstep);
		setUnlocalizedName("Monitor");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		// anonymous class
		return new TileEntityMonitor();
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>(1);
		list.add(new ItemStack(Blocks.monitor));
		return list;
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion par5Explosion) {
		super.onBlockDestroyedByExplosion(world, x, y, z, par5Explosion);
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
		super.onBlockDestroyedByPlayer(world, x, y, z, meta);
	}



	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		TileEntity te = world.getBlockTileEntity(x, y + 1, z);
		if (te instanceof TileEntityMonitor) {
			((TileEntityMonitor)te).hitByPlayer(player);
		}
	};

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
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return 15;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(Blocks.monitor);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		// first assume the player is pointing at the bottom block
		double customMinY = 0, customMaxY = 1;
		return AxisAlignedBB.getAABBPool().getAABB(x, y + customMinY, z, x + 1, y + customMaxY, z + 1);
	}

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack) {
		int meta = (MathHelper.floor_double(par5EntityLiving.rotationYaw * 4.0F / 360.0F + 2.5D) & 3);
		if(meta == 0) {
			meta = 2;
		}else if(meta == 2){
			meta = 0;
		}else if(meta == 1){
			meta = 3;
		}else if(meta == 3){
			meta = 1;
		}
		par1World.setBlockMetadataWithNotify(par2, par3, par4, meta, 2);
	}
}
