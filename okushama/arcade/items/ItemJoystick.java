package okushama.arcade.items;


import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemJoystick extends Item {

	public String iconImg;

	public ItemJoystick(int par1) {

		super(par1);
		iconImg = "joystickItem";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {

		itemIcon = iconRegister.registerIcon("npearcade" + ":" + iconImg);
	}





}