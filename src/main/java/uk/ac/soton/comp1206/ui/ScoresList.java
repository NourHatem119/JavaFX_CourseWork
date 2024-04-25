package uk.ac.soton.comp1206.ui;

import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoresList extends VBox {

  private static final Logger logger = LogManager.getLogger(ScoresList.class);
  protected SimpleListProperty<Pair<String, Integer>> scoresList;


  public ScoresList() {
    this.getStyleClass().add("scorelist");
    this.setSpacing(5);

    scoresList = new SimpleListProperty<>(FXCollections.observableArrayList());
    scoresList.addListener((observable, oldValue, newValue) -> {
      reveal(null);
    });
  }

  public void reveal(ArrayList<String> deadPlayers) {
    getChildren().clear();
    int index = 0;
    FadeTransition[] transitions = new FadeTransition[scoresList.getSize()];
    for (Pair<String, Integer> singleScore : scoresList) {
      logger.info("Item Added {} : {}", singleScore.getKey(), singleScore.getValue());
      Text name = new Text(singleScore.getKey());
      Text scoreValue = new Text(": " + singleScore.getValue().toString());
      if (deadPlayers != null) {
        if (deadPlayers.contains(singleScore.getKey())) {
          name.setStrikethrough(true);
        }
      }
      var score = new HBox(name, scoreValue);
      score.getStyleClass().add("scoreitem");
      FadeTransition transition = new FadeTransition(Duration.millis(250), score);
      transition.setFromValue(0.0);
      transition.setToValue(1.0);
      transitions[index] = transition;
      scoreValue.setFill(Color.YELLOW);
      name.setFill(Color.YELLOW);
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
