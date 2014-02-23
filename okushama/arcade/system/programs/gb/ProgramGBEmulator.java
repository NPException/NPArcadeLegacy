package okushama.arcade.system.programs.gb;

import static java.awt.RenderingHints.*;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import npe.arcade.tileentities.TileEntityArcade;
import okushama.arcade.system.OS;
import okushama.arcade.system.programs.IProgram;

import org.lwjgl.input.Keyboard;

import com.javaboy.Cartridge;
import com.javaboy.Dmgcpu;
import com.javaboy.GameBoyScreen;
import com.javaboy.JavaBoy;

public class ProgramGBEmulator implements IProgram {

	public BufferedImage gameIcon;
	public BufferedImage gameboyOutput = null;
	public String gameboyRom;
	public JavaBoy gameboy;
	public GameBoyScreen gbScreen;
	public boolean gameboyStarted = false;
	public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
	public int loadDelay = 20;
	public String romTitle;
	public OS os;
	public int[] keyCodes = { Keyboard.KEY_UP, Keyboard.KEY_DOWN, Keyboard.KEY_LEFT, Keyboard.KEY_RIGHT, Keyboard.KEY_Z, Keyboard.KEY_X, Keyboard.KEY_RETURN, Keyboard.KEY_RSHIFT };

	public ProgramGBEmulator(OS o, String romPath, String romName) {
		os = o;
		gameboyRom = romPath;
		romTitle = romName;
	}

	@Override
	public void load() {
		getOS().registerKey(this, Keyboard.KEY_UP);
		getOS().registerKey(this, Keyboard.KEY_DOWN);
		getOS().registerKey(this, Keyboard.KEY_LEFT);
		getOS().registerKey(this, Keyboard.KEY_RIGHT);
		getOS().registerKey(this, Keyboard.KEY_Z);
		getOS().registerKey(this, Keyboard.KEY_X);
		getOS().registerKey(this, Keyboard.KEY_RETURN);
		getOS().registerKey(this, Keyboard.KEY_RSHIFT);

		getOS().registerKey(this, Keyboard.KEY_BACK);
		getOS().registerKey(this, Keyboard.KEY_W);
	}

	@Override
	public String getTitle() {
		return "Gameboy Emulator: " + romTitle;
	}

