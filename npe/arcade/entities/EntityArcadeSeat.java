package npe.arcade.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import npe.arcade.items.Items;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityArcadeSeat extends Entity implements IEntityAdditionalSpawnData {

    public final float placementRotation;

    public EntityArcadeSeat(World world) {
        super(world);
        setSize(0.4F, 0.625F);
        placementRotation = world.rand.nextFloat() * 360f;
    }

    public EntityArcadeSeat(World world, double x, double y, double z) {
        this(world);

        posX = x;
        posY = y;
        posZ = z;
    }

    @Override
    protected void entityInit() {}

    @Override
    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        if (entity != riddenByEntity) {
            return entity.boundingBox;
        }
        else {
            return null;
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead;
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (!worldObj.isRemote) {
            if (riddenByEntity == null) {
                player.mountEntity(this);
            }
            else if (riddenByEntity == player) {
                player.dismountEntity(this);
            }
        }

        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float par2) {

        if (!worldObj.isRemote) {

            Entity dmgEntity = damageSource.getEntity();
            boolean isCreative = false;
            if (dmgEntity instanceof EntityPlayer) {
                isCreative = ((EntityPlayer)dmgEntity).capabilities.isCreativeMode;
            }

            if (!isCreative) {
                float spawnX = (float)posX + worldObj.rand.nextFloat() - 0.5f;
                float spawnY = (float)posY + worldObj.rand.nextFloat() - 0.5f;
                float spawnZ = (float)posZ + worldObj.rand.nextFloat() - 0.5f;

                EntityItem droppedItem = new EntityItem(worldObj, spawnX, spawnY, spawnZ, new ItemStack(Items.arcadeSeat));

                float mult = 0.05F;

                droppedItem.motionX = (-0.5F + worldObj.rand.nextFloat()) * 2 * mult;
                droppedItem.motionY = (4 + worldObj.rand.nextFloat()) * mult;
                droppedItem.motionZ = (-0.5F + worldObj.rand.nextFloat()) * 2 * mult;

                worldObj.spawnEntityInWorld(droppedItem);
            }

        }
        setDead();
        return true;
    }

    @Override
    public double getMountedYOffset() {
        return 0.5;
    }

    @Override
    public void writeSpawnData(ByteArrayDataOutput data) {}

    @Override
    public void readSpawnData(ByteArrayDataInput data) {}
}
