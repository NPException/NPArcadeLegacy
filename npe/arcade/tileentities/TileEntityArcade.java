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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import npe.arcade.entities.EntityArcadeStool;
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

	private int lastGameWidth, lastGameHeight, textureWidth, textureHeight;
	private int gameOffsetX, gameOffsetY;

	private static final Color BACKGROUND_COLOR = Color.BLACK;

	private int glTextureId = -1;

	private BufferedImage screen;
	private int[] screenData;
	public boolean isImageChanged = false;

	private EntityPlayer player;

	private IArcadeGame game;
	private final List<KEY> keysPressedDown = new ArrayList<KEY>(12);

	// Values that may need synchronization with the server
	private int damage = 0;

	/**
	 * Constructor
	 */
	public TileEntityArcade() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			Resource resource = Minecraft.getMinecraft().getResourceManager().getResource(frameResource);
			InputStream inputstream = resource.getInputStream();
			screenframeImage = ImageIO.read(inputstream);
		}
		catch (Exception ex) {}
	}

	public void hitByPlayer(EntityPlayer player) {
		// game will be null on Serverside
		if (worldObj.isRemote) {
			if (!player.isSneaking()) {
				game.initialize();
			}
			else {
				if (game != null) {
					game.unload();
				}
				game = new OS();
				game.setArcadeMachine(this);
				game.initialize();
			}
		}
		damage++;
	}

	public void activateByPlayer(EntityPlayer player) {
		if (worldObj.isRemote && player == Minecraft.getMinecraft().thePlayer) {
			if (player.ridingEntity instanceof EntityArcadeStool) {
				if (this.player == null) {
					this.player = player;
				}
				else if (this.player == player) {
					this.player = null;
				}
			}
		}
	}

	// TODO: init game chooser here.
	private void initMainMenu() {
		game = new CrapRacer();
		game.setArcadeMachine(this);
		game.initialize();
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

			// check the players name
			String playerName = null;
			if (player != null) {
				// check if the player is still sitting on an arcade stool
				if (player.ridingEntity instanceof EntityArcadeStool) {
					playerName = player.username;
				}
				else {
					player = null;
				}
			}
			game.setCurrentPlayerName(playerName);
			// updatePlayerFOV(playerName);
			// collect pressed input keys
			keysPressedDown.clear();
			if (player == Minecraft.getMinecraft().thePlayer) {
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
					keysPressedDown.add(KEY.RED);
				}
			}

			// let the game tick
			game.doGameTick(keysPressedDown);

			///////////////////
			// SCREEN UPDATE //
			///////////////////
			int gameResX = game.getGraphicsSize().x;
			int gameResY = game.getGraphicsSize().y;
			BufferedImage gameGraphics = new BufferedImage(gameResX, gameResY, BufferedImage.TYPE_INT_ARGB);
			gameGraphics.setRGB(0, 0, gameResX, gameResY, game.renderGraphics(), 0, gameResX);
			int width = gameGraphics.getWidth();
			int height = gameGraphics.getHeight();

			// check if the resolution has changed or the screen isn't ready yet and create a new texture if necessary
			if (screen == null || lastGameWidth != width && lastGameHeight != height) {
				generateNewScreenTexture(width, height);
			}

			Graphics2D g = (Graphics2D)screen.getGraphics();
			g.setBackground(BACKGROUND_COLOR);
			g.clearRect(0, 0, screen.getWidth(), screen.getHeight());
			g.drawImage(gameGraphics, gameOffsetX, gameOffsetY, null);

			if (screenframeImage != null) {
				g.drawImage(screenframeImage, 0, 0, textureWidth, textureHeight, null);
			}

			isImageChanged = true;
		}
	}

	private void generateNewScreenTexture(int neededWidth, int neededHeight) {
		// todo: generate texture with sizes of ^2
		if (neededWidth < neededHeight * 0.75f) {
			textureHeight = neededHeight;
			textureWidth = (int)(textureHeight * 0.75f);
		}
		else {
			textureWidth = neededWidth;
			textureHeight = (int)(textureWidth / 0.75f);
		}
		screen = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);

		lastGameHeight = neededHeight;
		lastGameWidth = neededWidth;

		gameOffsetX = (neededWidth < textureWidth) ? (textureWidth / 2 - neededWidth / 2) : 0;
		gameOffsetY = (neededHeight < textureHeight) ? (textureHeight / 2 - neededHeight / 2) : 0;

		TextureUtil.allocateTexture(getGlTextureId(true), screen.getWidth(), screen.getHeight());
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

	public int getGlTextureId(boolean forceNew)
	{
		if (glTextureId == -1 || forceNew) {
			if (glTextureId != -1) {
				GL11.glDeleteTextures(glTextureId);
			}
			glTextureId = TextureUtil.glGenTextures();
		}
		return glTextureId;
	}

	public int getScreenTextureWidth() {
		return screen.getWidth();
	}

	public int getScreenTextureHeight() {
		return screen.getHeight();
	}

	public int[] getScreenImageData() {
		if (screenData == null || screenData.length != screen.getWidth() * screen.getHeight()) {
			screenData = new int[screen.getWidth() * screen.getHeight()];
		}
		screen.getRGB(0, 0, screen.getWidth(), screen.getHeight(), screenData, 0, screen.getWidth());
		return screenData;
	}

	@SideOnly(Side.CLIENT)
	public boolean hasActivePlayer() {
		return player != null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getMaxRenderDistanceSquared() {
		// TODO: make configurable
		return 9216.0d;
	}

	@Override
	public void writeToNBT(NBTTagCompound par1nbtTagCompound) {}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		onChunkUnload();
	}

	@Override
	public void onChunkUnload() {
		disposeTexture();
		disposeGame();
	}

	private void disposeTexture() {
		if (glTextureId != -1) {
			GL11.glDeleteTextures(glTextureId);
			glTextureId = -1;
		}
	}

	private void disposeGame() {
		if (worldObj.isRemote && game != null) {
			game.unload();
			game = null;
		}
	}

	@ForgeSubscribe
	public void forgeWorldEventUnload(WorldEvent.Unload unloadEvent) {
		if (worldObj.isRemote && unloadEvent.world == worldObj) {
			onChunkUnload();
			MinecraftForge.EVENT_BUS.unregister(this);
		}
	}
}
