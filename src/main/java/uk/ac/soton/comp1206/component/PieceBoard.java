package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;

public class PieceBoard extends GameBoard {

  private static final Logger logger = LogManager.getLogger(PieceBoard.class);

  BlockClickedListener pieceClickedListener;
  private boolean isCurrentPiece = false;

  /**
   * Creates the Board for the piece.
   *
   * @param width  the width of the board
   * @param height the height of the board
   */
  public PieceBoard(double width, double height) {
    super(3, 3, width, height);
  }

  public boolean isCurrentPiece() {
    return isCurrentPiece;
  }

  public void setIsCurrentPiece(boolean currentPiece) {
    this.isCurrentPiece = currentPiece;
  }

  /**
   * Paints the circle.
   */
  public void showCircle() {
    blocks[1][1].paintCircle();
  }

  /**
   * Sets the show for a piece.
   *
   * @param gamePiece the piece to be shown
   */
  public void showPiece(GamePiece gamePiece) {
    this.grid.clean();
    this.grid.playPiece(1, 1, gamePiece);
    if (isCurrentPiece) {
      showCircle();
    }
  }

  /**
   * Create a block at the given x and y position in the PieceBoard.
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
    block.setOnMouseClicked((e) -> blockClicked(e, block));

    return block;
  }

  /**
   * Set the listener to handle an event when a piece is clicked.
   *
   * @param listener listener to add
   */
  public void setOnPieceClick(BlockClickedListener listener) {
    this.pieceClickedListener = listener;
  }

  /**
   * Triggered when a block is clicked. Call the attached listener.
   *
   * @param event mouse event
   * @param block block clicked on
   */
  private void blockClicked(MouseEvent event, GameBlock block) {
    logger.info("Block clicked: {}", block);

    if (pieceClickedListener != null) {
      pieceClickedListener.blockClicked(block, event);
    }
  }

}
