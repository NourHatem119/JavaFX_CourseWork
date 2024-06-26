package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 * <p>
 * Extends Canvas and is responsible for drawing itself.
 * <p>
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * <p>
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

  /**
   * The set of colours for different pieces
   */
  public static final Color[] COLOURS = {
      Color.TRANSPARENT,
      Color.DEEPPINK,
      Color.RED,
      Color.ORANGE,
      Color.YELLOW,
      Color.YELLOWGREEN,
      Color.LIME,
      Color.GREEN,
      Color.DARKGREEN,
      Color.DARKTURQUOISE,
      Color.DEEPSKYBLUE,
      Color.AQUA,
      Color.AQUAMARINE,
      Color.BLUE,
      Color.MEDIUMPURPLE,
      Color.PURPLE
  };
  private static final Logger logger = LogManager.getLogger(GameBlock.class);
  private final GameBoard gameBoard;
  private final double width;
  private final double height;
  /**
   * The column this block exists as in the grid
   */
  private final int x;
  /**
   * The row this block exists as in the grid
   */
  private final int y;
  /**
   * The value of this block (0 = empty, otherwise specifies the colour to render as)
   */
  private final IntegerProperty value = new SimpleIntegerProperty(0);

  /**
   * Create a new single Game Block
   *
   * @param gameBoard the board this block belongs to
   * @param x         the column the block exists in
   * @param y         the row the block exists in
   * @param width     the width of the canvas to render
   * @param height    the height of the canvas to render
   */
  public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
    this.gameBoard = gameBoard;
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;

    //A canvas needs a fixed width and height
    setWidth(width);
    setHeight(height);

    //Do an initial paint
    paint();

    //When the value property is updated, call the internal updateValue method
    value.addListener(this::updateValue);
  }

  public GameBoard getGameBoard() {
    return gameBoard;
  }

  /**
   * When the value of this block is updated,
   *
   * @param observable what was updated
   * @param oldValue   the old value
   * @param newValue   the new value
   */
  private void updateValue(ObservableValue<? extends Number> observable, Number oldValue,
      Number newValue) {
    paint();
  }

  /**
   * Handle painting of the block canvas
   */
  public void paint() {
    //If the block is empty, paint as empty
    if (value.get() == 0) {
      paintEmpty();
    } else {
      //If the block is not empty, paint with the colour represented by the value
      paintColor(COLOURS[value.get()]);
    }

    if (isHover() && !(gameBoard instanceof PieceBoard)) {
      hoverBlock();
    }
  }

  /**
   * Paint this canvas empty
   */
  private void paintEmpty() {
    var gc = getGraphicsContext2D();

    //Clear
    gc.clearRect(0, 0, width, height);

    //Fill
    gc.setFill(new Color(0.6431372549019608, 0.2196078431372549, 0.12549019607843137, 0.4));
    gc.fillRect(0, 0, width, height);

    //Border
    gc.setStroke(Color.BLACK);
    gc.strokeRect(0, 0, width, height);
  }

  /**
   * Paint this canvas with the given colour
   *
   * @param colour the colour to paint
   */
  private void paintColor(Paint colour) {
    var gc = getGraphicsContext2D();

    //Clear
    gc.clearRect(0, 0, width, height);

    //Colour fill
    gc.setFill(colour);
    gc.fillRect(0, 0, width, height);
    gc.strokeRoundRect(0, 0, width, height, 30, 30);
    gc.strokeRoundRect(0, 0, width, height, 120, 120);

    //Border
    gc.setStroke(Color.BLACK);
    gc.strokeRect(0, 0, width, height);
  }

  /**
   * Get the column of this block
   *
   * @return column number
   */
  public int getX() {
    return x;
  }

  /**
   * Get the row of this block
   *
   * @return row number
   */
  public int getY() {
    return y;
  }

  /**
   * Get the current value held by this block, representing it's colour
   *
   * @return value
   */
  public int getValue() {
    return this.value.get();
  }

  /**
   * Bind the value of this block to another property. Used to link the visual block to a
   * corresponding block in the Grid.
   *
   * @param input property to bind the value to
   */
  public void bind(ObservableValue<? extends Number> input) {
    value.bind(input);
  }

  public void paintCircle() {
    GraphicsContext g = getGraphicsContext2D();
    g.setFill(new Color(0.75, 0.75, 0.75, 0.75));
    g.fillOval(width / 10, height / 10, width * 0.8, height * 0.8);
  }

  public void hoverBlock() {
    var gc = getGraphicsContext2D();
    gc.setFill(Color.rgb(255, 255, 255, 0.2));
    gc.fillRect(0, 0, width, height);
  }

  public void setIsHover(boolean hover) {
    setHover(hover);
    paint();
  }

  public void fadeOut() {
    logger.info("Fading out...");
    FadeTransition clear = new FadeTransition(Duration.millis(500), this);
    clear.setFromValue(1.0);
    clear.setToValue(0.0);
    clear.play();
    clear.setOnFinished(e -> {
      gameBoard.grid.set(getX(), getY(), 0);
      FadeTransition restore = new FadeTransition(Duration.millis(500), this);
      restore.setFromValue(0.0);
      restore.setToValue(1.0);
      restore.play();
    });
  }

}
