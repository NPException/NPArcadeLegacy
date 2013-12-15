package npe.arcade.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import npe.arcade.tileentities.TileEntityArcade;

public class BlockArcadeTop extends BlockArcadeBase {

    public BlockArcadeTop(int id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityArcade();
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
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        ((TileEntityArcade)world.getBlockTileEntity(x, y, z)).hitByPlayer(player);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int facing = world.getBlockMetadata(x, y, z);
        float[] bounds = { 0f, 0f, 0f, 1f, 1f, 1f };

        // changing parts of the bounding box depending on the facing
        switch (facing) {

            case 0: {
                bounds[5] = 0.375f;
                break;
            }

            case 1: {
                bounds[0] = 0.625f;
                break;
            }

            case 2: {
                bounds[2] = 0.625f;
                break;
            }

            case 3: {
                bounds[3] = 0.375f;
                break;
            }
        }

        AxisAlignedBB blockbounds = AxisAlignedBB.getAABBPool().getAABB(x + bounds[0], y + bounds[1], z + bounds[2], x + bounds[3], y + bounds[4], z + bounds[5]);
        return blockbounds;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3) {
        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
    }
}
