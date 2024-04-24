package uk.ac.soton.comp1206.scene;

import java.io.File;
import java.security.Key;
import java.util.HashSet;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.ScoresList;
import uk.ac.soton.comp1206.ux.Multimedia;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the
 * game.
 */
public class ChallengeScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
  protected Game game;
  PieceBoard currentPieceShow = new PieceBoard(gameWindow.getWidth() / 4.0,
      gameWindow.getHeight() / 4.0);
  PieceBoard nextPieceShow = new PieceBoard(gameWindow.getWidth() / 4.0 - 40,
      gameWindow.getHeight() / 4.0 - 40);
  GameBoard board;
  Rectangle timeBar;
  protected static final Multimedia multimedia = new Multimedia();
  private final String path = "d:\\Uni\\P_II\\Coursework\\coursework\\src\\main"
      + "\\resources";
  protected final Media music = new Media(new File(path + "\\music\\game.wav").toURI().toString());
  private final Media rotateEffect =
      new Media(new File(path + "\\sounds\\rotate.wav").toURI().toString());
  private final Media placeEffect =
      new Media(new File(path + "\\sounds\\place.wav").toURI().toString());
  private final Media lineClearEffect =
      new Media(new File(path + "\\sounds\\clear.wav").toURI().toString());
  private final Media failEffect =
      new Media(new File(path + "\\sounds\\fail.wav").toURI().toString());
  private final Media levelUpEffect =
      new Media(new File(path + "\\sounds\\level.wav").toURI().toString());
  private final Media gameOverEffect =
      new Media(new File(path + "\\sounds\\explode.wav").toURI().toString());


  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Challenge Scene");
  }
  protected  HBox buildTopBar(String title){
    var scoreValue = new Text();
    var scoreText = new Text("Score");
    scoreText.getStyleClass().add("heading");
    var scoreBox = new VBox(scoreText, scoreValue);

    var livesValue = new Text();
    var livesText = new Text("Lives");
    livesText.getStyleClass().add("heading");
    var livesBox = new VBox(livesText, livesValue);
    livesBox.setAlignment(Pos.CENTER);


    var titleTxt = new Text(title);
    titleTxt.getStyleClass().add("title");

    var topBar = new HBox();
    topBar.setSpacing(170);
    topBar.setPadding(new Insets(topBar.getPadding().getTop(), 20,
        topBar.getPadding().getBottom(), 10));

    topBar.getChildren().addAll(scoreBox, titleTxt, livesBox);
    scoreValue.textProperty().bind(game.scoreProperty().asString());
    livesValue.textProperty().bind(game.livesProperty().asString());
    scoreValue.getStyleClass().add("score");
    livesValue.getStyleClass().add("lives");
    return topBar;
  }

  protected VBox buildSideBar(){
    var highScoreText = new Text("HighScore");
    highScoreText.getStyleClass().add("heading");
    var highScoreBox = new VBox(highScoreText);
    highScoreBox.setAlignment(Pos.CENTER);
    Integer highScore = getHighScore();
    logger.info("HighScore : {}", highScore);
    var highScoreValue = new Text(highScore.toString());
    highScoreValue.getStyleClass().add("score");
    highScoreBox.getChildren().add(highScoreValue);

    var levelValue = new Text();
    var levelText = new Text("Level");
    levelText.getStyleClass().add("heading");
    var levelBox = new VBox(levelText, levelValue);
    levelBox.setAlignment(Pos.CENTER);

    var sideBar = new VBox();
    sideBar.setAlignment(Pos.CENTER);
    sideBar.setPadding(new Insets(sideBar.getPadding().getTop(), 10,
        sideBar.getPadding().getBottom(), sideBar.getPadding().getLeft()));
    sideBar.setSpacing(15);
    sideBar.getChildren().addAll(highScoreBox, levelBox);

    levelValue.textProperty().bind(game.levelProperty().asString());

    levelValue.getStyleClass().add("level");
    highScoreValue.getStyleClass().add("score");
    currentPieceShow.setIsCurrentPiece(true);
    sideBar.getChildren().addAll(currentPieceShow, nextPieceShow);

    return sideBar;
  }

  VBox buildBottomBar() {
    timeBar = new Rectangle();
    VBox bottomBar = new VBox(timeBar);
    timeBar.setFill(Color.LIMEGREEN);
    timeBar.setHeight(20.0);
    timeBar.setWidth(gameWindow.getWidth());
    return bottomBar;
  }

  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    setupGame();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    //Set up the Level and add it to the right, and set the score and lives and add them
    // to the top bar with the title.
    var challengePane = new StackPane();
    // Bind the Score, Level, Lives to their corresponding property.
    //Change the styles of each component to its corresponding style.
    //Set up and show the challengePane.
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("challenge-background");
    root.getChildren().add(challengePane);

    var mainPane = new BorderPane();
    challengePane.getChildren().add(mainPane);

    //Set up the main GameBoard, add the main Gameboard and the rightBar.
    logger.info("Width: {}, Height: {}",gameWindow.getWidth() / 2, gameWindow.getHeight() / 2);
    board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2,
        gameWindow.getHeight() / 2);

    mainPane.setCenter(board);
    mainPane.setRight(buildSideBar());
    mainPane.setBottom(buildBottomBar());
    if (this instanceof MultiPlayerScene) {
      HBox topBar = buildTopBar("MultiPlayer Match");
      topBar.setSpacing(164);
      mainPane.setTop(topBar);
    } else {
      HBox topBar = buildTopBar("Challenge Mode");
      topBar.setSpacing(179);
      mainPane.setTop(topBar);
    }
    //Handle block on gameboard grid being clicked
    board.setOnBlockClick(this::blockClicked);
    currentPieceShow.setOnPieceClick(this::pieceClicked);
    nextPieceShow.setOnPieceClick(this::pieceClicked);
    game.setOnNextPiece(this::nextPiece);
    board.setOnRightClicked(this::rightClicked);
    game.setOnLineCleared(this::lineCleared);
    game.setGameLoop(this::gameLoop);
    game.setOnGameOver(this::gameOver);
  }

  protected void gameLoop(int i) {
    logger.info("GameLoop Started...");
    Timeline timePass = new Timeline(
        new KeyFrame(Duration.ZERO, new KeyValue(timeBar.widthProperty(), gameWindow.getWidth())),
        new KeyFrame(Duration.ZERO, new KeyValue(timeBar.fillProperty(), Color.LIMEGREEN)),
        new KeyFrame(Duration.millis((double) i / 2), new KeyValue(timeBar.fillProperty(),
            Color.ORANGE)),
        new KeyFrame(Duration.millis(i), new KeyValue(timeBar.fillProperty(),
            Color.RED)),
        new KeyFrame(Duration.millis(i), new KeyValue(timeBar.widthProperty(), 0))
    );
    timePass.setCycleCount(1);
    Timeline colorChange = new Timeline(
        new KeyFrame(Duration.ZERO, e -> timeBar.setFill(Color.LIMEGREEN)),
        new KeyFrame(Duration.millis((double) i / 2), e -> timeBar.setFill(Color.ORANGE)),
        new KeyFrame(Duration.millis(i), e -> timeBar.setFill(Color.RED))
    );
    colorChange.setCycleCount(1);
    timePass.play();
    colorChange.play();

  }

  protected void rightClicked(MouseEvent event) {
    if (event.getButton().equals(MouseButton.SECONDARY)) {
      rotateCurrentPiece(game.getCurrentPiece(), "right");
      currentPieceShow.showPiece(game.getCurrentPiece());
    }
  }

  /**
   * Handle when a block is clicked.
   *
   * @param gameBlock The Game Block that was clocked
   * @param event The mouse event that has occurred
   */
  protected void blockClicked(GameBlock gameBlock, MouseEvent event) {
    boolean canPlay;
    if (event.getButton().equals(MouseButton.PRIMARY)) {
      canPlay = game.blockClicked(gameBlock);
      if (!canPlay) {
        multimedia.playAudio(failEffect);
      } else {
        multimedia.playAudio(placeEffect);
        boolean leveledUp = game.levelUp();
        if (leveledUp) {
          multimedia.playAudio(levelUpEffect);
        }
      }
    }
  }

  /**
   * Handles when a piece is clicked.
   *
   * @param block the Game Block that was clocked
   * @param event The mouse event that has occurred
   */
  protected void pieceClicked(GameBlock block, MouseEvent event) {
    boolean isCurrentPiece = ((PieceBoard) block.getGameBoard()).isCurrentPiece();
    if (isCurrentPiece) {
      logger.info("Current Piece Clicked...");
      rotateCurrentPiece(game.getCurrentPiece(), "right");
    } else {
      logger.info("Next Piece Clicked...");
      swapPieces();
    }
  }

  /**
   * Set up the game object and model
   */
  public void setupGame() {
    logger.info("Starting a new challenge");

    //Start new game
    game = new Game(5, 5);
  }

  /**
   * Initialise the scene and start the game
   */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");
    game.start();
