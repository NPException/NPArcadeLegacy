package okushama.glnes;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import npe.arcade.interfaces.IArcadeGame;
import npe.arcade.interfaces.IArcadeMachine;
import npe.arcade.tileentities.TileEntityArcade;

import okushama.arcade.system.IProgram;
import okushama.arcade.system.OS;

import org.lwjgl.input.Keyboard;

public class RomDirectory implements IProgram {

	public BufferedImage gameIcon;
	public boolean imageDirty = false;
	public Map<String, String> roms = new HashMap<String, String>();
	private int current = 0;
	private OS os;
	
	public RomDirectory(OS o) {
		os = o;
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
	public void load() {
		getOS().registerKey(this, Keyboard.KEY_DOWN);
		getOS().registerKey(this, Keyboard.KEY_UP);
		getOS().registerKey(this, Keyboard.KEY_RETURN);
		getOS().registerKey(this, Keyboard.KEY_BACK);
	}


	@Override
	public String getTitle() {
		return "NES Emulator";
	}

	public BufferedImage getImage() {
		if (gameIcon == null || imageDirty) 
		{
			gameIcon = new BufferedImage(getOS().machine.getScreenSize()[0], getOS().machine.getScreenSize()[1], BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) gameIcon.getGraphics();
			g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getOS().machine.getScreenSize()[0], getOS().machine.getScreenSize()[1]);
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
			g.drawString("CHOOSE A ROM", 10, 20);
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
	public void initialize() {
		((TileEntityArcade)getOS().machine).setScreenResolution(256,224);
	}

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
		if(i == Keyboard.KEY_DOWN){
			if(current < this.roms.size()-1){
				current++;
			}else{
				current = 0;
			}
			imageDirty = true;
		}
		if(i == Keyboard.KEY_UP){
			if(current > 0){
				current--;
			}else{
				current = roms.size()-1;
			}
			imageDirty = true;

		}
		if(i == Keyboard.KEY_RETURN){
			String pathToRom = roms.values().toArray(new String[0])[current];
			String romName = roms.keySet().toArray(new String[0])[current];
			getOS().loadProgram(new EmulatorNES(getOS(), pathToRom, romName));
		}
		if(i == Keyboard.KEY_BACK){
			getOS().unloadProgram();
		}
	}

	@Override
	public OS getOS() {
		return os;
	}
}
