package npe.arcade.tileentities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.Resource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import npe.arcade.entities.EntityArcadeSeat;
import npe.arcade.interfaces.IArcadeMachine;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArcade extends TileEntity implements IArcadeMachine {

    private static BufferedImage frame;

    private int glTextureId = -1;

    private final BufferedImage screen;
    public boolean isImageChanged = true;

    // this is synced by the seat itself.
    private EntityArcadeSeat occupiedBySeat;

    // TODO: Values that may need synchronization with the server

    private int damage = 0;

    // TODO: remove me, I'm temporary
    public Color backgroundColor = Color.DARK_GRAY.darker().darker();

    /**
     * Constructor
     */
    public TileEntityArcade() {

        InputStream inputstream = null;
        if (frame == null) {
            try
            {
                Resource resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("npearcade:textures/models/arcadeScreenFrame.png"));
                inputstream = resource.getInputStream();
                frame = ImageIO.read(inputstream);
            }
            catch (IOException ex) {
                frame = new BufferedImage(96, 128, BufferedImage.TYPE_INT_ARGB);
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

        screen = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)screen.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setClip(0, 0, 96, 128);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, 96, 128);
        g.drawImage(frame, 0, 0, null);
    }

    public void hitByPlayer(EntityPlayer player) {
        damage++;
    }

    private int tickcounter = 0;

    @Override
    public void updateEntity() {
        if (getWorldObj().isRemote) {
            // game.doGameTick();
            // screen.setRGB(0, 0, 96, 128, game.renderGraphics(), 0, 96);

            if (tickcounter == 0 || tickcounter == 10) {
                Color color = Color.WHITE;
                if (tickcounter == 10) {
                    color = backgroundColor;
                }
                Graphics2D g = (Graphics2D)screen.getGraphics();
                g.setColor(color);
                g.fillRect(10, 10, 4, 6);
                g.drawImage(frame, 0, 0, null);
                isImageChanged = true;
            }

            // update tickcounter
            tickcounter++;
            if (tickcounter > 20) {
                tickcounter = 0;
            }
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

    public void setOccupiedBySeat(EntityArcadeSeat seat) {
        occupiedBySeat = seat;
        // if (seat == null) game.setCurrentPlayerName(null);
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
