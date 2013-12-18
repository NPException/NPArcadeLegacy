package okushama.glnes;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import npe.arcade.interfaces.IArcadeGame;
import npe.arcade.interfaces.IArcadeMachine;
import npe.arcade.tileentities.TileEntityArcade;

import org.lwjgl.input.Keyboard;

public class RomDirectory implements IArcadeGame {

	public IArcadeMachine machine;
	public BufferedImage gameIcon;
	public String currentPlayer = null;
	public boolean imageDirty = false;
	public Map<String, String> roms = new HashMap<String, String>();
	private int current = 0;
	public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();

	public RomDirectory() {
		File romDirectory = new File("roms");
		if(!romDirectory.exists()){
			romDirectory.mkdir();
		}
		for(File file : romDirectory.listFiles()){
			if(file.getName().endsWith(".nes")){
				roms.put(file.getName(), file.getAbsolutePath());
			}
		}
	}

	@Override
	public String getTitle() {
		return "NES Emulator Rom Directory";
	}

	public BufferedImage getImage() {
		if (gameIcon == null || imageDirty) 
		{
			gameIcon = new BufferedImage(machine.getScreenSize()[0], machine.getScreenSize()[1], BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) gameIcon.getGraphics();
			g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, machine.getScreenSize()[0], machine.getScreenSize()[1]);
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
			g.drawString("NES ROM DIRECTORY", 10, 20);
			int markerOffset = 0;
			if(current > 11){
				int offset = 11 - current;
				markerOffset = offset;
			}
			g.drawString(">", 10, 36+((current+markerOffset)*16));
			
			String[] output = roms.keySet().toArray(new String[0]);
			if(output.length == 0){
				output = new String[]{"NO ROMS FOUND!"};
			}
			if(output.length == 0){
				g.setColor(Color.RED);
			}
			int offset = 0;
			if(current > 11){
				offset = current - 11;
			}
			for(int i = offset; i < output.length; i++){
				if(i < 12 + offset)
				{
					g.drawString(output[i], 30, 36+((i-offset)*16));
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
	public void initialize() {
		((TileEntityArcade)machine).setScreenResolution(256,224);
	
	}

	@Override
	public void unload() {
	
	}

	public Map<Integer, String> keys = new HashMap<Integer, String>(){
		{
			put(Keyboard.KEY_DOWN, "down");
			put(Keyboard.KEY_UP, "up");
			put(Keyboard.KEY_RETURN, "enter");
		}
	};
	
	public void onKeyDown(int key, String name){
		if(name.equals("down")){
			if(current < this.roms.size()-1){
				current++;
			}else{
				current = 0;
			}
			imageDirty = true;
		}
		if(name.equals("up")){
			if(current > 0){
				current--;
			}else{
				current = roms.size()-1;
			}
			imageDirty = true;

		}
		if(name.equals("enter")){
			String pathToRom = roms.values().toArray(new String[0])[current];
			String romName = roms.keySet().toArray(new String[0])[current];
			((TileEntityArcade)machine).setGame(new EmulatorNES(pathToRom, romName));
		}
	}
	
	public void onKeyUp(int key, String name){
		
	}

	@Override
	public void doGameTick(List<KEY> input) {
		if(Minecraft.getMinecraft().thePlayer.username.equals(currentPlayer)){
			for (int key : keys.keySet()) {
				if (Keyboard.isKeyDown(key)) {
					if (pressedKeys.containsKey(key)) {
						if (pressedKeys.get(key)) {
							continue;
						}
					}
					onKeyDown(key, keys.get(key));
					pressedKeys.put(key, true);
				}
			}
			Integer[] downkeys = pressedKeys.keySet().toArray(new Integer[0]);
			for (int i = 0; i < downkeys.length; i++) {
				if (!Keyboard.isKeyDown(downkeys[i])) {
					pressedKeys.put(downkeys[i], false);
					onKeyUp(downkeys[i], keys.get(downkeys[i]));
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
	}
}
