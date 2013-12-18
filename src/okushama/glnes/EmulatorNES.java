package okushama.glnes;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import npe.arcade.interfaces.IArcadeGame;
import npe.arcade.interfaces.IArcadeMachine;
import npe.arcade.tileentities.TileEntityArcade;

import org.lwjgl.input.Keyboard;

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
	public boolean nesStarted = false;
	public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
	public int loadDelay = 20;
	public String romTitle;
	
	public EmulatorNES(String romPath, String romName) {
		nesRom = romPath;
		romTitle = romName;
	}

	@Override
	public String getTitle() {
		return "NES Emulator: "+romTitle;
	}

	public BufferedImage getImage() {
		if(nes != null && nes.runEmulation){		
			if (nesOutput != null) {
				return nesOutput;
			}
		}
		if (gameIcon == null) 
		{
			gameIcon = new BufferedImage(machine.getScreenSize()[0], machine.getScreenSize()[1], BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) gameIcon.getGraphics();
			g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, machine.getScreenSize()[0], machine.getScreenSize()[1]);
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
			String[] output = {
					"NES EMULATOR",
					"Rom: "+romTitle,
					"",
					"Keys:",
					"Arrows -  D-Pad",
					"X      -  A",
					"Z      -  B",
					"Enter  -  Start",
					"Shift  -  Select",
					"Back   -  Quit Rom",
					"",
					"Press 'ENTER' to load",
					"Press  'BACK' to quit"};
			for(int i =0; i < output.length; i++){
				g.drawString(output[i], 10, 20+(i*16));
			}
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
		((TileEntityArcade)machine).setScreenResolution(256,224);
		nes = new NES(this);
		nes.setControllers(player1, player2);

	}
	
	public void loadRom(){
		nes.run(nesRom);
		nesStarted = true;
	}

	@Override
	public void unload() {
		if(nes.runEmulation){
			nes.quit();
		}
		nesStarted = false;
	}

	@Override
	public void doGameTick(List<KEY> input) {
		if(Minecraft.getMinecraft().thePlayer.username.equals(currentPlayer)){
			if(!nesStarted){
				loadDelay--;
				if(Keyboard.isKeyDown(Keyboard.KEY_RETURN) && loadDelay < 1){
					loadRom();
					loadDelay = 20;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_BACK)){
					((TileEntityArcade)machine).setGame(new RomDirectory());
				}
			}else{
				if(Keyboard.isKeyDown(Keyboard.KEY_BACK)){
					unload();
				}
			}
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
		if(!Minecraft.getMinecraft().thePlayer.username.equals(currentPlayer)){
			if(nes.runEmulation){
				nes.quit();
				this.nesStarted = false;
			}
		}
	}
}
