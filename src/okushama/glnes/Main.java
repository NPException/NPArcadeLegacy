package okushama.glnes;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.grapeshot.halfnes.GUIImpl;
import com.grapeshot.halfnes.NES;
import com.grapeshot.halfnes.halfNES;

public class Main{
	
	public static void main(String[] args){
		System.out.println("Launching halfNes gl");
		try {
			halfNES.main(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int width = 800, height = 600;
	public String TITLE = "Game ;D";
	private boolean running = true;
	public int FRAME_RATE = 60;
	private FPSCounter fpsCounter;
    private String lastFPS;
    public long ticks = 0L;
    public List<IKeyListener> keyListeners = new ArrayList<IKeyListener>();      
    public BufferedImage nesOutput = null; 
	public Thread mainThread;
	
	public NES nes = null;
	public GUIImpl nesgui = null;
	
	public InputControls player1 = new InputControls(1);
	public InputControls player2 = new InputControls(2);
	
    private static Main instance = null;
    public static Main instance(){
    	if(instance == null) instance = new Main();
    	return instance;
    }
	
	public Main() {
		try {
			instance = this;
			//keyListeners.add(player1);
			//keyListeners.add(player2);
			//keyListeners.add(new WindowControls());
			/*mainThread = new Thread(){
				@Override
				public void run(){
					try{
						initDisplay(false);
						initGL();
				        nes.setControllers(player1, player2);
					}catch(Exception e){
						e.printStackTrace();
					}
					instance.run();
					instance.cleanup();
				}
			};
			mainThread.start();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initDisplay(boolean fullscreen) throws Exception {	
		Display.setTitle(TITLE);
		Display.setFullscreen(fullscreen); 
        	DisplayMode displayMode = null;
        	DisplayMode d[] = Display.getAvailableDisplayModes();
        	for (int i = d.length - 1; i >= 0; i--) {
            		displayMode = d[i];
            	if (d[i].getWidth() == width
                	&& d[i].getHeight() == height
                	&& (d[i].getBitsPerPixel() >= 24 &&  d[i].getBitsPerPixel() <= 24 + 8 ) 
                	&& d[i].getFrequency() == FRAME_RATE) {
                	break;
            		}
        	}
        	Display.setDisplayMode(displayMode); 
        	if (fullscreen) Display.setVSyncEnabled(true);	
		Display.create();
		
	}
	
	public void initGL() {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
       	GL11.glClearColor(0f,0f,0f,0f);
        GL11.glDisable(GL11.GL_DITHER);
        GL11.glDepthFunc(GL11.GL_LESS);
        GL11.glEnable(GL11.GL_NORMALIZE);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); 
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST); 
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glViewport(0, 0, width, height);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(30, width/(float)height, 1f, 300f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW); 
        GL11.glDepthMask(true);
	}
	
	public void run() {
		while (true) {
			Display.sync(FRAME_RATE);
			logic();
			render();
			Display.update();
		}
	}
	public void logic() {
		if (fpsCounter != null) {
			if (fpsCounter.update()) lastFPS = "FPS " + fpsCounter.getFPS();
			Display.setTitle(TITLE);
		}
		int x = Mouse.getX();
		int y = Mouse.getY();
		ticks++;
		//System.out.println(" pre mouse");

		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				int mouse = Mouse.getEventButton();
				switch (mouse) {
					case 0: {
						break;
					}
					case 1: {

						break;
					}
					case 2: {
						break;
					}
				}
			}
		}
		//System.out.println(" pre keyboard");

		while (Keyboard.next()) {
			int key =  Keyboard.getEventKey();
			if(Keyboard.getEventKeyState()){
				for(IKeyListener k : this.keyListeners){
					if(!k.isListening()) continue;
						k.onKeyDown(key);
				}
			}else{
				for(IKeyListener k : this.keyListeners){
					if(!k.isListening()) continue;
						k.onKeyUp(key);
				}
			}
		}
		
		
	}
	
	public void render() {
		if(nes.getCurrentRomName() != null){
			Display.setTitle("nes gl - "+nes.getCurrentRomName());
		}
		glClear(GL11.GL_COLOR_BUFFER_BIT |GL11.GL_DEPTH_BUFFER_BIT); 
		startRendering2d();
		glEnable(GL_TEXTURE);
		glColor4f(1f,1f,1f,1f);
		if(instance.nesOutput != null){
			try {
				RenderHelper.bindBufferedImage(System.currentTimeMillis()+"", nesOutput);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		RenderHelper.renderTexturedQuad(width, height, 1, -1);
		//render here
		glDisable(GL_TEXTURE);
		stopRendering2d();
	}
	
	public static void startRendering2d() {
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, instance.width, 0, instance.height, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
	}

	public static void stopRendering2d() {
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
	}
	public void cleanup() {
		Keyboard.destroy();
		Mouse.destroy();
		Display.destroy();
		System.gc();
		System.out.println("Destroyed all the things!");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
   
}
class FPSCounter {
	private float FPS = 0, fc = 0;

    public long updateFrequency,
                 currentTime,
                 elapsedTime,
                 lastTime;
    
    void init() {
        updateFrequency = Sys.getTimerResolution()>>1;
        currentTime     = 0;
        elapsedTime     = Sys.getTime();
        lastTime        = Sys.getTime();

    }
    boolean update() {
		currentTime = Sys.getTime();

	      fc++;
	      
	      if((elapsedTime = currentTime - lastTime) >= updateFrequency){
	        FPS         = (fc/elapsedTime)*updateFrequency*2;
	        fc         = 0;
	        lastTime    = currentTime;
	        elapsedTime = 0;
	        return true;
	      }
	      return false;
    }
    float getFPS() {
    	return FPS;
    }
}

