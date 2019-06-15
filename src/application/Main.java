package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {

    public static final int UNIT_SIZE = 7;

    public static Button restart = new Button("Restart");

    private Pane gamePane;

    private GameController game = new GameController();
    private Timeline timeline;

    private Text lblScore;
    private HBox scorePane;

    private final Image alienImg = new Image(getClass().getResource("/application/alien.gif").toString());



    @Override
    public void start(Stage primaryStage) throws Exception {

        setupGamePane();
        setupScorePane();

        restart.setPrefSize(100,40);
        restart.setStyle("-fx-background-color: green");
        restart.setOnAction(e -> restartGame(primaryStage));
        restart.setVisible(false);

        timeline = game.getTimeline();
        timeline.stop();
        timeline.getKeyFrames().add(new KeyFrame(
                Duration.millis(50),
                e -> addMissile()));
        timeline.play();

        StackPane gameStack = new StackPane();
        gameStack.getChildren().addAll(gamePane,restart);

        BorderPane root = new BorderPane();
        root.setTop(scorePane);
        root.setCenter(gameStack);

        Scene scene = new Scene(root, 560, 715);
        scene.setOnKeyPressed(this::dispatchKeyEvents);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Space Invaders");
        primaryStage.show();
    }

    private void restartGame(Stage primaryStage) {
        game = new GameController();
        gamePane.setOpacity(0.3);
        try {
            start(primaryStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setupScorePane() {
        lblScore = new Text();
        lblScore.textProperty().bind(game.scoreProperty().asString());
        scorePane = new HBox(10, new Text("Score: "), lblScore);
    }

    private void addMissile() {
        if (!game.getMissiles().isEmpty()) {
            for (Missile m : game.getMissiles()) {
                if (!m.isDrawed()) {
                    gamePane.getChildren().add(createMissile(m));
                    m.setDrawed(true);
                }
            }
        }
    }

    private void setupGamePane() {
        gamePane = new Pane();
        gamePane.setMaxHeight(UNIT_SIZE * GameController.ROWS);
        gamePane.setMaxWidth(UNIT_SIZE * GameController.COLUMNS);

        for (Alien a : game.getAliens()) {
            gamePane.getChildren().add(createAlien(a));
        }

        for (Wall w : game.getWalls()) {
            gamePane.getChildren().add(createWall(w));
        }

        gamePane.getChildren().addAll(createPlayer());
        gamePane.setStyle("-fx-background-color: black");
    }

    private Node createAlien(Alien a) {
        Rectangle rect = new Rectangle(UNIT_SIZE * (GameController.ALIEN_SIZE - 1), UNIT_SIZE * (GameController.ALIEN_SIZE - 1));
        //rect.setFill(Color.WHITE);
        rect.setFill(new ImagePattern(alienImg));

        rect.xProperty().bind(a.columnProperty().multiply(UNIT_SIZE * GameController.ALIEN_SIZE));
        rect.yProperty().bind(a.rowProperty().multiply(UNIT_SIZE * GameController.ALIEN_SIZE));
        rect.visibleProperty().bind(a.aliveProperty());

        return rect;
    }

    private Node createWall(Wall wall) {
        Rectangle rect = new Rectangle(UNIT_SIZE * GameController.WALL_WIDTH, UNIT_SIZE * GameController.WALL_HEIGHT);
        rect.setFill(Color.LIMEGREEN);

        rect.xProperty().bind(wall.columnProperty().add(wall.getColumn()).multiply(UNIT_SIZE * GameController.WALL_WIDTH));
        rect.setY(UNIT_SIZE * GameController.WALL_ROW);
        rect.visibleProperty().bind(wall.aliveProperty());
        return rect;
    }

    private Node createPlayer() {
        Rectangle player = new Rectangle(UNIT_SIZE * GameController.PLAYER_WIDTH, UNIT_SIZE * 2);
        player.setFill(Color.GREEN);

        player.xProperty().bind(game.getPlayer().columnProperty().multiply(UNIT_SIZE));
        player.setY(UNIT_SIZE * GameController.PLAYER_ROW);

        return player;
    }

    private Node createMissile(Missile m) {
        Rectangle missile = new Rectangle(UNIT_SIZE, UNIT_SIZE * 2);
        missile.setFill(Color.GRAY);

        missile.xProperty().bind(m.columnProperty().multiply(UNIT_SIZE).add(UNIT_SIZE));
        missile.yProperty().bind(m.rowProperty().multiply(UNIT_SIZE));
        missile.visibleProperty().bind(m.aliveProperty());

        return missile;
    }

    private void dispatchKeyEvents(KeyEvent e) {
        switch (e.getCode()) {
            case LEFT -> game.moveLeft();
            case RIGHT -> game.moveRight();
            case SPACE -> game.shoot();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
