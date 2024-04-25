package uk.ac.soton.comp1206.component;

import java.util.HashSet;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard. It extends a GridPane to
 * hold a grid of GameBlocks.
 * <p>
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming
 * block. It also be linked to an external grid, for the main game board.
 * <p>
 * The GameBoard is only a visual representation and should not contain game logic or model logic in
 * it, which should take place in the Grid.
 */
public class GameBoard extends GridPane {

  private static final Logger logger = LogManager.getLogger(GameBoard.class);

  /**
   * Number of columns in the board
   */
  protected final int cols;

  /**
   * Number of rows in the board
   */
  protected final int rows;

  /**
   * The visual width of the board - has to be specified due to being a Canvas
   */
  protected final double width;

  /**
   * The visual height of the board - has to be specified due to being a Canvas
   */
  protected final double height;
  /**
   * The grid this GameBoard represents
   */
  final Grid grid;
  /**
   * The blocks inside the grid
   */
  GameBlock[][] blocks;
  private GameBlock currentBlock;
  /**
   * The listener to call when a specific block is clicked
   */
  private BlockClickedListener blockClickedListener;
  /**
   * The listener to call when right click is triggered.
   */
  private RightClickedListener rightClickedListener;

  /**
   * Create a new GameBoard, based off a given grid, with a visual width and height.
   *
   * @param grid   linked grid
   * @param width  the visual width
   * @param height the visual height
   */
  public GameBoard(Grid grid, double width, double height) {
    this.cols = grid.getCols();
    this.rows = grid.getRows();
    this.width = width;
    this.height = height;
    this.grid = grid;

    //Build the GameBoard
    build();
  }


  /**
   * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows,
   * along with the visual width and height.
   *
   * @param cols   number of columns for internal grid
   * @param rows   number of rows for internal grid
   * @param width  the visual width
   * @param height the visual height
   */
  public GameBoard(int cols, int rows, double width, double height) {
    this.cols = cols;
    this.rows = rows;
    this.width = width;
    this.height = height;
    this.grid = new Grid(cols, rows);

    //Build the GameBoard
    build();
  }

  public GameBlock getCurrentBlock() {
    return currentBlock;
  }

  /**
   * Get a specific block from the GameBoard, specified by it's row and column
   *
   * @param x column
   * @param y row
   * @return game block at the given column and row
   */
  public GameBlock getBlock(int x, int y) {
    return blocks[x][y];
  }

  /**
   * Build the GameBoard by creating a block at every x and y column and row
   */
  protected void build() {
    logger.info("Building grid: {} x {}", cols, rows);

    setMaxWidth(width);
    setMaxHeight(height);

    setGridLinesVisible(true);

    blocks = new GameBlock[cols][rows];

    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        createBlock(x, y);
      }
    }
  }

  /**
   * Create a block at the given x and y position in the GameBoard
   *
   * @param x column
   * @param y row
   */
  protected GameBlock createBlock(int x, int y) {
    var blockWidth = width / cols;
    var blockHeight = height / rows;

    //Create a new GameBlock UI component
    GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

    //Add to the GridPane
    add(block, x, y);

    //Add to our block directory
    blocks[x][y] = block;

    //Link the GameBlock component to the corresponding value in the Grid
    block.bind(grid.getGridProperty(x, y));

    //Add a mouse click handler to the block to trigger GameBoard blockClicked method
    block.setOnMouseClicked(e -> {
      if (e.getButton().equals(MouseButton.PRIMARY)) {
        blockClicked(e, block);
      } else if (e.getButton().equals(MouseButton.SECONDARY)) {
        rightClicked(e);
      }
    });

    block.setOnMouseEntered((e -> hoverBlock(block)));
    block.setOnMouseExited((e -> unhover(block)));

    return block;
  }

  /**
   * Set the listener to handle an event when a block is clicked
   *
   * @param listener listener to add
   */
  public void setOnBlockClick(BlockClickedListener listener) {
    this.blockClickedListener = listener;
  }

  /**
   * Triggered when a block is clicked. Call the attached listener.
   *
   * @param event mouse event
   * @param block block clicked on
   */
  private void blockClicked(MouseEvent event, GameBlock block) {
    logger.info("Block clicked: {}", block);

    if (blockClickedListener != null) {
      blockClickedListener.blockClicked(block, event);
    }
  }

  /**
   * Set the listener to handle an event when right click is triggered
   *
   * @param listener listener to add
   */
  public void setOnRightClicked(RightClickedListener listener) {
    this.rightClickedListener = listener;
  }

  private void rightClicked(MouseEvent rightClick) {
    logger.info("RightClick triggered on main GameBoard...");
    if (rightClickedListener != null) {
      rightClickedListener.rightClicked(rightClick);
    }
  }

  private void hoverBlock(GameBlock block) {
    if (currentBlock != null) {
      unhover(currentBlock);
    }
    currentBlock = block;
    currentBlock.setIsHover(true);
  }

  private void unhover(GameBlock block) {
    block.setIsHover(false);
  }

  public void upClicked() {
    if (currentBlock == null) {
      currentBlock = blocks[0][0];
      hoverBlock(currentBlock);
    } else if (currentBlock.getY() == 0) {
      currentBlock = blocks[currentBlock.getX()][0];
      hoverBlock(currentBlock);
    } else {
      unhover(currentBlock);
      currentBlock = blocks[currentBlock.getX()][currentBlock.getY() - 1];
      hoverBlock(currentBlock);
    }
  }

  public void downClicked() {
    if (currentBlock == null) {
      currentBlock = blocks[0][0];
      hoverBlock(currentBlock);
    } else if (currentBlock.getY() == 4) {
      currentBlock = blocks[currentBlock.getX()][4];
      hoverBlock(currentBlock);
    } else {
      unhover(currentBlock);
      currentBlock = blocks[currentBlock.getX()][currentBlock.getY() + 1];
      hoverBlock(currentBlock);
    }
  }

  public void rightArrowClicked() {
    if (currentBlock == null) {
      currentBlock = blocks[0][0];
      hoverBlock(currentBlock);
    } else if (currentBlock.getX() == 4) {
      currentBlock = blocks[4][currentBlock.getY()];
      hoverBlock(currentBlock);
    } else {
      unhover(currentBlock);
      currentBlock = blocks[currentBlock.getX() + 1][currentBlock.getY()];
      hoverBlock(currentBlock);
    }
  }

  public void leftArrowClicked() {
    if (currentBlock == null) {
      currentBlock = blocks[0][0];
      hoverBlock(currentBlock);
    } else if (currentBlock.getX() == 0) {
      currentBlock = blocks[0][currentBlock.getY()];
      hoverBlock(currentBlock);
    } else {
      unhover(currentBlock);
      currentBlock = blocks[currentBlock.getX() - 1][currentBlock.getY()];
      hoverBlock(currentBlock);
    }
  }

  public void playPiece(GamePiece piece) {
    grid.playPiece(currentBlock.getX(), currentBlock.getY(), piece);
  }

  public void fadeOut(HashSet<GameBlockCoordinate> blocks) {
    for (GameBlockCoordinate block : blocks) {
      this.blocks[block.getX()][block.getY()].fadeOut();
    }
  }

}
