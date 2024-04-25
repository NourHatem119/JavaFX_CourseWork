package uk.ac.soton.comp1206.scene;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ux.Multimedia;


/**
 * SettingsScene is a scene that handles increasing and decreasing the volume.
 */
public class SettingsScene extends BaseScene {


  /**
   * Create a new Settings Scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public SettingsScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  /**
   * Initialise the settings Scene.
   */
  @Override
  public void initialise() {
    scene.setOnKeyPressed(this::keyClicked);
    Multimedia.playBackGroundMusic(Multimedia.settingsMusic);
  }

  /**
   * Build the Settings Scene.
   */
  @Override
  public void build() {
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    var settingsPane = new StackPane();
    settingsPane.setMaxWidth(gameWindow.getWidth());
    settingsPane.setMaxHeight(gameWindow.getHeight());
    settingsPane.getStyleClass().add("settings");
    root.getChildren().add(settingsPane);

    var mainPane = new BorderPane();

    Slider slider = new Slider(0, 100, Multimedia.getVolume());
    Text sliderTitle = new Text("Volume: ");
    sliderTitle.getStyleClass().add("volume");

    HBox sliderBox = new HBox(sliderTitle, slider);
    sliderBox.setAlignment(Pos.CENTER);

    slider.valueProperty().addListener(new ChangeListener<Number>() {
      /**
       * Listen to the changes in the slider's value, and set the volume according to this value.
       *
       * @param observable the value that is being listened to
       * @param oldValue the slider's value before changing
       * @param newValue the slider's value after the change
       */
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue,
          Number newValue) {
        Multimedia.setVolume(Math.round(newValue.doubleValue() * 10) / 1000.0);
        System.out.println(newValue);
      }
    });

    mainPane.setCenter(sliderBox);

    settingsPane.getChildren().add(mainPane);
  }
}
