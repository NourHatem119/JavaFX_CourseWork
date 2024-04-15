package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionsScene extends BaseScene{


  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public InstructionsScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public void initialise() {
    logger.info("Initialising the Instructions Scene");
    scene.setOnKeyPressed(this::keyClicked);
  }

  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    var howToPlayPane = new BorderPane();
    howToPlayPane.setMaxWidth(gameWindow.getWidth());
    howToPlayPane.setMaxHeight(gameWindow.getHeight());
    howToPlayPane.getStyleClass().add("instructions-background");
    var content = new VBox();
    content.setAlignment(Pos.CENTER);
    howToPlayPane.setTop(content);
    var title = new Text("Instructions");
    title.getStyleClass().add("title");
    content.getChildren().add(title);
    var instructionsText = new Text("TetrECS is a fast-paced gravity-free block placement game, "
        + "where you must survive by clearing rows through careful placement of the upcoming\n"
        + "blocks before the time runs out. Lose all 3 lives and you're destroyed!");
    instructionsText.getStyleClass().add("instructions");
    content.getChildren().add(instructionsText);
    //Set Up the How to Play Image
    ImageView imageView = new ImageView();
    Image image = new Image(getClass().getResourceAsStream("/_images/Instructions.png"));
    imageView.setImage(image);
    imageView.setPreserveRatio(true);
    imageView.setFitHeight(gameWindow.getHeight() / 1.75);
    imageView.setFitWidth(gameWindow.getWidth() / 1.75);
    content.getChildren().add(imageView);
    var pieces = generatePieces();
    var piecesText = new Text();
    piecesText.setText("Pieces");
    piecesText.getStyleClass().add("title");
    content.getChildren().add(piecesText);
    content.getChildren().add(pieces);
    root.getChildren().add(howToPlayPane);
  }

  private GridPane generatePieces(){
    var piecesGrid = new GridPane();
    piecesGrid.setAlignment(Pos.CENTER);
    piecesGrid.setVgap(10);
    piecesGrid.setHgap(20);
    int counter = 0;
    piecesGrid.setAlignment(Pos.BOTTOM_CENTER);
    for(int y = 0; y < 3; y++){
      for(int x = 0; x < 5; x++){
        PieceBoard board = new PieceBoard(60,60);
        board.showPiece(GamePiece.createPiece(counter));
        counter++;
        piecesGrid.add(board, x, y);
      }
    }
    return piecesGrid;
  }
}
