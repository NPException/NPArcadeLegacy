package okushama.arcade.system.programs.nes;

import static java.awt.RenderingHints.*;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import okushama.arcade.system.OS;
import okushama.arcade.system.programs.IProgram;

import org.lwjgl.input.Keyboard;

import com.grapeshot.halfnes.NES;
import com.grapeshot.halfnes.SwingAudioImpl;

public class ProgramNESEmulator implements IProgram {

	public BufferedImage gameIcon;
	public BufferedImage nesOutput = null;
	public InputControls player1 = new InputControls(1);
	public InputControls player2 = new InputControls(1);
	public String nesRom;
	public NES nes;
	public boolean nesStarted = false;
	public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
	public int loadDelay = 20;
	public String romTitle;
	public OS os;

	public ProgramNESEmulator(OS o, String romPath, String romName) {
		os = o;
		nesRom = romPath;
		romTitle = romName;
	}

	@Override
	public void load() {
		getOS().registerKey(this, Keyboard.KEY_DOWN);
		getOS().registerKey(this, Keyboard.KEY_LEFT);
		getOS().registerKey(this, Keyboard.KEY_RIGHT);
		getOS().registerKey(this, Keyboard.KEY_UP);
		getOS().registerKey(this, Keyboard.KEY_Z);
		getOS().registerKey(this, Keyboard.KEY_X);
		getOS().registerKey(this, Keyboard.KEY_RETURN);
		getOS().registerKey(this, Keyboard.KEY_RSHIFT);
		getOS().registerKey(this, Keyboard.KEY_BACK);
	}

	@Override
	public String getTitle() {
		return "NES Emulator: " + romTitle;
	}

	@Override
	public BufferedImage getImage() {
		if (nes != null && nes.runEmulation) {
			if (nesOutput != null) {
				return nesOutput;
			}
		}
		if (gameIcon == null || getOS().imageDirty)
		{
			gameIcon = new BufferedImage(getOS().resX, getOS().resY, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)gameIcon.getGraphics();
			g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(getOS().getBackground());
			g.fillRect(0, 0, getOS().resX, getOS().resY);
			g.setColor(getOS().getForeground());
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
			String[] output = {
					"NES EMULATOR",
					"Rom: " + romTitle,
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
			"Press  'BACK' to quit" };
			for (int i = 0; i < output.length; i++) {
				g.drawString(output[i], 10, 20 + (i * 16));
			}
		}
		return gameIcon;
	}

	@Override
	public void initialize() {
		nes = new NES(this);
		nes.setControllers(player1, player2);

	}

	public void loadRom() {
		nes.run(nesRom);
		nesStarted = true;
	}

	@Override
	public void unload() {
		if (nes.runEmulation) {
			nes.quit();
		}
		nesStarted = false;
	}

	@Override
	public void onTick() {
		if (Minecraft.getMinecraft().thePlayer.username.equals(getOS().currentPlayer)) {
			if (!nesStarted) {
				loadDelay--;
			}
			SwingAudioImpl.outputvol = Minecraft.getMinecraft().gameSettings.musicVolume;
		}
		else {
			if (nes.runEmulation) {
				int arcadeX = ((TileEntity)getOS().machine).xCoord;
				int arcadeY = ((TileEntity)getOS().machine).yCoord;
				int arcadeZ = ((TileEntity)getOS().machine).zCoord;
				float dist = (float)Minecraft.getMinecraft().thePlayer.getDistance(arcadeX, arcadeY, arcadeZ);
				float vol = Minecraft.getMinecraft().gameSettings.musicVolume-(dist/50);
				if(vol < 0) {
					vol = 0;
				}
				SwingAudioImpl.outputvol = vol;
			}
		}
	}

	@Override
	public OS getOS() {
		return os;
	}

	@Override
	public void onKeyUp(int i) {
		player1.onKeyUp(i);
	}

	@Override
	public void onKeyDown(int i) {
		if (!nesStarted) {
			if (i == Keyboard.KEY_RETURN && loadDelay < 1) {
				loadRom();
				loadDelay = 20;
			}
			if (i == Keyboard.KEY_BACK) {
				getOS().loadProgram(new ProgramNESDirectory(getOS()));
			}
		}
		else {
			if (i == Keyboard.KEY_BACK) {
				unload();
			}

			player1.onKeyDown(i);
		}
	}

}
