package okushama.glnes;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderHelper {

public static HashMap<String, Integer> textures = new HashMap<String, Integer>();

public static void renderTexturedQuad(float width, float height, int u, int v){
	try{
		glPushMatrix();
		glBegin(GL_QUADS);
		glTexCoord2f(u, v);
		glVertex3d(width, height, 0);		
		glTexCoord2f(0, v);
		glVertex3d(0, height, 0);	
		glTexCoord2f(0, 0);
		glVertex3d(0, 0, 0);
		glTexCoord2f(u, 0);
		glVertex3d(width, 0, 0);
		glEnd();
		glPopMatrix();
	}catch(Exception e){
		
	}
}
	
	public static void bindBufferedImage(String id, BufferedImage image) throws Exception 
	{
		  int[] pixels = new int[image.getWidth() * image.getHeight()];
	        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
	        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);     
	        for(int y = 0; y < image.getHeight(); y++){
	            for(int x = 0; x < image.getWidth(); x++){
	                int pixel = pixels[y * image.getWidth() + x];
	                buffer.put((byte) ((pixel >> 16) & 0xFF));
	                buffer.put((byte) ((pixel >> 8) & 0xFF));
	                buffer.put((byte) (pixel & 0xFF));
	                buffer.put((byte) ((pixel >> 24) & 0xFF));
	            }
	        }

	        buffer.flip();
	      //  GL11.glTexImage2D(GL_TEXTURE_2D, 0, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, 0, buffer);	
	        int textureID = glGenTextures();
	        glBindTexture(GL_TEXTURE_2D, textureID);
	        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	       // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
	        glBindTexture(GL_TEXTURE_2D, textureID);
	}
		
	public static int bindBufferedImageMc(String id, BufferedImage image) throws Exception 
	{

		    //http://www.java-gaming.org/topics/bufferedimage-to-lwjgl-texture/25516/msg/220280/view.html#msg220280

		    int pixels[] = new int[image.getWidth() * image.getHeight()];
		    image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		    ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); // <-- 4 for RGBA, 3 for RGB

		    for(int y = 0; y < image.getHeight(); y++){
		        for(int x = 0; x < image.getWidth(); x++){
		            int pixel = pixels[y * image.getWidth() + x];
		            buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
		            buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
		            buffer.put((byte) (pixel & 0xFF));               // Blue component
		            buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
		        }
		    }

		    buffer.flip();

		    int textureID = glGenTextures(); //Generate texture ID
		    glBindTexture(GL_TEXTURE_2D, textureID);

		    // Setup wrap mode
		    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		    //Setup texture scaling filtering
		    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		    //Send texel data to OpenGL
		    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		    return 0;
		
	}
	
}