//    multimedia.playBackGroundMusic(music);
    scene.setOnKeyPressed(this::keyClicked);
    currentPieceShow.showPiece(game.getCurrentPiece());
    nextPieceShow.showPiece(game.getNextPiece());
  }


  @Override
  protected void keyClicked(KeyEvent keyClicked) {
    if (keyClicked.getCode().equals(KeyCode.ESCAPE)) {
//      multimedia.stopMusic();
      game.endGame();
      gameWindow.startMenu();
    } else if (keyClicked.getCode().equals(KeyCode.R) || keyClicked.getCode()
        .equals(KeyCode.SPACE)) {
      swapPieces();
    } else if (keyClicked.getCode().equals(KeyCode.Q) || keyClicked.getCode().equals(KeyCode.Z)) {
      rotateCurrentPiece(game.getCurrentPiece(), "left");
    } else if (keyClicked.getCode().equals(KeyCode.E) || keyClicked.getCode().equals(KeyCode.C)) {
      rotateCurrentPiece(game.getCurrentPiece(), "right");
      currentPieceShow.showPiece(game.getCurrentPiece());
    } else if (keyClicked.getCode().equals(KeyCode.UP) || keyClicked.getCode().equals(KeyCode.W)) {
      board.upClicked();
    } else if (keyClicked.getCode().equals(KeyCode.DOWN) || keyClicked.getCode().equals(KeyCode.S)) {
      board.downClicked();
    } else if (keyClicked.getCode().equals(KeyCode.RIGHT) || keyClicked.getCode().equals(KeyCode.D)) {
      board.rightArrowClicked();
    } else if (keyClicked.getCode().equals(KeyCode.LEFT) || keyClicked.getCode().equals(KeyCode.A)) {
      board.leftArrowClicked();
    } else if (keyClicked.getCode().equals(KeyCode.ENTER) || keyClicked.getCode()
        .equals(KeyCode.X)) {
      boolean placed = game.blockClicked(board.getCurrentBlock());
      if (placed) {
        multimedia.playAudio(placeEffect);
      }
    }
  }

  private void swapPieces() {
    multimedia.playAudio(rotateEffect);
    game.swapPieces();
    currentPieceShow.showPiece(game.getCurrentPiece());
    nextPieceShow.showPiece(game.getNextPiece());
  }

  protected void nextPiece(GamePiece currentPiece, GamePiece nextPiece) {
    currentPieceShow.showPiece(currentPiece);
    nextPieceShow.showPiece(nextPiece);
  }

  private void rotateCurrentPiece(GamePiece currentPiece, String direction) {
    multimedia.playAudio(rotateEffect);
    game.rotateCurrentPiece(direction);
    logger.info("Current Piece Rotated...");
    currentPieceShow.showPiece(currentPiece);
  }

  protected void lineCleared(HashSet<GameBlockCoordinate> blocks) {
    multimedia.playAudio(lineClearEffect);
    logger.info("Blocks Coordinates {}", blocks);
    board.fadeOut(blocks);
  }

  protected void gameOver(Game currentGame) {
//    multimedia.stopMusic();
    multimedia.playAudio(gameOverEffect);
    gameWindow.startScoresScene(game);

  }

  private Integer getHighScore() {
    return ScoresScene.getHighScore();
  }

}
