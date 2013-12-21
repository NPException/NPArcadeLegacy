package npe.arcade.tileentities;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityArcadeStool extends TileEntity {

	public static final Random RANDOM = new Random();

	public final float placementRotation;

	public TileEntityArcadeStool() {
		placementRotation = RANDOM.nextFloat() * 360f;
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {}
}
