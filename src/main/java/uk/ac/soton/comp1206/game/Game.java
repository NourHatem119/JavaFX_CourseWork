package uk.ac.soton.comp1206.game;


import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.GameOverListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to
 * manipulate the game state and to handle actions made by the player should take place inside this
 * class.
 */
public class Game {

  private static final Logger logger = LogManager.getLogger(Game.class);

  /**
   * Number of rows
   */
  protected final int rows;

  /**
   * Number of columns
   */
  protected final int cols;

  /**
   * The grid model linked to the game
   */
  protected final Grid grid;

  private Timer timer;

  private ScheduledExecutorService executor;
  ScheduledFuture<?> futureTask;

  public GamePiece getCurrentPiece() {
    return currentPiece;
  }

  protected GamePiece currentPiece;

  public GamePiece getNextPiece() {
    return nextPiece;
  }

  protected GamePiece nextPiece;
  public NextPieceListener nextPieceListener;

  public LineClearedListener lineClearedListener;

  public GameLoopListener gameLoopListener;
  public GameOverListener gameOverListener;

  public int getScore() {
    return score.get();
  }

  public IntegerProperty scoreProperty() {
    return score;
  }

  public void setScore(int score) {
    this.score.set(score);
  }

  public int getLevel() {
    return level.get();
  }

  public IntegerProperty levelProperty() {
    return level;
  }

  public void setLevel(int level) {
    this.level.set(level);
  }

  public int getLives() {
    return lives.get();
  }

  public IntegerProperty livesProperty() {
    return lives;
  }

  public void setLives(int lives) {
    this.lives.set(lives);
  }

  public int getMultiplier() {
    return multiplier.get();
  }

  public IntegerProperty multiplierProperty() {
    return multiplier;
  }

  public void setMultiplier(int multiplier) {
    this.multiplier.set(multiplier);
  }

  private final IntegerProperty score = new SimpleIntegerProperty(0);

  private final IntegerProperty level = new SimpleIntegerProperty(0);
  private final IntegerProperty lives = new SimpleIntegerProperty(3);
  private final IntegerProperty multiplier = new SimpleIntegerProperty(1);

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Game(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create a new grid model to represent the game state
    this.grid = new Grid(cols, rows);
    executor = Executors.newSingleThreadScheduledExecutor();
  }

  /**
   * Start the game
   */
  public void start() {
    logger.info("Starting game");
    initialiseGame();
  }

  /**
   * Initialise a new game and set up anything that needs to be done at the start
   */
  public void initialiseGame() {
    logger.info("Initialising game");
    currentPiece = spawnPiece();
    nextPiece = spawnPiece();
    createTimer();
  }

  /**
   * Handle what should happen when a particular block is clicked
   *
   * @param gameBlock the block that was clicked
   */
  public boolean blockClicked(GameBlock gameBlock) {
    //Get the position of this block
    int x = gameBlock.getX();
    int y = gameBlock.getY();

    if (grid.canPlayPiece(x, y, currentPiece)) {
      grid.playPiece(x, y, currentPiece);
      restartTimer();
      afterPiece();
      nextPiece();
      return true;
    } else {
      return false;
    }
  }

  /**
   * Get the grid model inside this game representing the game state of the board
   *
   * @return game grid model
   */
  public Grid getGrid() {
    return grid;
  }

  /**
   * Get the number of columns in this game
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the number of rows in this game
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  public GamePiece spawnPiece() {
    logger.info("Spawning a new Piece...");
    Random rand = new Random();
    return GamePiece.createPiece(rand.nextInt(GamePiece.PIECES));
  }

  protected void nextPiece() {
    currentPiece = nextPiece;
    nextPiece = spawnPiece();
    if (nextPieceListener != null) {
      nextPieceListener.nextPiece(currentPiece, nextPiece);
    }
    logger.info("Current Piece: {}", currentPiece);
  }

  /**
   * Handles the logic after each adding a piece.
   */
  private void afterPiece() {
    logger.info("Piece Placed, running afterPiece Procedures...");
    int linesCleared = 0;
    HashSet<GameBlockCoordinate> blocksToClear = new HashSet<>();
    for (int col = 0; col < grid.getCols(); col++) {
      HashSet<GameBlockCoordinate> candidateBlocks = checkColFull(col);
      if (!candidateBlocks.isEmpty()) {
        linesCleared++;
        blocksToClear.addAll(candidateBlocks);
      }
    }
    for (int row = 0; row < grid.getRows(); row++) {
      HashSet<GameBlockCoordinate> candidateBlocks = checkRowFull(row);
      if (!candidateBlocks.isEmpty()) {
        linesCleared++;
        blocksToClear.addAll(candidateBlocks);
      }

    }
    logger.info("Blocks To Clear {}", blocksToClear.size());
    if (linesCleared != 0) {
      lineCleared(blocksToClear);
    }
    score(linesCleared, blocksToClear.size());
    multiplier(linesCleared);
  }

