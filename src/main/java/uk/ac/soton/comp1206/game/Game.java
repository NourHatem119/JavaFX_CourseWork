package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

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
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                grid.set(i,j,1);
            }
        }
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this piece
        GamePiece gamePiece = GamePiece.createPiece(5);
        int [][] piece = GamePiece.block;
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        System.out.println(x + "\n" + y);
        //Update the grid with the new piece
        if (checkPieceFits(x, y, piece)){
            setPiece(x, y, piece, gamePiece);
        }
    }

  /**
   *
   * @param x x-coordinate of the block clicked
   * @param y y-coordinate of the block clicked
   * @param piece array containing the piece to be inserted
   * @param gamePiece piece to be inserted
   */
  private void setPiece(int x, int y, int[][] piece, GamePiece gamePiece) {
        for (int i = x - 1; i <= x + 1; i++){
            for (int j = y - 1; j <= y + 1; j++){
                if (piece[i - x + 1][j - y + 1] > 0){
                    logger.info("Trying to put gamePiece");
                    grid.set(i, j, gamePiece.getValue());
                }
            }
        }
    }

    /**
     *
     * @param x x-coordinate of the block clicked
     * @param y y-coordinate of the block clicked
     * @param piece array containing the piece to be inserted
     * @return whether the piece fits or not
     */
    private boolean checkPieceFits(int x, int y, int[][] piece) {
        for (int i = x - 1; i <= x + 1; i++) {
          for (int j = y - 1; j <= y + 1; j++) {
              if (piece[i - x + 1][j - y + 1] > 0){
                  if (grid.get(i,j) != 1){
                      return false;
                  }
              }
          }
        }
        return true;
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


}
