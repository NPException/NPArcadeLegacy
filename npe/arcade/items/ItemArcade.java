package npe.arcade.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import npe.arcade.ArcadeMod;
import npe.arcade.entities.EntityArcade;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArcade extends Item {

    public ItemArcade(int id) {
        super(id);
        setCreativeTab(ArcadeMod.CREATIVE_TAB);
        setUnlocalizedName(ItemInfo.ARCADE_UNLOCALIZED_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register) {
        itemIcon = register.registerIcon(ItemInfo.TEXTURE_LOCATION + ":" + ItemInfo.ARCADE_ICON);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            world.spawnEntityInWorld(new EntityArcade(world, x + 0.5, y + 0.5, z + 0.5));
            stack.stackSize--;
            System.out.println("Successfully used!");
            return true;
        }
        else {
            return false;
        }
    }

}