  public boolean levelUp() {
    var oldLevel = getLevel();
    setLevel(getScore() / 1000);
    return oldLevel < getLevel();
  }

  /**
   * Checks if the given column is complete or not.
   *
   * @param col The column to be checked
   * @return whether the column is full or not
   */
  private HashSet<GameBlockCoordinate> checkColFull(int col) {
    HashSet<GameBlockCoordinate> blocksToClear = new HashSet<>();
    for (int i = 0; i < rows; i++) {
      if (grid.get(col, i) == 0) {
        blocksToClear.clear();
        break;
      }
      blocksToClear.add(new GameBlockCoordinate(col, i));
    }
    return blocksToClear;
  }

  /**
   * Checks if the given row is complete or not.
   *
   * @param row The row to be checked
   * @return whether the row is full or not
   */
  private HashSet<GameBlockCoordinate> checkRowFull(int row) {
    HashSet<GameBlockCoordinate> blocksToClear = new HashSet<>();
    for (int i = 0; i < cols; i++) {
      if (grid.get(i, row) == 0) {
        blocksToClear.clear();
        break;
      }
      blocksToClear.add(new GameBlockCoordinate(i, row));
    }
    return blocksToClear;
  }

  protected void score(int numberOfLines, int numberOfBlocks) {
    setScore((numberOfLines * numberOfBlocks * 10 * getMultiplier()) + getScore());
  }

  protected void multiplier(int linesCleared) {
    if (linesCleared > 0) {
      setMultiplier(getMultiplier() + 1);
    } else {
      setMultiplier(1);
    }
  }

  public void rotateCurrentPiece(String direction) {
    if (direction.equals("right")) {
      currentPiece.rotate();
    } else if (direction.equals("left")) {
      currentPiece.rotate(3);
    }
  }

  /**
   * Handles swapping pieces.
   */
  public void swapPieces() {
    logger.info("Swapping Pieces...");
    var temp = this.currentPiece;
    this.currentPiece = this.nextPiece;
    this.nextPiece = temp;
  }

  public void setOnNextPiece(NextPieceListener listener) {
    this.nextPieceListener = listener;
  }

  public void setOnLineCleared(LineClearedListener listener) {
    this.lineClearedListener = listener;
  }

  private void lineCleared(HashSet<GameBlockCoordinate> blocks) {
    if (lineClearedListener != null) {
      lineClearedListener.lineCleared(blocks);
    }
  }

  public int getTimerDelay() {
//    return Math.max(2500, 12000 - 500 * getLevel());
    return 2500;
  }

  public void setGameLoop(GameLoopListener listener) {
    this.gameLoopListener = listener;
  }

  public void gameLoop() {
    if (getLives() <= 0) {
      logger.info("end Game...");
      if (gameOverListener != null) {
        endGame();
        Platform.runLater(() -> gameOverListener.gameOver(this));
      }
    } else {
      setLives(getLives() - 1);
      nextPiece();
      setMultiplier(1);
      restartTimer();
    }
  }

  protected void createTimer() {
//    timer = new Timer();
//    TimerTask task = new TimerTask() {
//      @Override
//      public void run() {
//        gameLoop();
//      }
//    };
//    timer.schedule(task, getTimerDelay());
//    if (gameLoopListener != null) {
//      gameLoopListener.gameLoop(getTimerDelay());
//    }
    futureTask = executor.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
    if (gameLoopListener != null) {
      gameLoopListener.gameLoop(getTimerDelay());
    }
  }

  private void restartTimer() {
    if (futureTask != null) {
      futureTask.cancel(true);
    }
    createTimer();
  }

  public void endGame() {
    logger.info("Game Finished...");
    executor.shutdownNow();
  }

  public void setOnGameOver(GameOverListener listener) {
    this.gameOverListener = listener;
  }
}
