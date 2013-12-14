package npe.arcade.items;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import npe.arcade.ArcadeMod;
import npe.arcade.blocks.BlockInfo;
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
            ForgeDirection direction = ForgeDirection.getOrientation(side);
            if ((world.getBlockId(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ) == 0 || world.getBlockMaterial(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ) == Material.air)
                    && (world.getBlockId(x + direction.offsetX, y + 1 + direction.offsetY, z + direction.offsetZ) == 0 || world.getBlockMaterial(x + direction.offsetX, y + 1 + direction.offsetY, z + direction.offsetZ) == Material.air)) {

                if (world.setBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, BlockInfo.ARCADE_ID)) {
                    world.setBlock(x + direction.offsetX, y + 1 + direction.offsetY, z + direction.offsetZ, BlockInfo.ARCADE_ID);

                    int whichDirectionFacing = MathHelper
                            .floor_double(player.rotationYaw * 4.0F / 360.0F + 2.5D) & 3;
                    // set the facing as metadata to the bottom block
                    world.setBlockMetadataWithNotify(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, whichDirectionFacing, 3);
                    // set the state of being the top block in the metadatas high bit -> 8 = 1000b
                    world.setBlockMetadataWithNotify(x + direction.offsetX, y + 1 + direction.offsetY, z + direction.offsetZ, 8, 3);
                    if (!player.capabilities.isCreativeMode) {
                        stack.stackSize--;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
