package npe.arcade.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import npe.arcade.ArcadeMod;
import npe.arcade.entities.EntityArcadeSeat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArcadeSeat extends Item {

    public ItemArcadeSeat(int id) {
        super(id);
        setCreativeTab(ArcadeMod.CREATIVE_TAB);
        setUnlocalizedName(ItemInfo.ARCADE_SEAT_UNLOCALIZED_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register) {
        itemIcon = register.registerIcon(ItemInfo.TEXTURE_LOCATION + ":" + ItemInfo.ARCADE_SEAT_ICON);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            ForgeDirection direction = ForgeDirection.getOrientation(side);
            if (world.isAirBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ)) {

                EntityArcadeSeat seat = new EntityArcadeSeat(world);

                seat.posX = x + direction.offsetX + 0.5;
                seat.posY = y + direction.offsetY;
                seat.posZ = z + direction.offsetZ + 0.5;

                seat.rotationYaw = player.cameraYaw;

                world.spawnEntityInWorld(seat);

                if (!player.capabilities.isCreativeMode) {
                    stack.stackSize--;
                }
                return true;
            }
        }
        return false;
    }
}
