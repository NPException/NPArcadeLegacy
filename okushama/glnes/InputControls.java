package okushama.glnes;

import static com.grapeshot.halfnes.utils.BIT0;
import static com.grapeshot.halfnes.utils.BIT1;
import static com.grapeshot.halfnes.utils.BIT2;
import static com.grapeshot.halfnes.utils.BIT3;
import static com.grapeshot.halfnes.utils.BIT4;
import static com.grapeshot.halfnes.utils.BIT5;
import static com.grapeshot.halfnes.utils.BIT6;
import static com.grapeshot.halfnes.utils.BIT7;
import static com.grapeshot.halfnes.utils.getbit;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.prefs.Preferences;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import org.lwjgl.input.Keyboard;

import com.grapeshot.halfnes.ControllerInterface;
import com.grapeshot.halfnes.PrefsSingleton;

public class InputControls implements IKeyListener, ControllerInterface{

	public int controllerPort;
	
	 private Controller gameController;
	 private Component[] buttons;
	 private  ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
	 private int latchbyte = 0, controllerbyte = 0, prevbyte = 0, outbyte = 0, gamepadbyte = 0;
	 public HashMap<Integer, Integer> keys = new HashMap<Integer, Integer>(10);
	 private  int controllernum;

	public InputControls(int player){
		
		  if ((player-1 != 0) && (player-1 != 1)) {
	            throw new IllegalArgumentException("controllerNum must be 0 or 1");
	        }
	        this.controllernum = player-1;
	        setButtons();
	        this.startEventQueue();
	}
	
	public void output(final boolean state) {
	    latchbyte = gamepadbyte | controllerbyte;
	}
	
	@Override
	public void onKeyDown(int key) {
		//System.out.println("Things");
		//enable the byte of whatever is found
        prevbyte = controllerbyte;
        final int kepressed = key;
        if (!keys.containsKey(kepressed)) {
            return;
        }
        //enable the corresponding bit to the key
        controllerbyte |= keys.get(kepressed);
        //special case: if up and down are pressed at once, use whichever was pressed previously
        if (getbit(controllerbyte, 4) && getbit(controllerbyte, 5)) {
            controllerbyte &= ~(BIT4 | BIT5);
            controllerbyte |= (prevbyte & ~(BIT4 | BIT5));
        }
        //same for left and right
        if (getbit(controllerbyte, 6) && getbit(controllerbyte, 7)) {
            controllerbyte &= ~(BIT6 | BIT7);
            controllerbyte |= (prevbyte & ~(BIT6 | BIT7));
        }
	}

	@Override
	public void onKeyUp(int key) {
		prevbyte = controllerbyte;
        final int kepressed = key;
        if (!keys.containsKey(kepressed)) {
            return;
        }
        controllerbyte &= ~keys.get(kepressed);
	}

	@Override
	public String getLabel() {
		return "Controller "+controllerPort+" Key Listener";
	}

	@Override
	public boolean isListening() {
		return true;
	}
	
	 private static Component[] getButtons(Controller controller) {
	        List<Component> buttons = new ArrayList<Component>();
	        // Get this controllers components (buttons and axis)
	        Component[] components = controller.getComponents();
	        for (Component component : components) {
	            if (component.getIdentifier() instanceof Component.Identifier.Button) {
	                buttons.add(component);
	            }
	        }
	        return buttons.toArray(new Component[0]);
	    }

	    public final void setButtons() {
	        //reset the buttons from prefs
	        keys.clear();
	        switch (controllernum) {
	            case 0:
	            	 keys.put(Keyboard.KEY_UP, BIT4);
		                keys.put(Keyboard.KEY_DOWN, BIT5);
		                keys.put(Keyboard.KEY_LEFT, BIT6);
		                keys.put(Keyboard.KEY_RIGHT, BIT7);
		                keys.put(Keyboard.KEY_X, BIT0);
		                keys.put(Keyboard.KEY_Z, BIT1);
		                keys.put(Keyboard.KEY_RSHIFT, BIT2);
		                keys.put(Keyboard.KEY_RETURN, BIT3);
		                break;
		            case 1:
		            default:
		                keys.put(Keyboard.KEY_W, BIT4);
		                keys.put(Keyboard.KEY_S, BIT5);
		                keys.put(Keyboard.KEY_A, BIT6);
		                keys.put(Keyboard.KEY_D, BIT7);
		                keys.put(Keyboard.KEY_G, BIT0);
		                keys.put(Keyboard.KEY_F, BIT1);
		                keys.put(Keyboard.KEY_R, BIT2);
		                keys.put(Keyboard.KEY_T, BIT3);
	                break;

	        }
	        Controller[] controllers = getAvailablePadControllers();
	        if (controllers.length > controllernum) {
	            this.gameController = controllers[controllernum];
	            PrefsSingleton.get().put("controller" + controllernum, gameController.getName());
	            System.err.println(controllernum + 1 + ". " + gameController.getName());
	            this.buttons = getButtons(controllers[controllernum]);
	        } else {
	            PrefsSingleton.get().put("controller" + controllernum,"");
	            this.gameController = null;
	            this.buttons = null;
	        }
	    }
	    
