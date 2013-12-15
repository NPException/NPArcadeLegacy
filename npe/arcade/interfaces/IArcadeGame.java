package npe.arcade.interfaces;

public interface IArcadeGame {

    /**
     * The name of the game
     * 
     * @return
     */
    public String getTitle();

    /**
     * Sets a reference to the IArcadeMachine instance on which this game is running.
     * 
     * @param arcadeMachine
     */
    public void setArcadeMachine(IArcadeMachine arcadeMachine);

    /**
     * Sets the name of the current player who's using the arcade.<br>
     * playername will be set to null if the player left the arcade.<br>
     * You could react to this case by reseting, saving, or pausing the game.
     * 
     * @param playername
     */
    public void setCurrentPlayerName(String playername);

    /**
     * Called when this game is first loaded.<br>
     * Use this to load any necessary game data (assets, sounds, etc.)
     */
    public void initialize();

    /**
     * Called when this game is going to be unloaded.<br>
     * You can use this method to save the highscores or whatever.
     */
    public void shutdown();

    /**
     * Makes the game tick once.
     */
    public void doGameTick();

    /**
     * Gets the current visible stuff of the game.
     * the int array must be of the size int[96*128], otherwise the results are undetermined.
     * 
     * Make sure to render the game just in this method, as it might get called independently from doGameTick();
     * 
     * @return
     */
    public int[] renderGraphics();
}
