package uk.ac.soton.comp1206.scene;

import java.util.Objects;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ux.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 * TODO add Menu Animations and Visual Effects
 */
public class MenuScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);

  /**
   * Create a new menu scene.
   *
   * @param gameWindow the Game Window this will be displayed in
   */
  public MenuScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Menu Scene");
  }

  /**
   * Build the menu layout.
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var menuPane = new StackPane();
    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add("menu-background");
    root.getChildren().add(menuPane);

    var mainPane = new BorderPane();
    menuPane.getChildren().add(mainPane);

    //Awful title
    Image title = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream("/_images/TetrECS.png")));
    ImageView titleView = new ImageView(title);
    titleView.setFitWidth(700);
    titleView.setFitHeight(150);
    RotateTransition rotation = new RotateTransition(Duration.millis(2000), titleView);
    rotation.setToAngle(5);
    rotation.setFromAngle(-5);
    rotation.setAutoReverse(true);
    rotation.setCycleCount(Animation.INDEFINITE);
    rotation.play();
//        var title = new Text("TetrECS");
//        title.getStyleClass().add("title");
    mainPane.setCenter(titleView);
    BorderPane.setAlignment(titleView, Pos.BOTTOM_CENTER);

    //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
    var play = new Button("Play");
    var multiplayer = new Button("Multiplayer");
    var instructions = new Button("How To Play");
    var exit = new Button("Exit");
    var buttons = new VBox(play, multiplayer, instructions, exit);
    for (Node button : buttons.getChildren()) {
      button.getStyleClass().add("button");
    }
    buttons.setAlignment(Pos.CENTER);
    mainPane.setBottom(buttons);

    //Bind the button action to the startGame method in the menu
    play.setOnMouseClicked(this::startGame);
    play.setOnKeyPressed(e -> {
      if (e.getCode().equals(KeyCode.ENTER)) {
        startGame(e);
      }
    });
    instructions.setOnMouseClicked(this::startInstructions);
    multiplayer.setOnAction(this::startLobby);
    exit.setOnAction(this::exitGame);
    mainPane.setOnKeyPressed(e -> {
      if (e.getCode().equals(KeyCode.ESCAPE)) {
        exitGame(e);
      }
    });
  }

  /**
   * Handles when the Multiplayer button is triggerred
   *
   * @param event Event triggered
   */
  private void startLobby(ActionEvent event) {
    Multimedia.playAudio(Multimedia.clickEffect);
    Multimedia.audio.setOnEndOfMedia(gameWindow::startLobby);
  }

  /**
   * Initialise the menu
   */
  @Override
  public void initialise() {
    logger.info("Initialising Menu Scene...");
    Multimedia.playBackGroundMusic(Multimedia.menuMusic);
  }

  /**
   * Handle when the Start Game button is triggerred.
   *
   * @param event event triggerred
   */
  private void startGame(Event event) {
    Multimedia.stopMusic();
    Multimedia.playAudio(Multimedia.clickEffect);
    Multimedia.audio.setOnEndOfMedia(gameWindow::startChallenge);
  }

  /**
   * Handle when the "How To Play" Game button is triggerred.
   *
   * @param event event triggerred
   */
  private void startInstructions(MouseEvent event) {
    Multimedia.stopMusic();
    Multimedia.playAudio(Multimedia.clickEffect);
    Multimedia.audio.setOnEndOfMedia(gameWindow::startInstructions);
  }

  /**
   * Handles when exit is requested.
   *
   * @param event Event triggerred
   */
  private void exitGame(Event event) {
    Multimedia.playAudio(Multimedia.exitEffect);
    Multimedia.audio.setOnEndOfMedia(() -> App.getInstance().shutdown());
  }

}
