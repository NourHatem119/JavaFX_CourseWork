package uk.ac.soton.comp1206.scene;

import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ux.Multimedia;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  protected Game game;
  PieceBoard currentPieceShow = new PieceBoard(gameWindow.getWidth() / 4, gameWindow.getHeight() / 4);
  PieceBoard nextPieceShow = new PieceBoard(gameWindow.getWidth() / 4, gameWindow.getHeight() / 4);
  private Multimedia multimedia = new Multimedia();
  private Media music = new Media(new File("d:\\Uni\\Programming_II\\Coursework\\coursework"
      + "\\src\\main\\resources\\music\\game.wav").toURI().toString());


  /**
   * Create a new Single Player challenge scene
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

/**
 * Build the Challenge window
 */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    setupGame();

    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    //Set up the Level, Lives, and Multiplier & add them to the sidebar.
    var challengePane = new StackPane();
    var sideBar = new VBox();
    sideBar.setAlignment(Pos.CENTER);
    var score = new Label();
    score.setAlignment(Pos.TOP_LEFT);
    var level = new Text();
    var lives = new Text();
    var multiplier = new Text();
    sideBar.getChildren().addAll(level, lives, multiplier);

    // Bind the Score, Level, Lives, and Multiplier to their corresponding property.
    score.textProperty().bind(game.scoreProperty().asString("Score: %d"));
    level.textProperty().bind(game.levelProperty().asString("Level: %d"));
    lives.textProperty().bind(game.livesProperty().asString("Lives: %d"));
    multiplier.textProperty().bind(game.multiplierProperty().asString("Multi: %d"));

    //Change the styles of each component to its corresponding style.
    score.getStyleClass().add("score");
    level.getStyleClass().add("level");
    lives.getStyleClass().add("lives");
    multiplier.getStyleClass().add("multiplier");
    //Set up and show the challengePane.
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("challenge-background");
    root.getChildren().add(challengePane);

    var mainPane = new BorderPane();
    challengePane.getChildren().add(mainPane);

    //Set up the main GameBoard, add the main Gameboard and the sideBar.
    var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
    currentPieceShow.setIsCurrentPiece(true);
    sideBar.getChildren().addAll(currentPieceShow, nextPieceShow);
    mainPane.setCenter(board);
    mainPane.setRight(sideBar);
    mainPane.setLeft(score);

    //Handle block on gameboard grid being clicked
    board.setOnBlockClick(this::blockClicked);
    currentPieceShow.setOnPieceClick(this::blockClicked);
    nextPieceShow.setOnPieceClick(this::blockClicked);
    game.setOnNextPiece(this::nextPiece);
    board.setOnRightClicked(this::rightClicked);
  }

  private void rightClicked(MouseEvent event) {
    if (event.getButton().equals(MouseButton.SECONDARY)) {
      rotateCurrentPiece(game.getCurrentPiece());
      currentPieceShow.showPiece(game.getCurrentPiece());
    }
  }

  /**
   * Handle when a block is clicked
   * @param gameBlock the Game Block that was clocked
   */
  private void blockClicked(GameBlock gameBlock) {
    var gameBoard = gameBlock.getGameBoard();
    if (gameBoard instanceof PieceBoard) {
      boolean isCurrentPiece = ((PieceBoard) gameBoard).isCurrentPiece();
      if (isCurrentPiece) {
        logger.info("Current Piece Clicked...");
        game.rotateCurrentPiece("right");
        currentPieceShow.showPiece(game.getCurrentPiece());
      } else {
        logger.info("Next Piece Clicked...");
        game.swapPieces();
        currentPieceShow.showPiece(game.getCurrentPiece());
        nextPieceShow.showPiece(game.getNextPiece());
      }
    } else {
      game.blockClicked(gameBlock);
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
    multimedia.playBackGroundMusic(music);
    scene.setOnKeyPressed(this::keyClicked);
    currentPieceShow.showPiece(game.getCurrentPiece());
    nextPieceShow.showPiece(game.getNextPiece());
  }

    @Override
    public void keyClicked(KeyEvent keyClicked) {
      if (keyClicked.getCode().equals(KeyCode.ESCAPE)) {
        multimedia.stopMusic();
        gameWindow.startMenu();
      }
//      else if (keyClicked.getCode().equals(KeyCode.R) || keyClicked.getCode().equals(KeyCode.SPACE)) {
//        game.swapPieces();}
      else if(keyClicked.getCode().equals(KeyCode.Q) || keyClicked.getCode().equals(KeyCode.Z)) {
        game.rotateCurrentPiece("left");
        currentPieceShow.showPiece(game.getCurrentPiece());
      } else if(keyClicked.getCode().equals(KeyCode.E) || keyClicked.getCode().equals(KeyCode.C)) {
        rotateCurrentPiece(game.getCurrentPiece());
        currentPieceShow.showPiece(game.getCurrentPiece());
      }
    }

    public void nextPiece(GamePiece currentPiece, GamePiece nextPiece) {
      currentPieceShow.showPiece(currentPiece);
      nextPieceShow.showPiece(nextPiece);
    }
  public void rotateCurrentPiece(GamePiece currentPiece) {
    game.rotateCurrentPiece("right");
    currentPieceShow.showPiece(currentPiece);
  }

}
