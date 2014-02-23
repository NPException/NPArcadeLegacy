package okushama.arcade.system.programs.gb;

import static java.awt.RenderingHints.*;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okushama.arcade.system.OS;
import okushama.arcade.system.programs.IProgram;

import org.lwjgl.input.Keyboard;

public class ProgramGBDirectory implements IProgram {

	public BufferedImage gameIcon;
	public boolean imageDirty = false;
	public List<Rom> roms = new ArrayList<Rom>();
	private int current = 0;
	private final OS os;

	public ProgramGBDirectory(OS o) {
		os = o;
		File romDirectory = new File("roms/gb");
		if (!romDirectory.exists()) {
			romDirectory.mkdirs();
		}
		for (File file : romDirectory.listFiles()) {
			if (file.getName().endsWith(".gb") || file.getName().endsWith(".gbc")) {
				roms.add(new Rom(file.getName(), file.getAbsolutePath()));
			}
		}
	}

	@Override
	public void load() {
		getOS().registerKey(this, Keyboard.KEY_DOWN);
		getOS().registerKey(this, Keyboard.KEY_UP);
		getOS().registerKey(this, Keyboard.KEY_RETURN);
		getOS().registerKey(this, Keyboard.KEY_BACK);
	}

	@Override
	public String getTitle() {
		return "Gameboy Emulator";
	}

	@Override
	public BufferedImage getImage() {
		if (gameIcon == null || imageDirty || getOS().imageDirty)
		{
			gameIcon = new BufferedImage(getOS().res.x, getOS().res.y, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)gameIcon.getGraphics();
			g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(getOS().getBackground());
			g.fillRect(0, 0, getOS().res.x, getOS().res.y);
			g.setColor(getOS().getForeground());
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
			g.drawString("CHOOSE A ROM", 10, 20);
			int markerOffset = 0;
			if (current > 11) {
				int offset = 11 - current;
				markerOffset = offset;
			}
			g.drawString(">", 10, 36 + ((current + markerOffset) * 16));

			String[] output = new String[roms.size()];
			for (int i = 0; i < output.length; i++) {
				output[i] = roms.get(i).name;
			}

			if (output.length == 0) {
				output = new String[] { "NO ROMS FOUND!" };
			}
			int offset = 0;
			if (current > 11) {
				offset = current - 11;
			}
			for (int i = offset; i < output.length; i++) {
				if (i < 12 + offset)
				{
					g.drawString(output[i], 30, 36 + ((i - offset) * 16));
				}
			}
			imageDirty = false;
		}
		return gameIcon;
	}

	@Override
	public void initialize() {}

	@Override
	public void unload() {
		current = 0;
	}

	@Override
	public void onTick() {

	}

	@Override
	public void onKeyUp(int i) {

	}

	@Override
	public void onKeyDown(int i) {
		if (i == Keyboard.KEY_DOWN) {
			if (current < roms.size() - 1) {
				current++;
			}
			else {
				current = 0;
			}
			imageDirty = true;
		}
		if (i == Keyboard.KEY_UP) {
			if (current > 0) {
				current--;
			}
			else {
				current = roms.size() - 1;
			}
			imageDirty = true;

		}
		if (i == Keyboard.KEY_RETURN) {
			if (roms.size() > 0) {
				Rom rom = roms.get(current);
				getOS().loadProgram(new ProgramGBEmulator(getOS(), rom.path, rom.name));
			}
		}
		if (i == Keyboard.KEY_BACK) {
			getOS().unloadProgram();
		}
	}

	@Override
	public OS getOS() {
		return os;
	}

	/**
	 * Small wrapper class for the ROMs
	 */
	private class Rom {
		private final String name;
		private final String path;

		private Rom(String name, String path) {
			this.name = name;
			this.path = path;
		}
	}
}
