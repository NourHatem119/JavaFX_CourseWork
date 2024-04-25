package uk.ac.soton.comp1206.ui;

import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

/**
 * ScoresList is custom UI component that extends VBox, it has a List Property that get bound to
 * another List Property given, which allows it to get automatically updated whenever the given list
 * is updated
 */
public class ScoresList extends VBox {

  private static final Logger logger = LogManager.getLogger(ScoresList.class);
  protected SimpleListProperty<Pair<String, Integer>> scoresList;

  /**
   * Creates a new ScoresList and adds a listener to reveal whenever the list is changed
   */
  public ScoresList() {
    this.getStyleClass().add("scorelist");
    this.setSpacing(5);
    scoresList = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    scoresList.addListener((observable, oldValue, newValue) -> {
      reveal(null);
    });
  }

  /**
   * Animates the appearance of scores, and make an effect if in a multiplayer game and a player is
   * eliminated.
   *
   * @param deadPlayers dead players in a multiplayer game, passed as null if in a local game
   */
  public void reveal(ArrayList<String> deadPlayers) {
    getChildren().clear();
    int index = 0;
    FadeTransition[] transitions = new FadeTransition[scoresList.getSize()];
    for (Pair<String, Integer> singleScore : scoresList) {
      logger.info("Item Added {} : {}", singleScore.getKey(), singleScore.getValue());
      Text name = new Text(singleScore.getKey());
      Text scoreValue = new Text(" : " + singleScore.getValue().toString());
      name.getStyleClass().add("scoreName");
      scoreValue.getStyleClass().add("score");
      if (deadPlayers != null) {
        if (deadPlayers.contains(singleScore.getKey())) {
          name.setStrikethrough(true);
        }
      }
      var score = new HBox(name, scoreValue);
      FadeTransition transition = new FadeTransition(Duration.millis(250), score);
      transition.setFromValue(0.0);
      transition.setToValue(1.0);
      transitions[index] = transition;
      name.setFill(GameBlock.COLOURS[index + 1]);
      getChildren().add(score);
      index++;
    }

    if (scoresList.getSize() != 0) {
      SequentialTransition sequentialTransition = new SequentialTransition(transitions);
      sequentialTransition.play();
    }
  }

  public ListProperty<Pair<String, Integer>> listProperty() {
    return scoresList;
  }
}
