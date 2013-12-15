package npe.arcade.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import npe.arcade.items.Items;
import npe.arcade.tileentities.TileEntityArcadeBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockArcadeBase extends BlockContainer {

    public BlockArcadeBase(int id) {
        super(id, Material.iron);
        setHardness(2f);
        setStepSound(Block.soundMetalFootstep);
        setUnlocalizedName(BlockInfo.ARCADE_UNLOCALIZED_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        // anonymous class
        return new TileEntityArcadeBase();
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>(1);
        list.add(new ItemStack(Items.arcade));
        return list;
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion par5Explosion) {
        onBlockDestroyed(world, x, y, z, world.getBlockMetadata(x, y, z));
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
        onBlockDestroyed(world, x, y, z, meta);
    }

    private void onBlockDestroyed(World world, int x, int y, int z, int meta) {
        int yOffset = 1; // target the block on top
        if (this instanceof BlockArcadeTop) {
            yOffset = -1; // target the block below
        }

        int blockID = world.getBlockId(x, y + yOffset, z);
        if (BlockInfo.ARCADE_TOP_ID == blockID || BlockInfo.ARCADE_BASE_ID == blockID) {
            world.setBlockToAir(x, y + yOffset, z);
        }
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
        return new ItemStack(Items.arcade);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        // first assume the player is pointing at the bottom block
        double customMinY = 0, customMaxY = 2;

        // if the player points at the top block, shift the boundingbox down
        if (this instanceof BlockArcadeTop) {
            customMinY--;
            customMaxY--;
        }
        return AxisAlignedBB.getAABBPool().getAABB(x, y + customMinY, z, x + 1, y + customMaxY, z + 1);
    }
}
