package npe.arcade.games.crapracer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import npe.arcade.interfaces.IArcadeGame.KEY;

public class RunGameState extends AbstractGameState {

    private BufferedImage graphics;
    private int width, height, laneOffset;
    int tickCounter;
    private float velocity;

    private List<Truck> trucks;
    private Car car;

    private boolean wasKeyDown;

    public RunGameState(CrapRacer game) {
        super(game);
    }

    @Override
    public void initialize() {
        if (graphics == null) {
            width = game.getGameGraphics().getWidth();
            height = game.getGameGraphics().getHeight();
            graphics = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        if (trucks == null) {
            trucks = new LinkedList<RunGameState.Truck>();
        }
        trucks.clear();
        if (car == null) {
            car = new Car();
        }
        car.lane = 1;
        tickCounter = 1;
        laneOffset = width / 4;
        velocity = 0.5f;
    }

    @Override
    public void doTick() {

        if (game.isKeyDown(KEY.LEFT)) {
            if (!wasKeyDown) {
                car.changeLaneLeft();
                wasKeyDown = true;
            }
        }
        else if (game.isKeyDown(KEY.RIGHT)) {
            if (!wasKeyDown) {
                car.changeLaneRight();
                wasKeyDown = true;
            }
        }
        else {
            wasKeyDown = false;
        }

        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            velocity *= 1.01f;
            if (game.getRandom().nextFloat() < 0.30f) {
                trucks.add(new Truck(game.getRandom()));
            }
        }

        for (Iterator<Truck> iter = trucks.iterator(); iter.hasNext();) {
            Truck truck = iter.next();
            if (truck.collidesWith(car)) {
                game.setGameState(ErrorGameState.class);
            }
            truck.move(velocity);
            if (truck.canBeRemoved()) {
                iter.remove();
            }
        }
    }

    @Override
    public void doRender(Graphics2D gamescreen) {
        Graphics2D g = (Graphics2D)graphics.getGraphics();
        g.setColor(game.getBackgroundColor());
        g.fillRect(0, 0, width, height);

        g.drawImage(game.racecarImage, laneOffset + laneOffset * car.lane - game.racecarImage.getWidth() / 2, (int)car.pos, null);

        for (Truck truck : trucks) {
            g.drawImage(game.truckImage, laneOffset + laneOffset * truck.lane - game.truckImage.getWidth() / 2, (int)truck.pos - game.truckImage.getHeight(), null);
        }

        gamescreen.drawImage(graphics, 0, 0, null);
    }

    // ENTITIES //

    class Car {
        int lane;
        float pos;

        Car() {
            pos = 95;
            lane = 1;
        }

        void changeLaneLeft() {
            if (lane > 0) {
                lane--;
            }
        }

        void changeLaneRight() {
            if (lane < 2) {
                lane++;
            }
        }
    }

    class Truck extends Car {

        Truck(Random rand) {
            pos = 0f;
            lane = rand.nextInt(3);
        }

        void move(float velocity) {
            pos += velocity;
        }

        boolean collidesWith(Car car) {
            if (lane == car.lane) {
                if (pos >= car.pos && (car.pos + game.racecarImage.getHeight()) < pos) {
                    return true;
                }
            }
            return false;
        }

        boolean canBeRemoved() {
            return pos > height + game.truckImage.getHeight();
        }
    }
}
