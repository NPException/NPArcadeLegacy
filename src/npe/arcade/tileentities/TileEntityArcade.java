package npe.arcade.tileentities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import npe.arcade.entities.EntityArcadeSeat;
import npe.arcade.games.crapracer.CrapRacer;
import npe.arcade.interfaces.IArcadeGame;
import npe.arcade.interfaces.IArcadeGame.KEY;
import npe.arcade.interfaces.IArcadeMachine;
import okushama.glnes.EmulatorNES;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArcade extends TileEntity implements IArcadeMachine {

    private int SCREEN_WIDTH = 196;
    private int SCREEN_HEIGHT = 259;
    private int[] SCREEN_SIZE = { SCREEN_WIDTH, SCREEN_HEIGHT };

    private static final Color BACKGROUND_COLOR = Color.BLACK.brighter();
    private static BufferedImage FRAME;

    private int glTextureId = -1;

    private final BufferedImage screen;
    public boolean isImageChanged = true;

    private IArcadeGame game;
    private final List<KEY> keysPressedDown = new ArrayList<KEY>(12);

    // this is synced by the seat itself.
    private EntityArcadeSeat occupiedBySeat;

    // TODO: Values that may need synchronization with the server

    private int damage = 0;

    /**
     * Constructor
     */
    public TileEntityArcade() {

        InputStream inputstream = null;
        if (FRAME == null) {
            try
            {
                Resource resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("npearcade:textures/models/arcadeScreenFrame.png"));
                inputstream = resource.getInputStream();
                FRAME = ImageIO.read(inputstream);
            }
            catch (IOException ex) {
                FRAME = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            }
            finally
            {
                if (inputstream != null)
                {
                    try {
                        inputstream.close();
                    }
                    catch (IOException e) {}
                }
            }
        }

        final int textureSize = Math.max(SCREEN_WIDTH, SCREEN_HEIGHT);
        screen = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);
       /* Graphics2D g = (Graphics2D)screen.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setBackground(BACKGROUND_COLOR);
        g.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.drawImage(FRAME, 0, 0, null);*/
    }

    public void hitByPlayer(EntityPlayer player) {
        // game will be null on Serverside
        if (worldObj.isRemote) {
            if (occupiedBySeat != null && occupiedBySeat.riddenByEntity == player) {
                game.initialize();
            }
        }
        damage++;
    }

    public void setOccupiedBySeat(EntityArcadeSeat seat) {
        occupiedBySeat = seat;
    }

    // TODO: init game chooser here.
    private void initBaseGame() {
        game = new CrapRacer();
     // game = new EmulatorNES("path/to/nes/rom");
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
                initBaseGame();
            }

            Entity user = (occupiedBySeat == null) ? null : occupiedBySeat.riddenByEntity;

            // check the players name
            String playerName = null;
            if (user instanceof EntityPlayer) {
                playerName = ((EntityPlayer)user).username;
            }
            game.setCurrentPlayerName(playerName);

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

            Graphics2D g = (Graphics2D)screen.getGraphics();
            g.setBackground(BACKGROUND_COLOR);
            g.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            BufferedImage gameGraphics = game.renderGraphics();
            int w = gameGraphics.getWidth();
            int h = gameGraphics.getHeight();
            SCREEN_WIDTH = w; SCREEN_HEIGHT = h;
            g.drawImage(gameGraphics, SCREEN_WIDTH / 2 - w / 2, SCREEN_HEIGHT / 2 - h / 2, null);

           // g.drawImage(FRAME, 0, 0, null);
            isImageChanged = true;
        }
    }

    @Override
    public void playSound(File soundFile) {
        // TODO: play given soundFile
    }

    @Override
    public void fail(boolean hcf) {
        // TODO: display some error screen
        if (hcf || damage > 50) {
            // TODO: catch fire
        }
    }

    @Override
    public int[] getScreenSize() {
        return SCREEN_SIZE;
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
