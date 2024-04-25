package uk.ac.soton.comp1206.scene;

import java.util.Objects;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ux.Multimedia;

public class OpeningScene extends BaseScene {

  SequentialTransition sequential;

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public OpeningScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public void initialise() {
    Multimedia.playAudio(Multimedia.opening);
    scene.setOnKeyPressed(this::skip);
  }

  @Override
  public void build() {
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    var openingPane = new StackPane();
    openingPane.setMaxHeight(gameWindow.getHeight());
    openingPane.setMaxWidth(gameWindow.getWidth());
    openingPane.getStyleClass().add("intro");
    root.getChildren().add(openingPane);
    ImageView opening = new ImageView(
        new Image(Objects.requireNonNull(getClass().
            getResourceAsStream("/_images/ECSGames.png"))));
    opening.setPreserveRatio(true);
    opening.setFitWidth(400);
    openingPane.getChildren().add(opening);

    RotateTransition rotation = new RotateTransition(Duration.millis(2000), opening);
    rotation.setFromAngle(6.0);
    rotation.setToAngle(-6.0);
    FadeTransition fade = new FadeTransition(Duration.millis(500), opening);
    fade.setToValue(0);

    sequential = new SequentialTransition(new Animation[]{(Animation) rotation, (Animation) fade});
    sequential.play();
    sequential.setOnFinished(e -> {
      gameWindow.startMenu();
    });
  }

  public void skip(KeyEvent anyKey) {
    gameWindow.startMenu();
    sequential.stop();
  }
}
