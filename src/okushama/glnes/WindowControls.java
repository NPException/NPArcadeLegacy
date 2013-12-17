package okushama.glnes;

import org.lwjgl.input.Keyboard;

public class WindowControls implements IKeyListener{

	@Override
	public void onKeyDown(int key) {
		
	}

	@Override
	public void onKeyUp(int key) {
		if(key == Keyboard.KEY_ESCAPE){
			System.exit(0);
		}
		if(key == Keyboard.KEY_1){
			Main.instance().nesgui.loadROM();
		}
	}

	@Override
	public String getLabel() {
		return "Window Controls Listener";
	}

	@Override
	public boolean isListening() {
		return true;
	}

}
