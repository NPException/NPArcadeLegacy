package npe.arcade.games.crapracer;

import java.awt.Graphics2D;

public abstract class AbstractGameState {
    protected CrapRacer game;

    public AbstractGameState(CrapRacer game) {
        this.game = game;
    }

    public abstract void initialize();

    public abstract void doTick();

    public abstract void doRender(Graphics2D g);
}
