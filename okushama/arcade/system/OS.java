package okushama.arcade.system;

import static java.awt.RenderingHints.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import npe.arcade.interfaces.IArcadeGame;
import npe.arcade.interfaces.IArcadeMachine;
import okushama.arcade.system.programs.IProgram;
import okushama.arcade.system.programs.ProgramReloadSettings;
import okushama.arcade.system.programs.nes.ProgramNESDirectory;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLLog;

public class OS implements IArcadeGame {

	public final int resX = 256;
	public final int resY = 224;

	private static OSSettings settings = OSSettings.load();
	public static OSLogger logger = new OSLogger("okushama OS");
	
	public IArcadeMachine machine;
	public BufferedImage gameIcon;
	public String currentPlayer = null;
	public boolean imageDirty = false;
	public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
	private IProgram currentProgram;
	private int currentSelection = 0;
	public List<IProgram> programs = new ArrayList<IProgram>();
	public Map<String, List<Integer>> keys = new HashMap<String, List<Integer>>();

	@Override
	public void initialize() {
		programs.add(new ProgramNESDirectory(this));
		programs.add(new ProgramReloadSettings(this));
		Integer[] keyList = new Integer[] { Keyboard.KEY_DOWN, Keyboard.KEY_UP, Keyboard.KEY_RETURN };
		keys.put("os", Arrays.asList(keyList));
	}

	@Override
	public String getTitle() {
		return "okushama OS";
	}

	public void unloadProgram() {
		currentProgram = null;
	}

	private Color osBackground = null;
	private Color osForeground = null;

	public void reloadSettings() {
		osBackground = null;
		osForeground = null;
		settings = OSSettings.load();
		imageDirty = true;
	}

	public Color getBackground() {
		if (osBackground == null) {
			logger.log("Remapping colour!");
			osBackground = new Color(settings.colourBackground[0], settings.colourBackground[1], settings.colourBackground[2]);
		}
		return osBackground;
	}

	public Color getForeground() {
		if (osForeground == null) {
			logger.log("Remapping colour!");
			osForeground = new Color(settings.colourForeground[0], settings.colourForeground[1], settings.colourForeground[2]);
		}
		return osForeground;
	}

	public BufferedImage getImage() {
		if (currentProgram != null) {
			return currentProgram.getImage();
		}
		if (gameIcon == null || imageDirty)
		{
			gameIcon = new BufferedImage(resX, resY, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)gameIcon.getGraphics();
			g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(getBackground());
			g.fillRect(0, 0, resX, resY);
			g.setColor(getForeground());
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
			g.drawString(getTitle(), 10, 20);
			int markerOffset = 0;
			if (currentSelection > 11) {
				int offset = 11 - currentSelection;
				markerOffset = offset;
			}
			g.drawString(">", 10, 36 + ((currentSelection + markerOffset) * 16));

			ArrayList<String> output = new ArrayList<String>();
			for (IProgram p : programs) {
				output.add(p.getTitle());
			}

			if (output.size() == 0) {
				output.add("NO PROGRAMS FOUND!");
			}
			if (output.size() == 0) {
				g.setColor(Color.RED);
			}
			int offset = 0;
			if (currentSelection > 11) {
				offset = currentSelection - 11;
			}
			for (int i = offset; i < output.size(); i++) {
				if (i < 12 + offset)
				{
					g.drawString(output.get(i), 30, 36 + ((i - offset) * 16));
				}
			}
			imageDirty = false;
		}
		return gameIcon;
	}

	@Override
	public BufferedImage getGameIcon() {
		return getImage();
	}

	@Override
	public void setArcadeMachine(IArcadeMachine arcadeMachine) {
		machine = arcadeMachine;
	}

	@Override
	public void unload() {
		if (currentProgram != null) {
			currentProgram.unload();
		}
	}

