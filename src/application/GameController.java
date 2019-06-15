package application;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

import java.util.*;

public class GameController {

    public static final int ALIEN_COLUMNS = 11;
    public static final int ALIEN_ROWS = 5;
    public static final int ALIEN_SIZE = 4;

    public static final int WALL_WIDTH = 7;
    public static final int WALL_HEIGHT = 5;
    public static final int WALL_HEALTH = 10;
    public static final int WALL_ROW = 85;

    public static final int PLAYER_WIDTH = 6;
    public static final int PLAYER_ROW = 98;

    public static final int COLUMNS = 80;
    public static final int ROWS = 100;

    public static final double DELAY = 1500;
    private static final int ENEMY_SHOOTING = 800;
    private static final int MISSILE_SPEED = 13;

    private List<Alien> aliens = new ArrayList<>();
    private List<Wall> walls = new ArrayList<>();
    private Player player = new Player(PLAYER_ROW);
    private List<Missile> missiles = new ArrayList<>();

    private boolean movedDown = false;
    private boolean shooting = false;
    private boolean shootAllowed = true;

    private Timeline timeline = new Timeline();
    private KeyFrame updates;

    private IntegerProperty score = new SimpleIntegerProperty(0);
    private BooleanProperty active = new SimpleBooleanProperty(true);
    private Timeline updateTimeline = new Timeline();
    private List<Timeline> timelines = new ArrayList<>();

    private final String[] sounds = {"/effects/fastinvader1.wav", "/effects/fastinvader2.wav", "/effects/fastinvader3.wav", "/effects/fastinvader4.wav"};
    private ListIterator<String> invaderSounds;
    private AudioClip soundEffect;

