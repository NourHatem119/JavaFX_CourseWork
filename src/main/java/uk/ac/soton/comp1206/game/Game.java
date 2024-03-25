package uk.ac.soton.comp1206.game;

import java.util.Random;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game.
 * Methods to manipulate the game state and to handle actions made by the player should take
 * place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows.
     */
    protected final int rows;

    /**
     * Number of columns.
     */
    protected final int cols;

    /**
     * The grid model linked to the game.
     */
    protected final Grid grid;
    public GamePiece currentPiece;
    public Text name = new Text();

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
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game.
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start.
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked.
     *
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this piece
        Random rand = new Random();
        currentPiece = GamePiece.createPiece(rand.nextInt(15));
        name.setText(currentPiece.toString());
        int [][] piece = GamePiece.block;
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        System.out.println(x + "\n" + y);
        //Update the grid with the new piece
        if (checkPieceFits(x, y, piece)){
            setPiece(x, y, piece, currentPiece);
        }

        int[] fullRows = new int[grid.getRows()];
        int[] fullCols = new int[grid.getCols()];
        //checks all the columns if there are any columns to be cleared
        for (int col = 0; col < grid.getCols(); col++){
          fullCols[col] = checkColFull(col);
          if (fullCols[col] == 1){
            logger.info("Column Cleared");
            //Clearing the column
            clear(col, "Col");
          }
          fullCols[col] = 0;
        }
        //checks all the rows if there are any rows to be cleared
        for (int row = 0; row < grid.getRows(); row++){
          fullRows[row] = checkRowFull(row);
          if (fullRows[row] == 1){
            logger.info("Row Cleared");
            clear(row, "row");
          }
          fullRows[row] = 0;
        }

    }

  /**
   * Clears the given row/column.
   *
   * @param line index of the row/column to be cleared
   * @param type is it a row or a column
   */
  private void clear(int line, String type) {
      switch (type.toLowerCase()){
        case "col":
          for (int rows = 0; rows < grid.getRows(); rows++){
            grid.set(line, rows, 1);
          }
        case "row":
          for (int cols = 0; cols < grid.getRows(); cols++){
            grid.set(cols, line, 1);
          }
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
   *
   * @param row The row to be checked
   * @return whether the row is full or not
   */
  private int checkRowFull(int row){
      if(grid.get(0,row) != 1 && grid.get(1,row) != 1 && grid.get(2,row) != 1 && grid.get(3,row) != 1 && grid.get(4,row) != 1){
        return 1;
      } else {
        return 0;
      }
    }

  /**
   *
   * @param col The column to be checked
   * @return whether the column is full or not
   */
  private int checkColFull(int col){
      if(grid.get(col,0) != 1 && grid.get(col,1) != 1 && grid.get(col,2) != 1 && grid.get(col,
          3) != 1 && grid.get(col,4) != 1){
        return 1;
      } else {
        return 0;
      }
    }

  /**
   * Get the grid model inside this game representing the game state of the board.
   *
   * @return game grid model
   */
  public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game.
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game.
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }


}
