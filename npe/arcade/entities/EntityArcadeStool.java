package npe.arcade.entities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import npe.arcade.tileentities.TileEntityArcadeStool;

public class EntityArcadeStool extends Entity {

	private TileEntityArcadeStool stoolTE;

	public EntityArcadeStool(World world) {
		super(world);
	}

	public EntityArcadeStool(World world, TileEntityArcadeStool stoolTE, double x, double y, double z) {
		this(world);

		this.stoolTE = stoolTE;

		posX = x;
		posY = y;
		posZ = z;
	}

	@Override
	protected void entityInit() {
		setInvisible(true);
		setSize(0f, 0f);
	}

	@Override
	public void onEntityUpdate() {
		if (!worldObj.isRemote) {
			// check if stool tileEntity is still there. Set dead if not.
			if (stoolTE == null || stoolTE.isInvalid()) {
				stoolTE = null;
				setDead();
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public double getMountedYOffset() {
		return 0.5;
	}

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float par7, float par8, int par9) {
		// This fixes a bug where the player is shifted upwards sometimes, when sitting on the stool
		setPosition(x, y, z);
	}

	//

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {}
}