	@Override
	public BufferedImage getImage() {
		if (gameboy != null && gameboyStarted) {
			if (gameboyOutput == null)
			{
				gameboyOutput = new BufferedImage(160, 144, BufferedImage.TYPE_INT_ARGB);
			}
			//if (gameboyOutput != null)
			{
				return gameboyOutput;
			}
		}
		if (gameIcon == null || getOS().imageDirty)
		{
			gameIcon = new BufferedImage(getOS().res.x, getOS().res.y, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)gameIcon.getGraphics();
			g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
			g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			g.setColor(getOS().getBackground());
			g.fillRect(0, 0, getOS().res.x, getOS().res.y);
			g.setColor(getOS().getForeground());
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
			String[] output = {
					"GAMEBOY EMULATOR",
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

	}

	public void loadRom() {
		gameboy = new JavaBoy(this, gameboyRom);
		gameboy.cartridge = new Cartridge(gameboyRom, gbScreen);
		gameboy.dmgcpu = new Dmgcpu(gameboy.cartridge, gameboy.gameLink, gbScreen);
		//	gameboy.gameBoyPrinter = new GameBoyPrinter();
		if (gameboy.gameLink != null) {
			gameboy.gameLink.setDmgcpu(gameboy.dmgcpu);
		}
		gbScreen.setGraphicsChip(gameboy.dmgcpu.graphicsChip);
		gbScreen.setSoundFreq();
		gbScreen.setBufferLength();
		gbScreen.setMagnify();
		gbScreen.setFrameSkip();
		gbScreen.setChannelEnable();
		gameboy.dmgcpu.allowGbcFeatures = gbScreen.fileGameboyColor.getState();
		gameboy.dmgcpu.reset();
		gameboy.queueDebuggerCommand("g");
		gameboy.dmgcpu.terminate = true;
		gameboyStarted = true;
		gbScreen.viewSingle.setState(true);
		gbScreen.viewDouble.setState(false);
		gbScreen.viewTriple.setState(false);
		gbScreen.viewQuadrouple.setState(false);
		gbScreen.setMagnify();
		gbScreen.setWindowSize(1);
		//gbScreen.setVisible(false);
	}

	@Override
	public void unload() {

		gbScreen.setVisible(false);
		gameboy = null;
		gbScreen = null;
		gameboyStarted = false;
	}

	@Override
	public void onTick() {
		loadDelay--;
		if (Minecraft.getMinecraft().thePlayer.username.equals(getOS().currentPlayer)) {}
		else {
			//	if (nes.runEmulation)
			{
				int arcadeX = ((TileEntityArcade)getOS().machine).xCoord;
				int arcadeY = ((TileEntityArcade)getOS().machine).yCoord;
				int arcadeZ = ((TileEntityArcade)getOS().machine).zCoord;
				float dist = (float)Minecraft.getMinecraft().thePlayer.getDistance(arcadeX, arcadeY, arcadeZ);
				float vol = Minecraft.getMinecraft().gameSettings.musicVolume - (dist / 15);
				if (vol < 0) {
					vol = 0;
				}
				//SwingAudioImpl.outputvol = vol;
			}
		}
	}

	@Override
	public OS getOS() {
		return os;
	}

	@Override
	public void onKeyUp(int key) {
		if (gameboyStarted) {
			if (key == keyCodes[0]) {
				gameboy.dmgcpu.ioHandler.padUp = false;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
			}
			else if (key == keyCodes[1]) {
				gameboy.dmgcpu.ioHandler.padDown = false;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
			}
			else if (key == keyCodes[2]) {
				gameboy.dmgcpu.ioHandler.padLeft = false;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
			}
			else if (key == keyCodes[3]) {
				gameboy.dmgcpu.ioHandler.padRight = false;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
			}
			else if (key == keyCodes[4]) {
				gameboy.dmgcpu.ioHandler.padA = false;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
			}
			else if (key == keyCodes[5]) {
				gameboy.dmgcpu.ioHandler.padB = false;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
			}
			else if (key == keyCodes[6]) {
				gameboy.dmgcpu.ioHandler.padStart = false;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
			}
			else if (key == keyCodes[7]) {
				gameboy.dmgcpu.ioHandler.padSelect = false;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
			}
		}
	}

	@Override
	public void onKeyDown(int key) {
		if (!gameboyStarted) {
			if (key == Keyboard.KEY_RETURN && loadDelay < 1) {
				loadRom();
				loadDelay = 20;
			}
			if (key == Keyboard.KEY_BACK) {
				getOS().loadProgram(new ProgramGBDirectory(getOS()));
			}
		}
		else {
			if (key == Keyboard.KEY_W) {
				System.out.println("W");
				gbScreen.setVisible(!gbScreen.isVisible());
				System.out.println(gbScreen.isVisible());
			}
			if (key == Keyboard.KEY_BACK) {
				unload();
			}

			//player1.onKeyDown(i);

			if (key == keyCodes[0]) {
				//   if (!dmgcpu.ioHandler.padUp) {
				gameboy.dmgcpu.ioHandler.padUp = true;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
				//   }
			}
			else if (key == keyCodes[1]) {
				//   if (!dmgcpu.ioHandler.padDown) {
				gameboy.dmgcpu.ioHandler.padDown = true;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
				//   }
			}
			else if (key == keyCodes[2]) {
				//   if (!dmgcpu.ioHandler.padLeft) {
				gameboy.dmgcpu.ioHandler.padLeft = true;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
				//   }
			}
			else if (key == keyCodes[3]) {
				//   if (!dmgcpu.ioHandler.padRight) {
				gameboy.dmgcpu.ioHandler.padRight = true;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
				//   }
			}
			else if (key == keyCodes[4]) {
				//   if (!dmgcpu.ioHandler.padA) {
				gameboy.dmgcpu.ioHandler.padA = true;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
				//   }
			}
			else if (key == keyCodes[5]) {
				//   if (!dmgcpu.ioHandler.padB) {
				gameboy.dmgcpu.ioHandler.padB = true;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
				//   }
			}
			else if (key == keyCodes[6]) {
				//   if (!dmgcpu.ioHandler.padStart) {
				gameboy.dmgcpu.ioHandler.padStart = true;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
				//   }
			}
			else if (key == keyCodes[7]) {
				//   if (!dmgcpu.ioHandler.padSelect) {
				gameboy.dmgcpu.ioHandler.padSelect = true;
				gameboy.dmgcpu.triggerInterruptIfEnabled(gameboy.dmgcpu.INT_P10);
				//   }
			}

			switch (key) {
				case KeyEvent.VK_F1:
					if (gameboy.dmgcpu.graphicsChip.frameSkip != 1) {
						gameboy.dmgcpu.graphicsChip.frameSkip--;
					}
					break;
				case KeyEvent.VK_F2:
					if (gameboy.dmgcpu.graphicsChip.frameSkip != 10) {
						gameboy.dmgcpu.graphicsChip.frameSkip++;
					}
					break;
			}
		}
	}

}
