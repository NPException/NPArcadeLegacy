package npe.arcade.games;

import static java.awt.RenderingHints.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import npe.arcade.interfaces.IArcadeGame;
import npe.arcade.interfaces.IArcadeMachine;

public abstract class AbstractArcadeGame implements IArcadeGame {

	private Map<KEY, Boolean> input;

	protected IArcadeMachine arcadeMachine;
	protected String playername;

	protected BufferedImage gameGraphics;
	protected Size gameSize = new Size(96, 128);
	private final Size logoSize = new Size(32, 16);
	protected BufferedImage gameIcon;

	protected Random rand;

	@Override
	public abstract String getTitle();

	@Override
	public int[] getGameIcon() {
		//        if (gameIcon == null) {
		// just a random default icon
		gameIcon = new BufferedImage(logoSize.x, logoSize.y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)gameIcon.getGraphics();
		g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
		g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g.setBackground(new Color(255, 255, 255, 0));
		g.clearRect(0, 0, 32, 16);
		g.setColor(Color.WHITE);
		g.setStroke(new BasicStroke(1.5f));
		g.drawRoundRect(1, 1, 30, 14, 3, 3);
		g.drawRoundRect(5, 5, 22, 6, 1, 1);
		g.setColor(Color.ORANGE);
		g.drawRoundRect(3, 3, 26, 10, 2, 2);
		//        }
		return gameIcon.getRGB(0, 0, logoSize.x, logoSize.y, null, 0, logoSize.x);
	}

	@Override
	public Size getGameIconSize() {
		return logoSize;
	}

	@Override
	public void setArcadeMachine(IArcadeMachine arcadeMachine) {
		this.arcadeMachine = arcadeMachine;
	}

	@Override
	public void setCurrentPlayerName(String playername) {
		this.playername = playername;
	}

	@Override
	public void initialize() {
		if (gameGraphics == null) {
			gameGraphics = new BufferedImage(gameSize.x, gameSize.y, BufferedImage.TYPE_INT_ARGB);
		}
		if (rand == null) {
			rand = new Random();
		}
		rand.setSeed("NPExceptional Seed".hashCode());
		if (input == null) {
			input = new HashMap<IArcadeGame.KEY, Boolean>();
		}
	}

	@Override
	public void unload() {}

	@Override
	public void doGameTick(List<KEY> input) {
		for (KEY key : KEY.values()) {
			this.input.put(key, Boolean.valueOf(input.contains(key)));
		}
		gameTick();

		// TODO: render some fancy overlay
	}

	/**
	 * The game tick is done here. The input is already done and available via isKeyDown(KEY);
	 */
	public abstract void gameTick();

	public boolean isKeyDown(KEY key) {
		return input.get(key).booleanValue();
	}

	@Override
	public int[] renderGraphics() {
		return gameGraphics.getRGB(0, 0, gameSize.x, gameSize.y, null, 0, gameSize.x);
	}

	@Override
	public Size getGraphicsSize() {
		return gameSize;
	}
}
