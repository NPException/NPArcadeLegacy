package okushama.arcade.system;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import npe.arcade.interfaces.IArcadeGame;
import npe.arcade.interfaces.IArcadeMachine;
import npe.arcade.tileentities.TileEntityArcade;

import okushama.glnes.EmulatorNES;
import okushama.glnes.RomDirectory;

import org.lwjgl.input.Keyboard;

public class OS implements IArcadeGame {

	public IArcadeMachine machine;
	public BufferedImage gameIcon;
	public String currentPlayer = null;
	public boolean imageDirty = false;
	public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
	private IProgram currentProgram;
	private int currentSelection = 0;
	public List<IProgram> programs = new ArrayList<IProgram>();	
	public Map<String, ArrayList<Integer>> keys = new HashMap<String, ArrayList<Integer>>();

	@Override
	public void initialize() {
		((TileEntityArcade)machine).setScreenResolution(256,224);
		programs.add(new RomDirectory(this));
		keys.put("os", new ArrayList<Integer>(){
			{
				add(Keyboard.KEY_DOWN);
				add(Keyboard.KEY_UP);
				add(Keyboard.KEY_RETURN);
			}
		});
	}
	
	@Override
	public String getTitle() {
		return "okushama OS";
	}
	
	public void unloadProgram(){
		currentProgram = null;
	}

	public BufferedImage getImage() {
		if(currentProgram != null){
			return currentProgram.getImage();
		}
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
			g.drawString(getTitle(), 10, 20);
			int markerOffset = 0;
			if(currentSelection > 11){
				int offset = 11 - currentSelection;
				markerOffset = offset;
			}
			g.drawString(">", 10, 36+((currentSelection+markerOffset)*16));
			
			ArrayList<String> output = new ArrayList<String>();
			for(IProgram p : programs){
				output.add(p.getTitle());
			}
			
			if(output.size() == 0){
				output.add("NO PROGRAMS FOUND!");
			}
			if(output.size() == 0){
				g.setColor(Color.RED);
			}
			int offset = 0;
			if(currentSelection > 11){
				offset = currentSelection - 11;
			}
			for(int i = offset; i < output.size(); i++){
				if(i < 12 + offset)
				{
					g.drawString(output.get(i), 30, 36+((i-offset)*16));
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
		if(currentProgram != null){
			currentProgram.unload();
		}
	}
	
	public void onKeyDown(int key){
		if(currentProgram != null && keys.get(currentProgram.getTitle()).contains(key)){
			currentProgram.onKeyDown(key);
		}else{
			if(key == Keyboard.KEY_DOWN){
				if(currentSelection < programs.size()-1){
					currentSelection++;
				}else{
					currentSelection = 0;
				}
				imageDirty = true;
			}
			if(key == Keyboard.KEY_UP){
				if(currentSelection > 0){
					currentSelection--;
				}else{
					currentSelection = programs.size()-1;
				}
				imageDirty = true;

			}
			if(key == Keyboard.KEY_RETURN){
				this.loadProgram(programs.get(currentSelection));
			}
		}
	}
	
	public void onKeyUp(int key){
		if(currentProgram != null){
			currentProgram.onKeyUp(key);
		}
	}

	@Override
	public void doGameTick(List<KEY> input) {
		if(currentProgram != null){
			currentProgram.onTick();
		}
		if(Minecraft.getMinecraft().thePlayer.username.equals(currentPlayer)){
			ArrayList<ArrayList<Integer>> keysets = new ArrayList<ArrayList<Integer>>(keys.values());
			for(int i = 0; i < keysets.size(); i++){
				ArrayList<Integer> keyset = keysets.get(i);
				for(int j = 0; j < keyset.size(); j++){
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
		if(Minecraft.getMinecraft().thePlayer.username.equals(currentPlayer)){
			float zoomSize = -0.5f;
			if(Minecraft.getMinecraft().gameSettings.fovSetting > zoomSize){
				Minecraft.getMinecraft().gameSettings.fovSetting -= 0.05;
				if(GuiIngameForge.renderCrosshairs){
					GuiIngameForge.renderCrosshairs = false;
				}
			}
		}else{
			if(Minecraft.getMinecraft().gameSettings.fovSetting < 0){
				Minecraft.getMinecraft().gameSettings.fovSetting += 0.05;
				if(!GuiIngameForge.renderCrosshairs){
					GuiIngameForge.renderCrosshairs = true;
				}
			}
		}
	}
	
	public void loadProgram(IProgram p){
		currentProgram = p;
		currentProgram.load();
		currentProgram.initialize();
	}
	
	public void registerKey(IProgram toProgram, int key){
		if(keys.containsKey(toProgram.getTitle()) && keys.get(toProgram.getTitle()) != null){
			keys.get(toProgram.getTitle()).add(key);
			System.out.println("Registered key! " + toProgram.getTitle() + " " + key);
		}else{
			ArrayList<Integer> keyz = new ArrayList<Integer>();
			keyz.add(key);
			keys.put(toProgram.getTitle(), keyz);
			System.out.println("Registered key, created new keyset! " + toProgram.getTitle() + " " + key);
		}
	}
	
	public void unregisterKey(IProgram toProgram, int key){
		if(keys.containsKey(toProgram.getTitle())){
			if(keys.get(toProgram).contains(key)){
				keys.get(toProgram).remove(key);
				System.out.println("Unregistered key! " + toProgram.getTitle() + " " + key);
			}
		}
	}
}
