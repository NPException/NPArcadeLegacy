package okushama.arcade.system.programs;

import static java.awt.RenderingHints.*;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import okushama.arcade.system.OS;

public class ProgramReloadSettings implements IProgram {

	public BufferedImage gameIcon;
	public OS os;
	int ticks = 0;

	public ProgramReloadSettings(OS o) {
		os = o;
	}

	@Override
	public OS getOS() {
		return os;
	}

	@Override
	public void load() {
		ticks = 0;
		getOS().reloadSettings();
	}

	@Override
	public void initialize() {

	}

	@Override
	public void unload() {
		ticks = 0;
	}

	@Override
	public String getTitle() {
		return "Reload Settings";
	}

	@Override
	public void onKeyUp(int i) {

	}

	@Override
	public void onKeyDown(int i) {

	}

	@Override
	public BufferedImage getImage() {
		if (gameIcon == null || getOS().imageDirty) {
			gameIcon = new BufferedImage(getOS().resX, getOS().resY,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) gameIcon.getGraphics();
			g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(getOS().getBackground());
			g.fillRect(0, 0, getOS().resX, getOS().resY);
			g.setColor(getOS().getForeground());
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
			g.drawString("RELOADED SETTINGS", 10, 20);
			g.drawString("Now returning you", 10, 36);
			g.drawString("to the main menu", 10, 52);
		}
		return gameIcon;
	}

	@Override
	public void onTick() {
		ticks++;
		if (ticks >= 30) {
			getOS().reloadSettings();
			getOS().unloadProgram();
		}
	}

}
