package npe.arcade.tileentities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.Resource;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import npe.arcade.entities.EntityArcadeSeat;
import npe.arcade.games.crapracer.CrapRacer;
import npe.arcade.interfaces.IArcadeGame;
import npe.arcade.interfaces.IArcadeGame.KEY;
import npe.arcade.interfaces.IArcadeMachine;
import okushama.arcade.system.OS;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArcade extends TileEntity implements IArcadeMachine {

	private static final ResourceLocation frameResource = new ResourceLocation("npearcade", "textures/models/arcadeScreenFrame.png");
	private static BufferedImage screenframeImage;

	private final int[] SUGGESTED_SCREEN_SIZE = { 96, 128 };

	private int screenWidth, screenHeight;
	private int gameOffsetX, gameOffsetY;

	private static final Color BACKGROUND_COLOR = Color.BLACK;

	private int glTextureId = -1;

	private BufferedImage screen;
	public boolean isImageChanged = true;

	private IArcadeGame game;
	private final List<KEY> keysPressedDown = new ArrayList<KEY>(12);

	// this is synced by the seat itself.
	private EntityArcadeSeat occupiedBySeat;

	// Values that may need synchronization with the server
	private int damage = 0;

	/**
	 * Constructor
	 */
	public TileEntityArcade() {
		screen = new BufferedImage(SUGGESTED_SCREEN_SIZE[0], SUGGESTED_SCREEN_SIZE[1], BufferedImage.TYPE_INT_ARGB);
		screenWidth = screen.getWidth();
		screenHeight = screen.getHeight();
		try {
			Resource resource = Minecraft.getMinecraft().getResourceManager().getResource(frameResource);
			InputStream inputstream = resource.getInputStream();
			screenframeImage = ImageIO.read(inputstream);
		}
		catch (Exception ex) {}
	}

	/**
	 * Try not to use this. This will get removed in the future.
	 * 
	 * @param newGame
	 * @return
	 */
	@Deprecated
	public TileEntityArcade setGame(IArcadeGame newGame) {
		game = newGame;
		game.setArcadeMachine(this);
		game.initialize();
		return this;
	}

	public void hitByPlayer(EntityPlayer player) {
		// game will be null on Serverside
		if (worldObj.isRemote) {
			if (occupiedBySeat != null && occupiedBySeat.riddenByEntity == player) {
				game.initialize();
			}
			if (player.isSneaking()) {
				setGame(new OS());
			}
		}
		damage++;
	}

	public void setOccupiedBySeat(EntityArcadeSeat seat) {
		occupiedBySeat = seat;
	}

	// TODO: init game chooser here.
	private void initMainMenu() {
		game = new CrapRacer();
		game.setArcadeMachine(this);
		game.initialize();
	}

	public float originalFov = -999;
	public float zoomFov = -0.5f;

	public void updatePlayerFOV(String playerName) {
		float currentFov = Minecraft.getMinecraft().gameSettings.fovSetting;
		if (currentFov >= 0 && originalFov == -999) {
			originalFov = currentFov;
		}
		if (Minecraft.getMinecraft().thePlayer.username.equals(playerName)) {
			if (currentFov > zoomFov) {
				currentFov -= 0.05;
				if (GuiIngameForge.renderCrosshairs) {
					GuiIngameForge.renderCrosshairs = false;
				}
			}
		}
		else {
			if (currentFov < originalFov) {
				currentFov += 0.05;
				if (!GuiIngameForge.renderCrosshairs) {
					GuiIngameForge.renderCrosshairs = true;
				}
			}
		}
		Minecraft.getMinecraft().gameSettings.fovSetting = currentFov;
	}

	/**
	 * Where the magic happens...
	 */
	@Override
	public void updateEntity() {
		if (getWorldObj().isRemote) {
			// init game if it is not there
			if (game == null) {
				initMainMenu();
			}
			Entity user = (occupiedBySeat == null) ? null : occupiedBySeat.riddenByEntity;

			// check the players name
			String playerName = null;
			if (user instanceof EntityPlayer) {
				playerName = ((EntityPlayer)user).username;
			}
			game.setCurrentPlayerName(playerName);
			// updatePlayerFOV(playerName);
			// collect pressed input keys
			keysPressedDown.clear();
			if (user == Minecraft.getMinecraft().thePlayer) {
				GameSettings settings = Minecraft.getMinecraft().gameSettings;
				if (GameSettings.isKeyDown(settings.keyBindRight)) {
					keysPressedDown.add(KEY.RIGHT);
				}
				if (GameSettings.isKeyDown(settings.keyBindLeft)) {
					keysPressedDown.add(KEY.LEFT);
				}
				if (GameSettings.isKeyDown(settings.keyBindBack)) {
					keysPressedDown.add(KEY.DOWN);
				}
				if (GameSettings.isKeyDown(settings.keyBindForward)) {
					keysPressedDown.add(KEY.UP);
				}
				if (GameSettings.isKeyDown(settings.keyBindJump)) {
					keysPressedDown.add(KEY.A);
				}
			}

			// let the game tick
			game.doGameTick(keysPressedDown);

			///////////////////
			// SCREEN UPDATE //
			///////////////////
			BufferedImage gameGraphics = game.renderGraphics();
			int width = gameGraphics.getWidth();
			int height = gameGraphics.getHeight();

			// check if the resolution has changed or the screen isn't ready yet and create a new texture if necessary
			if (screenWidth != width && screenHeight != height) {
				generateNewScreenTexture(width, height);
			}

			Graphics2D g = (Graphics2D)screen.getGraphics();
			g.setBackground(BACKGROUND_COLOR);
			g.clearRect(0, 0, screenWidth, screenHeight);
			g.drawImage(gameGraphics, gameOffsetX, gameOffsetY, null);

			if (screenframeImage != null) {
				g.drawImage(screenframeImage, 0, 0, screenWidth, screenHeight, null);
			}

			isImageChanged = true;
		}
	}

	private void generateNewScreenTexture(int neededWidth, int neededHeight) {
		// todo: generate texture with sizes of ^2
		int textureSize;
		if (neededHeight > neededWidth) {
			textureSize = neededHeight;
			screenHeight = neededHeight;
			screenWidth = screenHeight;
		}
		else {
			textureSize = neededWidth;
			screenWidth = neededWidth;
			screenHeight = screenWidth;
		}
		screen = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);

		gameOffsetX = (neededWidth > neededHeight) ? 0 : screenWidth / 2 - neededWidth / 2;
		gameOffsetY = (neededHeight > neededWidth) ? 0 : screenHeight / 2 - neededHeight / 2;
	}

	@Override
	public void playSound(File soundFile) {
		// TODO: play given soundFile
	}

	@Override
	public void fail(boolean hcf) {
		// TODO: display some error screen
		if (hcf || damage > 50) {
			// halt and catch fire
		}
	}

	@Override
	public int[] getSuggestedScreenSize() {
		return SUGGESTED_SCREEN_SIZE;
	}

	@Override
	public Color getScreenBackgroundColor() {
		return BACKGROUND_COLOR;
	}

	/*
	 * Getter and convenience methods
	 */

	public int getGlTextureId()
	{
		if (glTextureId == -1) {
			glTextureId = TextureUtil.glGenTextures();
		}
		return glTextureId;
	}

	public BufferedImage getScreenImage() {
		return screen;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getMaxRenderDistanceSquared() {
		// TODO: make configurable
		return 9216.0d;
	}

	@Override
	public void onChunkUnload() {
		if (glTextureId != -1) {
			GL11.glDeleteTextures(glTextureId);
		}
		if (worldObj.isRemote) {
			game.unload();
			game = null;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}
}
