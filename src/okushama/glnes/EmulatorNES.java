package okushama.glnes;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import npe.arcade.interfaces.IArcadeGame;
import npe.arcade.interfaces.IArcadeMachine;
import okushama.glnes.InputControls;

import com.grapeshot.halfnes.GUIImpl;
import com.grapeshot.halfnes.NES;

public class EmulatorNES implements IArcadeGame {

	public IArcadeMachine machine;
	public BufferedImage gameIcon;
	public BufferedImage nesOutput = null;
	public InputControls player1 = new InputControls(1);
	public InputControls player2 = new InputControls(1);
	public String currentPlayer = null;
	public String nesRom;
	public NES nes;

	public EmulatorNES(String romPath) {
		nesRom = romPath;
		if (nes == null){
			nes = new NES(this);
		}
		nes.setControllers(player1, player2);
	}

	@Override
	public String getTitle() {
		return "NES Emulator";
	}

	public BufferedImage getImage() {
		if (nesOutput != null) {
			return nesOutput;
		}
		if (gameIcon == null) {
			gameIcon = new BufferedImage(32, 16, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) gameIcon.getGraphics();
			g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setBackground(new Color(255, 255, 255, 0));
			g.clearRect(0, 0, 32, 16);
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(1.5f));
			g.drawRoundRect(1, 1, 30, 14, 3, 3);
			g.drawRoundRect(5, 5, 22, 6, 1, 1);
			g.setColor(Color.ORANGE);
			g.drawRoundRect(3, 3, 26, 10, 2, 2);
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
	public void initialize() {
		nes.reset();
		nes.run(nesRom);
	}

	@Override
	public void unload() {

	}

	public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();

	@Override
	public void doGameTick(List<KEY> input) {
		if(Minecraft.getMinecraft().thePlayer.username.equals(currentPlayer)){
			for (int key : player1.keys.keySet()) {
				if (Keyboard.isKeyDown(key)) {
					if (pressedKeys.containsKey(key)) {
						if (pressedKeys.get(key)) {
							continue;
						}
					}
					player1.onKeyDown(key);
					pressedKeys.put(key, true);
				}
			}
			Integer[] keys = pressedKeys.keySet().toArray(new Integer[0]);
			for (int i = 0; i < keys.length; i++) {
				if (!Keyboard.isKeyDown(keys[i])) {
					pressedKeys.put(keys[i], false);
					player1.onKeyUp(keys[i]);
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
		if(Minecraft.getMinecraft().thePlayer.username.equals(currentPlayer)){
			if(!nes.runEmulation){
				initialize();
			}
		}else{
			if(nes.runEmulation){
				nes.quit();
			}
		}
	}
}
