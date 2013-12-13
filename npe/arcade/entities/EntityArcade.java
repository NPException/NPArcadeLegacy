package npe.arcade.entities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityArcade extends Entity {

    public EntityArcade(World world) {
        super(world);
    }

    public EntityArcade(World world, double x, double y, double z) {
        this(world);

        posX = x;
        posY = y;
        posZ = z;
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(20, (byte)0);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!worldObj.isRemote) {
            // server side stuff
        }
        else {
            // client side stuff
        }
    }
}
