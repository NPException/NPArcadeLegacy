package npe.arcade.interfaces;

import java.util.List;

public interface IArcadeGame {

	/**
	 * Small class to be used as return value for methods which request a size
	 * 
	 * @author NPException
	 * 
	 */
	public class Size {
		public final int x, y;

		public Size(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * Theese are the available keys for the Arcade.
	 * 
	 */
	public enum KEY {
		UP, DOWN, LEFT, RIGHT, RED, GREEN, BLUE, YELLOW;
	}

	/**
	 * The name of the game. Will be displayed in the game choose menu. Should not be too long so it fits withing the
	 * screen bounds.<br>
	 * TODO: The name will be line wrapped if it is to long to fit in one line on the screen.
	 * 
	 * @return The gameTitle. Must NOT be null.
	 */
	public String getTitle();

	/**
	 * Returns the game logo of some sorts. If not null, this logo will be shown in the game choose menu instead of just
	 * the name.</br>
	 * logo must be of the size
	 * 
	 * @return The logo of the game. Can be null. May get resized on the arcade screen if it is to large to fit the game
	 *         choose menu.
	 */
	public int[] getGameIcon();

	/**
	 * The size of the games logo [width, height].<br>
	 * Must not be null.
	 * 
	 * @return
	 */
	public Size getGameIconSize();

	/**
	 * Sets a reference to the IArcadeMachine instance on which this game is running.<br>
	 * This method is called before the initialize method, so the {@code arcadeMachine} can be used there.
	 * 
	 * @param arcadeMachine
	 */
	public void setArcadeMachine(IArcadeMachine arcadeMachine);

	/**
	 * Called when the game is loaded.<br>
	 * Use this to load any necessary game data (assets, sounds, etc.)<br>
	 * <br>
	 * <i>Keep in mind that an instance of the game might be kept held in the arcade machine, so you should NOT use the
	 * constructor to initialize anything! The constructor maybe will only get called when the arcade initializes
	 * itself!</i>
	 */
	public void initialize();

	/**
	 * Called when this game is going to be unloaded from the arcade machine.<br>
	 * You can use this method to save the highscores or whatever.
	 */
	public void unload();

	/**
	 * Makes the game tick.
	 * 
	 * @param input
	 *            Contains all keys that are currently pressed.
	 */
	public void doGameTick(List<KEY> input);

	/**
	 * Gets the current visible stuff of the game.<br>
	 * If the image is smaller than the screens resolution it will be centered on it,
	 * 
	 * Make sure to render the game just in this method, as it might get called independently from doGameTick();
	 * 
	 * @return
	 */
	public int[] renderGraphics();

	/**
	 * Gets the size of the games rendered image.
	 * 
	 * @return an int array [width, height]
	 */
	public Size getGraphicsSize();

	/**
	 * Sets the name of the current player who's using the arcade.<br>
	 * playername will be set to <b>null</b> if the player left the arcade.<br>
	 * You could react to this case by reseting, saving, or pausing the game.
	 * 
	 * @param playername
	 */
	public void setCurrentPlayerName(String playername);
}
