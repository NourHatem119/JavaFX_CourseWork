package uk.ac.soton.comp1206.scene;

import java.util.Objects;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
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
   * Sets up the image to be displayed in the menu scene with transitions.
   *
   * @return the image that has been initialised
   */
  ImageView initialiseImage() {
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
    return titleView;
  }

  /**
   * Build the Box that contains all the buttons in the menu scene.
   *
   * @return the Box containing all the buttons
   */
  VBox buildButtonsBox() {
    var play = new Button("Play");
    var multiplayer = new Button("Multiplayer");
    var instructions = new Button("How To Play");
    var settings = new Button("Settings");
    var exit = new Button("Exit");
    var buttons = new VBox(play, multiplayer, instructions, settings, exit);
    for (Node button : buttons.getChildren()) {
      button.getStyleClass().add("menuItem");
    }
    buttons.setAlignment(Pos.CENTER);
    return buttons;
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

    ImageView titleView = initialiseImage();
    mainPane.setCenter(titleView);
    BorderPane.setAlignment(titleView, Pos.BOTTOM_CENTER);

    var buttons = buildButtonsBox();
    mainPane.setBottom(buttons);

    //Bind the button action to the startGame method in the menu
    buttons.getChildren().get(0).setOnMouseClicked(this::startGame);
    buttons.getChildren().get(0).setOnKeyPressed(e -> {
      if (e.getCode().equals(KeyCode.ENTER)) {
        startGame(e);
      }
    });
    buttons.getChildren().get(1).setOnMouseClicked(this::startLobby);
    buttons.getChildren().get(2).setOnMouseClicked(this::startInstructions);
    buttons.getChildren().get(3).setOnMouseClicked(this::startSettings);
    buttons.getChildren().get(4).setOnMouseClicked(this::exitGame);
    mainPane.setOnKeyPressed(e -> {
      if (e.getCode().equals(KeyCode.ESCAPE)) {
        exitGame(e);
      }
    });
    for (Node button : buttons.getChildren()) {
      button.hoverProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue) {
          button.getStyleClass().add("menuItem:hover");
        } else {
          button.getStyleClass().remove("menuItem:hover");
        }
      });
    }
  }

  /**
   * Handles when the Multiplayer button is triggerred.
   *
   * @param event Event triggered
   */
  private void startLobby(Event event) {
    Multimedia.playAudio(Multimedia.clickEffect);
    Multimedia.audio.setOnEndOfMedia(gameWindow::startLobby);
  }

  /**
   * Initialises the menu.
   */
  @Override
  public void initialise() {
    logger.info("Initialising Menu Scene...");
    Multimedia.playBackGroundMusic(Multimedia.menuMusic);
  }

  /**
   * Start a Game.
   *
   * @param event event triggerred
   */
  private void startGame(Event event) {
    Multimedia.stopMusic();
    Multimedia.playAudio(Multimedia.clickEffect);
    Multimedia.audio.setOnEndOfMedia(gameWindow::startChallenge);
  }

  /**
   * Opens the instructions window.
   *
   * @param event event triggerred
   */
  private void startInstructions(MouseEvent event) {
    Multimedia.stopMusic();
    Multimedia.playAudio(Multimedia.clickEffect);
    Multimedia.audio.setOnEndOfMedia(gameWindow::startInstructions);
  }

  /**
   * Opens the settings.
   *
   * @param event event triggerred
   */
  private void startSettings(MouseEvent event) {
    Multimedia.stopMusic();
    Multimedia.playAudio(Multimedia.clickEffect);
    Multimedia.audio.setOnEndOfMedia(gameWindow::startSettings);
  }

  /**
   * Exits TetrECS Game.
   *
   * @param event Event triggerred
   */
  private void exitGame(Event event) {
    Multimedia.playAudio(Multimedia.exitEffect);
    Multimedia.audio.setOnEndOfMedia(() -> App.getInstance().shutdown());
  }

}