    public GameController() {
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLUMNS; c++) {
                aliens.add(new Alien(r, c));
            }
            // nechtělo se mi to dávat zvlášť do dalšího cyklu
            if (r < 1) continue;
            walls.add(new Wall(r, WALL_HEALTH));
        }
        invaderSounds = Arrays.asList(sounds).listIterator();
        setupTimeline();
    }

    private void update() {
        aliens.forEach(Alien::update);
        if (invaderSounds.hasNext()) playSound(invaderSounds.next());
        else {
            invaderSounds = Arrays.asList(sounds).listIterator();
            playSound(invaderSounds.next());
        }
        //boolean kvůli gameOver
        checkAliens();
    }

    private void shootMissile() {
        if (shooting && shootAllowed) {
            playSound("/effects/shoot.wav");
            var m = new Missile(player.getRow(), player.getColumn());
            m.setrDir(-1);
            missiles.add(m);
            shooting = false;
            shootAllowed = false;
        }
        missiles.forEach(this::checkMissileHit);
        missiles.forEach(Missile::update);
    }

    private void checkMissileHit(Missile missile) {
        if (missile.isAlive()) {
            int c = missile.getColumn();
            int r = missile.getRow();

            if (r < -2 || r > 100) {
                if (r < -2) shootAllowed = true;
                missile.setAlive(false);
                return;
            }
            // PLAYER SHOOTING
            if (missile.getrDir() == -1) {
                Optional<Alien> col1 = aliens.stream().filter(a -> a.isAlive() &&
                        ((a.getColumn() * ALIEN_SIZE >= c - ALIEN_SIZE + 1) && (a.getColumn() * ALIEN_SIZE <= c + ALIEN_SIZE - 1))
                        && (a.getRow() * ALIEN_SIZE == Math.abs(r))).findFirst();

                if (col1.isPresent()) {
                    playSound("/effects/invaderkilled.wav");
                    missile.setAlive(false);
                    col1.get().setAlive(false);
                    aliens.remove(col1.get());
                    score.setValue(score.getValue() + 50);
                    shootAllowed = true;
                    if (aliens.isEmpty()) gameOver();
                }
                Optional<Wall> col2 = walls.stream().filter(w -> w.isAlive() &&
                        (((w.getColumn() * 2) * WALL_WIDTH < c + (WALL_WIDTH / 2) - 1) && ((w.getColumn() * 2) * WALL_WIDTH > c - WALL_WIDTH + 1))
                        && (ROWS - w.getRow() - WALL_HEIGHT == Math.abs(r))).findFirst();
                if (col2.isPresent()) {
                    score.setValue(score.getValue() + 100);
                    col2.get().takeDamage();
                    missile.setAlive(false);
                    shootAllowed = true;
                }
            }
            // ALIEN SHOOTING
            else {
                Optional<Wall> wall = walls.stream().filter(w -> w.isAlive() &&
                        (((w.getColumn() * 2) * WALL_WIDTH < c + (WALL_WIDTH / 2) - 1) && ((w.getColumn() * 2) * WALL_WIDTH > c - WALL_WIDTH + 1))
                        && (ROWS - w.getRow() - (WALL_HEIGHT * 3) == Math.abs(r))).findFirst();
                if (wall.isPresent()) {
                    wall.get().takeDamage();
                    missile.setAlive(false);
                }
                if (((player.getColumn() <= c) && (player.getColumn() + PLAYER_WIDTH > c)) &&
                        (player.getRow() == Math.abs(r))) {
                    playSound("/effects/explosion.wav");
                    gameOver();
                }
            }
        }
    }

    private void playSound(String soundName) {
        soundEffect = new AudioClip(getClass().getResource(soundName).toString());
        soundEffect.play();
    }

    private void setupTimeline() {
        Timeline shoot = new Timeline();
        KeyFrame shooting = new KeyFrame(Duration.millis(MISSILE_SPEED), e -> shootMissile());
        shoot.getKeyFrames().add(shooting);
        shoot.setCycleCount(Animation.INDEFINITE);
        shoot.play();

        Timeline enemyShoot = new Timeline();
        enemyShoot.getKeyFrames().add(new KeyFrame(Duration.millis(ENEMY_SHOOTING), e -> enemyShoot()));
        enemyShoot.setCycleCount(Animation.INDEFINITE);
        enemyShoot.play();

        updates = new KeyFrame(
                Duration.millis(DELAY),
                e -> update());
        updateTimeline.getKeyFrames().add(updates);
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        Collections.addAll(timelines, shoot, enemyShoot, updateTimeline, timeline);
    }

    private void enemyShoot() {
        Random r = new Random();

        int rEnemy = r.nextInt(aliens.size());
        Optional<Alien> alien = aliens.stream().skip(rEnemy).findFirst();
        if (alien.isPresent()) {
            Missile m = new Missile(alien.get().getRow() * ALIEN_SIZE, alien.get().getColumn() * ALIEN_SIZE);
            m.setrDir(1);
            missiles.add(m);
        }
    }

    private void speedUpGame() {
        updateTimeline.stop();

        updateTimeline.getKeyFrames().remove(updates);

        Duration boost = updates.getTime();
        if (boost.greaterThan(Duration.millis(100))) boost = boost.subtract(Duration.millis(100));
        else boost = boost.divide(1.2);

        updates = new KeyFrame(boost, e -> update());
        updateTimeline.getKeyFrames().add(updates);
        updateTimeline.play();
    }

    private void checkAliens() {
        boolean colision = aliens.stream().anyMatch(this::checkAlien);
        if (colision) {
            if (!movedDown) {
                aliens.forEach(Alien::moveDown);
                speedUpGame();
                movedDown = true;
            } else {
                aliens.forEach(Alien::stayOnRow);
                aliens.forEach(Alien::hBounce);
                movedDown = false;
            }
        } else aliens.forEach(Alien::stayOnRow);
    }

    private boolean checkAlien(Alien alien) {
        int r = alien.getRow();
        int c = alien.getColumn();
        if (r == 20) gameOver();
        return (c == 0) || (c == COLUMNS / ALIEN_SIZE - 1);
    }

    private void gameOver() {
        timelines.forEach(Timeline::stop);
        Main.restart.setVisible(true);
    }

    public List<Alien> getAliens() {
        return aliens;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Missile> getMissiles() {
        return missiles;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public Player getPlayer() {
        return player;
    }

    public int getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public boolean isActive() {
        return active.get();
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public void moveLeft() {
        if (!isActive()) return;
        int pos = player.getColumn();
        if (pos > 0) player.setColumn(pos - 2);
    }

    public void moveRight() {
        if (!isActive()) return;
        int pos = player.getColumn();
        if (pos + PLAYER_WIDTH < COLUMNS) player.setColumn(pos + 2);
    }

    public void shoot() {
        shooting = true;
    }
}
