package npe.arcade.interfaces;

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
}
