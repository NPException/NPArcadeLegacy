package npe.arcade.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import npe.arcade.items.Items;
import npe.arcade.tileentities.TileEntityArcade;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityArcadeSeat extends Entity implements IEntityAdditionalSpawnData {

    private TileEntityArcade arcadeMachine;

    public final float placementRotation;
    private Entity lastRiddenByEntity; // TODO: use to force player next to the seat if he dismounts

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
    public void onUpdate() {
        // check if the arcadeMachine is still there
        if (arcadeMachine != null && arcadeMachine.isInvalid()) {
            arcadeMachine.setOccupiedBySeat(null);
            arcadeMachine = null;
        }

        //        if (riddenByEntity == null && lastRiddenByEntity != null) {
        //            int posX = (int)this.posX;
        //            int posY = (int)this.posY;
        //            int posZ = (int)this.posZ;
        //            boolean continueLoop = true;
        //            for (int x = -1; x < 3; x++) {
        //                if (!continueLoop) {
        //                    break;
        //                }
        //                for (int z = -1; z < 3; z++) {
        //                    if (x != 0 && z != 0) {
        //                        if (worldObj.isAirBlock(posX + x, posY, posZ + z) && worldObj.isAirBlock(posX + x, posY + 1, posZ + z)) {
        //                            lastRiddenByEntity.setPosition(posX + x, posY, posZ + z);
        //                            continueLoop = false;
        //                            break;
        //                        }
        //                    }
        //                }
        //            }
        //            lastRiddenByEntity = null;
        //        }
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
                lastRiddenByEntity = player;
                player.mountEntity(this);
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

                EntityItem droppedItem = new EntityItem(worldObj, posX, posY + 0.2, posZ, new ItemStack(Items.arcadeSeat));

                float mult = 0.05F;

                droppedItem.motionX = (-0.5F + worldObj.rand.nextFloat()) * 1.5f * mult;
                droppedItem.motionY = (4 + worldObj.rand.nextFloat()) * mult;
                droppedItem.motionZ = (-0.5F + worldObj.rand.nextFloat()) * 1.5f * mult;

                worldObj.spawnEntityInWorld(droppedItem);
            }

        }
        setDead();
        return true;
    }

    @Override
    public void setDead() {
        occupyArcade(null);
        super.setDead();
    }

    @Override
    public double getMountedYOffset() {
        return 0.5;
    }

    @Override
    public void onEntityUpdate() {
        // TODO Auto-generated method stub
        super.onEntityUpdate();
    }

    /**
     * Called when the seat occupies or "un"-occupies an arcademachine<br>
     * (copy pasta from mountEntity(...) code)
     */
    public void occupyArcade(TileEntityArcade arcade)
    {
        if (arcade == null)
        {
            if (arcadeMachine != null)
            {
                arcadeMachine.setOccupiedBySeat(null);
            }
            arcadeMachine = null;
        }
        else
        {
            if (arcadeMachine != null)
            {
                arcadeMachine.setOccupiedBySeat(null);
            }

            arcadeMachine = arcade;
            arcadeMachine.setOccupiedBySeat(this);
        }
    }

    public TileEntityArcade getOccupiedArcadeMachine() {
        return arcadeMachine;
    }

    @Override
    public ItemStack getPickedResult(MovingObjectPosition target) {
        return new ItemStack(Items.arcadeSeat);
    }

    //

    // these are set by readFromNBTData. if these are not null, grab the arcademachine in writeSpawnData
    private int[] nbtArcadeCoords;

    @Override
    public void writeSpawnData(ByteArrayDataOutput data) {
        if (nbtArcadeCoords != null) {
            TileEntity tileEntity = worldObj.getBlockTileEntity(nbtArcadeCoords[0], nbtArcadeCoords[1], nbtArcadeCoords[2]);
            if (tileEntity instanceof TileEntityArcade) {
                occupyArcade((TileEntityArcade)tileEntity);
            }
            nbtArcadeCoords = null;
        }
        data.writeBoolean(arcadeMachine != null);
        if (arcadeMachine != null) {
            data.writeInt(arcadeMachine.xCoord);
            data.writeInt(arcadeMachine.yCoord);
            data.writeInt(arcadeMachine.zCoord);
        }
    }

    @Override
    public void readSpawnData(ByteArrayDataInput data) {
        boolean occupiesArcadeMachine = data.readBoolean();
        if (occupiesArcadeMachine) {
            int x = data.readInt();
            int y = data.readInt();
            int z = data.readInt();
            TileEntityArcade arcade = (TileEntityArcade)worldObj.getBlockTileEntity(x, y, z); // TODO: maybe a instanceof check will be necessary here someday
            occupyArcade(arcade);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setBoolean("occupiesArcade", arcadeMachine != null);
        if (arcadeMachine != null) {
            compound.setIntArray("arcadeMachineCoords", new int[] { arcadeMachine.xCoord, arcadeMachine.yCoord, arcadeMachine.zCoord });
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        boolean occupiesArcade = compound.getBoolean("occupiesArcade");
        if (occupiesArcade && compound.hasKey("arcadeMachineCoords")) {
            nbtArcadeCoords = compound.getIntArray("arcadeMachineCoords");
        }
    }
}
