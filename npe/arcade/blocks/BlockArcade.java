package npe.arcade.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import npe.arcade.ArcadeMod;
import npe.arcade.tileentities.TileEntityArcade;

public class BlockArcade extends BlockContainer {

    public BlockArcade(int id) {
        super(id, Material.circuits);
        setCreativeTab(ArcadeMod.CREATIVE_TAB);
        setHardness(2f);
        setStepSound(Block.soundMetalFootstep);
        setUnlocalizedName(BlockInfo.ARCADE_UNLOCALIZED_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityArcade();
    }

}
