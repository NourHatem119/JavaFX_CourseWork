package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBoard;
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
    howToPlayPane.setMaxWidth(gameWindow.getWidth());
    howToPlayPane.setMaxHeight(gameWindow.getHeight());
    howToPlayPane.getStyleClass().add("menu-background");
    ImageView imageView = new ImageView();
    Image image = new Image("D:\\Projects\\Java\\Year1Semester2\\ProgrammingII\\coursework\\src\\main\\resources\\images\\Instructions.png");
    imageView.setImage(image);
    imageView.setFitHeight(gameWindow.getHeight());
    imageView.setFitWidth(gameWindow.getWidth() - 250);
    howToPlayPane.getChildren().add(imageView);
    root.getChildren().add(howToPlayPane);

    var mainPane = new BorderPane();
    howToPlayPane.getChildren().add(mainPane);
  }
}
