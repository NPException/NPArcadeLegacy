package npe.arcade.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import npe.arcade.items.Items;
import npe.arcade.tileentities.TileEntityArcade;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockArcade extends BlockContainer {

    public BlockArcade(int id) {
        super(id, Material.iron);
        setHardness(2f);
        setStepSound(Block.soundMetalFootstep);
        setUnlocalizedName(BlockInfo.ARCADE_UNLOCALIZED_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityArcade();
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> list = new ArrayList<>(1);
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
        if (isTopPart(meta)) {
            yOffset = -1; // target the block below
        }
        if (BlockInfo.ARCADE_ID == world.getBlockId(x, y + yOffset, z)) {
            world.setBlockToAir(x, y + yOffset, z);
        }
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);

        // assume the player is pointing at the bottom block first
        double customMinY = 0, customMaxY = 2;

        // if the player points at the top block, shift the boundingbox down
        if (isTopPart(meta)) {
            customMinY--;
            customMaxY--;
        }
        return AxisAlignedBB.getAABBPool().getAABB(x + minX, y + customMinY, z + minZ, x + maxX, y + customMaxY, z + maxZ);
    }

    public static boolean isTopPart(int meta) {
        return (meta & 8) > 1;
    }

    public static int getFacing(int meta) {
        return meta & 7;
    }
}
