package npe.arcade.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityArcadeBase extends TileEntity {

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {}
}
