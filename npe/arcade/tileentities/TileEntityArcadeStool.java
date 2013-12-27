package npe.arcade.tileentities;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import npe.arcade.entities.EntityArcadeStool;

public class TileEntityArcadeStool extends TileEntity {

	public static final Random RANDOM = new Random();

	public float placementRotation;

	private EntityArcadeStool stool;

	public TileEntityArcadeStool() {
		placementRotation = RANDOM.nextFloat() * 360f;
	}

	public void activateByPlayer(EntityPlayer player) {
		if (!worldObj.isRemote) {
			if (stool == null || stool.isDead) {
				stool = new EntityArcadeStool(worldObj, this, xCoord + 0.5, yCoord, zCoord + 0.5);
				worldObj.spawnEntityInWorld(stool);
			}
			if (stool.riddenByEntity == null) {
				player.mountEntity(stool);
			}
			else if (stool.riddenByEntity == player) {
				player.mountEntity(null);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setShort("Rotation", (short)Math.round(placementRotation));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		placementRotation = compound.getShort("Rotation");
	}
}
