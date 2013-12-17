package npe.arcade.games.crapracer;

import static java.awt.RenderingHints.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ErrorGameState extends AbstractGameState {

    private BufferedImage errorScreen;

    public ErrorGameState(CrapRacer game) {
        super(game);
    }

    @Override
    public void initialize() {
        if (errorScreen == null) {
            int width = game.getGameGraphics().getWidth();
            int height = game.getGameGraphics().getHeight();
            errorScreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = (Graphics2D)errorScreen.getGraphics();
            g.setColor(Color.RED);
            g.fillRect(0, 0, width, height);

            g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
            g.drawString("ERROR", 12, 25);

            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            g.drawString("Something", 12, 45);
            g.drawString("went wrong", 12, 60);
            g.drawString("with", 12, 75);
            g.drawString("Crap Racer", 12, 90);
        }
    }

    @Override
    public void doTick() {

    }

    @Override
    public void doRender(Graphics2D g) {
        g.drawImage(errorScreen, 0, 0, null);
    }
}
