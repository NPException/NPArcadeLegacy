package npe.arcade.interfaces;

import java.awt.Color;
import java.io.File;

public interface IArcadeMachine {

    /**
     * Plays the sound contained in the file
     * 
     * @param soundFile
     */
    public void playSound(File soundFile);

    /**
     * If a game produces an error it could call this method.
     * 
     * @param hcf
     *            If set to true, shit will go wrong...
     */
    public void fail(boolean hcf);

    /**
     * Returns the resolution of the arcade machines screen.<b>
     * int[0] = width<b>
     * int[1] = height<b>
     * 
     * @return
     */
    public int[] getScreenSize();

    /**
     * Gets the background color of the arcades screen, which will be used to fill areas of the screen, that are not
     * used by the game.
     * 
     * @return
     */
    public Color getScreenBackgroundColor();
}
