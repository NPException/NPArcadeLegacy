package okushama.arcade.client;

import static org.lwjgl.opengl.GL11.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import okushama.arcade.tileentities.TileEntityMonitor;

import org.lwjgl.opengl.GL11;




public class RenderMonitor extends TileEntitySpecialRenderer
{

	private final ModelMonitor modelMonitor = new ModelMonitor();

	private final ResourceLocation monitorTexture = new ResourceLocation("npearcade", "textures/models/monitor.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double posX, double posY, double posZ, float f)
	{
		if(tileentity == null)
		{
			return;
		}
		glPushMatrix();

		TileEntityMonitor monitor = (TileEntityMonitor)tileentity;//HERE


		GL11.glTranslated(posX, posY, posZ);

		glEnable(GL_LIGHTING);
		Tessellator.instance.setColorRGBA_F(1f, 1f, 1f, 1f);
		Minecraft.getMinecraft().renderEngine.bindTexture(monitorTexture);
		modelMonitor.render(null, 0.0F, 0.0F, 0F, 0.0F, 0.0F, 0.0625F);


		if (monitor.isImageChanged) {
			TextureUtil.uploadTextureImageAllocate(monitor.getGlTextureId(), monitor.getScreenImage(), false, false);
			monitor.isImageChanged = false;
		}
		glDisable(GL_LIGHTING);

		int metadata = monitor.getBlockMetadata();
		GL11.glRotatef(180 - 90 * metadata, 0, 1, 0);
		double zOffset = 0.001d;
		double xOffset = 0f;
		if(metadata == 1 || metadata == 2){
			zOffset = 1.001d;
		}
		if(metadata == 2 || metadata == 3){
			xOffset = 1f;
		}
		GL11.glTranslated(xOffset, 0d, zOffset);

		//System.out.println(monitor.getBlockMetadata());
		//GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

		Tessellator tessellator = Tessellator.instance;
		GL11.glColor3f(1f,1f,1f);
		GL11.glBindTexture(GL_TEXTURE_2D, monitor.getGlTextureId()); //HERE
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,  GL_NEAREST);
		/*	glTexParameteri( GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR );
		glTexParameteri( GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_LINEAR );  */
		RenderHelper.disableStandardItemLighting();
		tessellator.startDrawingQuads();
		float blockScaleX = 5f, blockScaleY = 5f;
		float minX = -1f, maxX = blockScaleX-1f, minY = 0, maxY = blockScaleY;
		tessellator.addVertexWithUV(minX, minY, 0, 0, 0);
		tessellator.addVertexWithUV(maxX, minY, 0, 1, 0);
		tessellator.addVertexWithUV(maxX, maxY, 0, 1, -1);
		tessellator.addVertexWithUV(minX, maxY, 0, 0, -1);
		tessellator.draw();
		RenderHelper.enableStandardItemLighting();

		glPopMatrix();
	}
}