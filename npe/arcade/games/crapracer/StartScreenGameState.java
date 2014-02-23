package npe.arcade.games.crapracer;

import static java.awt.RenderingHints.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import npe.arcade.interfaces.IArcadeGame.KEY;

public class StartScreenGameState extends AbstractGameState {

	private BufferedImage startscreen;
	private int width, height;
	int tickCounter;

	public StartScreenGameState(CrapRacer game) {
		super(game);
	}

	@Override
	public void initialize() {
		if (startscreen == null) {
			width = game.getGameGraphics().getWidth();
			height = game.getGameGraphics().getHeight();
			startscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		}
		tickCounter = 1;
	}

	private Graphics2D graphics() {
		return (Graphics2D)startscreen.getGraphics();
	}

	@Override
	public void doTick() {
		if (game.isKeyDown(KEY.RED)) {
			game.setGameState(RunGameState.class);
			return;
		}

		Graphics2D g = graphics();
		g.setColor(game.getBackgroundColor());
		g.fillRect(0, 0, width, height);

		g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
		g.drawString("CRAP", 6, 25);
		g.drawString("RACER", 20, 55);

		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
		g.drawString("PRESS", 30, 80);

		if (tickCounter > 10) {
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
			g.drawString("A", 45, 100);
		}

		tickCounter++;
		if (tickCounter > 20) {
			tickCounter = 1;
		}
	}

	@Override
	public void doRender(Graphics2D g) {
		g.drawImage(startscreen, 0, 0, null);
	}

}
