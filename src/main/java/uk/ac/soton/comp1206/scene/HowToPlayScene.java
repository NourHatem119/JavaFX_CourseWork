package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class HowToPlayScene extends BaseScene{


  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public HowToPlayScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public void initialise() {

  }

  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    var howToPlayPane = new StackPane();
    var title = new Text("Instructions");
    title.getStyleClass().add("title");
    var instructionsText = new Text("TetrECS is a fast-paced gravity-free block placement game, "
        + "where you must survive by clearing rows through careful placement of the upcoming "
        + "blocks before the time runs out. Lose all 3 lives and you're destroyed!");
    instructionsText.getStyleClass().add("instructions");
    howToPlayPane.setMaxWidth(gameWindow.getWidth());
    howToPlayPane.setMaxHeight(gameWindow.getHeight());
    howToPlayPane.getStyleClass().add("menu-background");
    ImageView imageView = new ImageView();
    Image image = new Image("D:\\Projects\\Java\\Year1Semester2\\ProgrammingII\\coursework\\src\\main\\resources\\images\\Instructions.png");
    imageView.setImage(image);
    imageView.setPreserveRatio(true);
    imageView.setFitHeight(gameWindow.getHeight());
    imageView.setFitWidth(gameWindow.getWidth());
    howToPlayPane.getChildren().add(title);
    howToPlayPane.getChildren().add(instructionsText);
    StackPane.setAlignment(title, Pos.TOP_CENTER);
    StackPane.setAlignment(instructionsText, Pos.TOP_CENTER);
    howToPlayPane.getChildren().add(imageView);
    root.getChildren().add(howToPlayPane);

    var mainPane = new BorderPane();
    howToPlayPane.getChildren().add(mainPane);
  }
}
