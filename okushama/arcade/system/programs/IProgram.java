package okushama.arcade.system.programs;

import java.awt.image.BufferedImage;

import okushama.arcade.system.OS;

public interface IProgram {

	public OS getOS();
	public void load();
	public void initialize();
	public void unload();
	public String getTitle();
	public void onKeyUp(int i);
	public void onKeyDown(int i);
	public BufferedImage getImage();
	public void onTick();
}