	public void onKeyDown(int key) {
		if (currentProgram != null && keys.get(currentProgram.getTitle()) != null && keys.get(currentProgram.getTitle()).contains(key)) {
			currentProgram.onKeyDown(key);
		}
		else {
			if (key == Keyboard.KEY_DOWN) {
				if (currentSelection < programs.size() - 1) {
					currentSelection++;
				}
				else {
					currentSelection = 0;
				}
				imageDirty = true;
			}
			if (key == Keyboard.KEY_UP) {
				if (currentSelection > 0) {
					currentSelection--;
				}
				else {
					currentSelection = programs.size() - 1;
				}
				imageDirty = true;

			}
			if (key == Keyboard.KEY_RETURN) {
				loadProgram(programs.get(currentSelection));
			}
		}
	}

	public void onKeyUp(int key) {
		if (currentProgram != null) {
			currentProgram.onKeyUp(key);
		}
	}

	@Override
	public void doGameTick(List<KEY> input) {
		if (currentProgram != null) {
			currentProgram.onTick();
		}
		if (Minecraft.getMinecraft().thePlayer.username.equals(currentPlayer)) {
			List<List<Integer>> keysets = new ArrayList<List<Integer>>(keys.values());
			for (int i = 0; i < keysets.size(); i++) {
				List<Integer> keyset = keysets.get(i);
				for (int j = 0; j < keyset.size(); j++) {
					int key = keyset.get(j);
					if (Keyboard.isKeyDown(key)) {
						if (pressedKeys.containsKey(key)) {
							if (pressedKeys.get(key)) {
								continue;
							}
						}
						onKeyDown(key);
						pressedKeys.put(key, true);
					}
				}
			}
			Integer[] downkeys = pressedKeys.keySet().toArray(new Integer[0]);
			for (int i = 0; i < downkeys.length; i++) {
				if (!Keyboard.isKeyDown(downkeys[i])) {
					pressedKeys.put(downkeys[i], false);
					onKeyUp(downkeys[i]);
				}
			}
		}
	}

	@Override
	public BufferedImage renderGraphics() {
		return getImage();
	}

	@Override
	public void setCurrentPlayerName(String playername) {
		currentPlayer = playername;
		if (Minecraft.getMinecraft().thePlayer.username.equals(currentPlayer)) {
			float zoomSize = -0.5f;
			if (Minecraft.getMinecraft().gameSettings.fovSetting > zoomSize) {
				Minecraft.getMinecraft().gameSettings.fovSetting -= 0.05;
				if (GuiIngameForge.renderCrosshairs) {
					GuiIngameForge.renderCrosshairs = false;
				}
			}
		}
		else {
			if (Minecraft.getMinecraft().gameSettings.fovSetting < 0) {
				Minecraft.getMinecraft().gameSettings.fovSetting += 0.05;
				if (!GuiIngameForge.renderCrosshairs) {
					GuiIngameForge.renderCrosshairs = true;
				}
			}
		}
	}

	public void loadProgram(IProgram p) {
		currentProgram = p;
		currentProgram.load();
		currentProgram.initialize();
	}

	public void registerKey(IProgram toProgram, int key) {
		if (keys.containsKey(toProgram.getTitle()) && keys.get(toProgram.getTitle()) != null) {
			keys.get(toProgram.getTitle()).add(key);
			logger.log("Registered key! " + toProgram.getTitle() + " " + key);
		}
		else {
			ArrayList<Integer> keyz = new ArrayList<Integer>();
			keyz.add(key);
			keys.put(toProgram.getTitle(), keyz);
			logger.log("Registered key, created new keyset! " + toProgram.getTitle() + " " + key);
		}
	}

	public void unregisterKey(IProgram toProgram, int key) {
		if (keys.containsKey(toProgram.getTitle())) {
			if (keys.get(toProgram).contains(key)) {
				keys.get(toProgram).remove(key);
				logger.log("Unregistered key! " + toProgram.getTitle() + " " + key);
			}
		}
	}
}
