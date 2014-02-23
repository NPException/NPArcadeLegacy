package okushama.arcade.system.programs;

import static java.awt.RenderingHints.*;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.minecraft.client.Minecraft;
import okushama.arcade.system.OS;

import org.lwjgl.input.Keyboard;

public class ProgramNotepad implements IProgram {

	public BufferedImage gameIcon;
	public OS os;
	public int ticks = 0;
	public String currentInput = "";
	public int backspaceDelay = 20;
	public int maxChars = 22;

	public ProgramNotepad(OS o) {
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
		int[] allKeys = new int[Keyboard.getKeyCount()];
		for (int i = 0; i < allKeys.length; i++) {
			if (i == Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode) {
				continue;
			}
			allKeys[i] = i;
		}
		getOS().registerKeys(this, allKeys);
	}

	@Override
	public void initialize() {}

	@Override
	public void unload() {
		ticks = 0;
	}

	@Override
	public String getTitle() {
		return "Notepad--";
	}

	@Override
	public void onKeyUp(int i) {

	}

	@Override
	public void onKeyDown(int i) {
		if (KeyboardInput.getChar(i) != '~') {
			currentInput += KeyboardInput.getChar(i);
		}
		if (i == Keyboard.KEY_BACK && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
			if (currentInput.length() > 1) {
				try {
					if (currentInput.substring(currentInput.length() - 1).equals(" ")) {
						while (currentInput.substring(currentInput.length() - 1).equals(" ")) {
							if (currentInput.length() < 3) {
								currentInput = "";
								break;
							}
							currentInput = currentInput.substring(0, currentInput.length() - 1);
						}
					}
					else {
						while (!currentInput.substring(currentInput.length() - 1).equals(" ")) {
							if (currentInput.length() < 3) {
								currentInput = "";
								break;
							}
							currentInput = currentInput.substring(0, currentInput.length() - 1);
						}
						currentInput += " ";
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				currentInput = "";
			}
		}
		if (i == Keyboard.KEY_BACK && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
			getOS().unloadProgram();
		}
		if (i == Keyboard.KEY_RETURN) {
			for (int j = lengthOfLastLine; j < maxChars; j++) {
				currentInput += " ";
			}
		}
		getOS().imageDirty = true;
	}

	public int lengthOfLastLine = 0;

	@Override
	public BufferedImage getImage() {
		if (gameIcon == null || getOS().imageDirty) {
			gameIcon = new BufferedImage(getOS().res.x, getOS().res.y,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)gameIcon.getGraphics();
			g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(getOS().getBackground());
			g.fillRect(0, 0, getOS().res.x, getOS().res.y);
			g.setColor(getOS().getForeground());
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
			String[] output = KeyboardInput.wrapText(currentInput, maxChars);
			for (int i = 0; i < output.length; i++) {
				g.drawString(output[i], 10, 20 + (i * 16));
			}
			int len = 0;
			int ylen = output.length - 1;
			try {
				len = output[output.length - 1].length();
				lengthOfLastLine = len;
				if (len >= maxChars - 1) {
					ylen += 1;
					len = 0;
				}
			}
			catch (Exception e) {

			}
			g.drawString(ticks % 20 > 10 ? "" : "|", 5 + (len * (11f)), 16 + (16.5f * (ylen)));
		}
		return gameIcon;
	}

	@Override
	public void onTick() {
		ticks++;
		if (Keyboard.isKeyDown(Keyboard.KEY_BACK) && Minecraft.getMinecraft().thePlayer.username.equals(getOS().currentPlayer)) {
			if (backspaceDelay == 10) {
				if (currentInput.length() > 1) {
					currentInput = currentInput.substring(0, currentInput.length() - 1);
				}
				else {
					currentInput = "";
				}
			}
			backspaceDelay--;
			if (backspaceDelay <= 0) {
				if (ticks % 2 == 1) {
					if (currentInput.length() > 1) {
						currentInput = currentInput.substring(0, currentInput.length() - 1);
					}
					else {
						currentInput = "";
					}
				}
			}
		}
		else {
			backspaceDelay = 10;
		}
	}

}
