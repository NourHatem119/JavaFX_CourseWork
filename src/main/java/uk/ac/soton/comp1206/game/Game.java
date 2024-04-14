package uk.ac.soton.comp1206.game;



import java.util.HashSet;
import java.util.Random;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
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

    protected GamePiece currentPiece ;

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
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
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
        currentPiece=spawnPiece();
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();


      if(grid.canPlayPiece(x,y,currentPiece)){
        grid.playPiece(x,y,currentPiece);
        afterPiece();
        nextPiece();
      }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

  public static GamePiece spawnPiece() {
      logger.info("Spawning a new Piece...");
      Random rand = new Random();
      return GamePiece.createPiece(rand.nextInt(GamePiece.PIECES));
  }
  private void nextPiece() {
    currentPiece = spawnPiece();
    logger.info("Current Piece: {}", currentPiece);
  }
  /**
   * Handles the logic after each adding a piece.
   */
  private void afterPiece() {
    logger.info("Piece Placed, running afterPiece Procedures...");
    int blocksCleared = 0;
    HashSet<GameBlockCoordinate> blocksToClear=new HashSet<>();
    boolean[] fullRows = new boolean[grid.getRows()];
    boolean[] fullCols = new boolean[grid.getCols()];
    for (int col = 0; col < grid.getCols(); col++){
      HashSet<GameBlockCoordinate> candidateBlocks=checkColFull(col);
      if(!candidateBlocks.isEmpty()) {
        fullCols[col] = true;
        blocksToClear.addAll(candidateBlocks);
      }
    }
    for (int row = 0; row < grid.getRows(); row++){
      HashSet<GameBlockCoordinate> candidateBlocks=checkRowFull(row);
      if(!candidateBlocks.isEmpty()) {
        fullRows[row] = true;
        blocksToClear.addAll(candidateBlocks);
      }

    }
    logger.info("Blocks To Clear {}", blocksToClear.size());
    for (GameBlockCoordinate block : blocksToClear) {
      grid.set(block.getX(),block.getY(),0);
    }
//    int linesCleared = 0;
//    for (int i = 0; i < grid.getCols(); i++) {
//      if (fullCols[i] == 1) {
//        logger.info("Column Cleared");
//        blocksCleared += clear(i, "col");
//        linesCleared++;
//      }
//      if (fullRows[i] == 1) {
//        logger.info("Row Cleared");
//        blocksCleared += clear(i, "row");
//        linesCleared++;
//      }
//    }
//    int scoreIncrease = linesCleared * blocksCleared * 10 * multiplier.get() + score.get();
//    if (linesCleared != 0) {
//      setMultiplier(getMultiplier() + 1);
//    } else {
//      setMultiplier(1);
//    }
//    score.set(scoreIncrease);
//    setLevel(score.get() / 1000);
  }

  /**
   * Checks if the given column is complete or not.
   * @param col The column to be checked
   * @return whether the column is full or not
   */
  private HashSet<GameBlockCoordinate> checkColFull(int col){
    HashSet<GameBlockCoordinate> blocksToClear = new HashSet<>();
    for (int i = 0; i < rows; i++){
      if (grid.get(col, i) == 0){
        blocksToClear.clear();
        break;
      }
      blocksToClear.add(new GameBlockCoordinate(col,i));
    }
    return blocksToClear;
  }
  /**
   * Checks if the given row is complete or not.
   *
   * @param row The row to be checked
   * @return whether the row is full or not
   */
  private HashSet<GameBlockCoordinate> checkRowFull(int row){
    HashSet<GameBlockCoordinate> blocksToClear = new HashSet<>();
    for (int i = 0; i < cols; i++){
      if (grid.get(i, row) == 0){
        blocksToClear.clear();
        break;
      }
      blocksToClear.add(new GameBlockCoordinate(i,row));
    }
    return blocksToClear;
  }
}