	    @Override
	    public void strobe() {
	        //shifts a byte out
	        outbyte = latchbyte & 1;
	        latchbyte = ((latchbyte >> 1) | 0x100);
	    }
	    
	    @Override
	    public int getbyte() {
	        return outbyte;
	    }
	    
	    private static Controller[] getAvailablePadControllers() {
	        List<Controller> gameControllers = new ArrayList<Controller>();
	        // Get a list of the controllers JInput knows about and can interact
	        // with
	        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
	        // Check the useable controllers (gamepads or joysticks with at least 2
	        // axis and 2 buttons)
	        for (Controller controller : controllers) {
	            if ((controller.getType() == Controller.Type.GAMEPAD) || (controller.getType() == Controller.Type.STICK)) {
	                int nbOfAxis = 0;
	                // Get this controllers components (buttons and axis)
	                Component[] components = controller.getComponents();
	                // Check the availability of X/Y axis and at least 2 buttons
	                // (for A and B, because select and start can use the keyboard)
	                for (Component component : components) {
	                    if ((component.getIdentifier() == Component.Identifier.Axis.X)
	                            || (component.getIdentifier() == Component.Identifier.Axis.Y)) {
	                        nbOfAxis++;
	                    }
	                }
	                if ((nbOfAxis >= 2) && (getButtons(controller).length >= 2)) {
	                    // Valid game controller
	                    gameControllers.add(controller);
	                }
	            }
	        }
	        return gameControllers.toArray(new Controller[0]);
	    }
	    
	    public void startEventQueue() {
//	        if (System.getProperty("java.class.path").contains("jinput")) {
	        thread.execute(eventQueueLoop());
//	        }
	    }
	    double threshold = 0.25;

	    private Runnable eventQueueLoop() {
	        return new Runnable() {
	            @Override
	            public void run() {
	                if (gameController != null) {
	                    Event event = new Event();
	                    while (!Thread.interrupted()) {
	                        gameController.poll();
	                        EventQueue queue = gameController.getEventQueue();
	                        while (queue.getNextEvent(event)) {
	                            Component component = event.getComponent();
	                            if (component.getIdentifier() == Component.Identifier.Axis.X) {
	                                if (event.getValue() > threshold) {
	                                    gamepadbyte |= BIT7;//left on, right off
	                                    gamepadbyte &= ~BIT6;

	                                } else if (event.getValue() < -threshold) {
	                                    gamepadbyte |= BIT6;
	                                    gamepadbyte &= ~BIT7;
	                                } else {
	                                    gamepadbyte &= ~(BIT7 | BIT6);
	                                }
	                            } else if (component.getIdentifier() == Component.Identifier.Axis.Y) {
	                                if (event.getValue() > threshold) {
	                                    gamepadbyte |= BIT5;//up on, down off
	                                    gamepadbyte &= ~BIT4;
	                                } else if (event.getValue() < -threshold) {
	                                    gamepadbyte |= BIT4;//down on, up off
	                                    gamepadbyte &= ~BIT5;
	                                } else {
	                                    gamepadbyte &= ~(BIT4 | BIT5);
	                                }
	                            } else if (component == buttons[0]) {
	                                if (isPressed(event)) {
	                                    gamepadbyte |= BIT0;
	                                } else {
	                                    gamepadbyte &= ~BIT0;
	                                }
	                            } else if (component == buttons[1]) {
	                                if (isPressed(event)) {
	                                    gamepadbyte |= BIT1;
	                                } else {
	                                    gamepadbyte &= ~BIT1;
	                                }
	                            } else if (component == buttons[2]) {
	                                if (isPressed(event)) {
	                                    gamepadbyte |= BIT2;
	                                } else {
	                                    gamepadbyte &= ~BIT2;
	                                }
	                            } else if (component == buttons[3]) {
	                                if (isPressed(event)) {
	                                    gamepadbyte |= BIT3;
	                                } else {
	                                    gamepadbyte &= ~BIT3;
	                                }
	                            }
	                        }


	                        try {
	                            Thread.sleep(5);
	                        } catch (InterruptedException e) {
	                            // Preserve interrupt status
	                            Thread.currentThread().interrupt();
	                        }
	                    }
	                }
	            }
	        };
	    }
	    
	    private boolean isPressed(Event event) {
	        Component component = event.getComponent();
	        if (component.isAnalog()) {
	            if (Math.abs(event.getValue()) > 0.2f) {
	                return true;
	            } else {
	                return false;
	            }
	        } else if (event.getValue() == 0) {
	            return false;
	        } else {
	            return true;
	        }
	    }

}
