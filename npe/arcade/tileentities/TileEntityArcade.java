package npe.arcade.tileentities;

import net.minecraft.tileentity.TileEntity;
import npe.arcade.blocks.BlockArcade;

public class TileEntityArcade extends TileEntity {

    public boolean isTopPart() {
        return (BlockArcade.isTopPart(getBlockMetadata()));
    }

    public int getFacing() {
        return (BlockArcade.getFacing(getBlockMetadata()));
    }
}
