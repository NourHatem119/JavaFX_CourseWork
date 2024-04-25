package uk.ac.soton.comp1206.ui;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.scene.ScoresScene;

public class ScoresList extends VBox {

  private static final Logger logger = LogManager.getLogger(ScoresList.class);

  public ScoresList() {
    this.getStyleClass().add("scorelist");
    this.setSpacing(5);

    scoresList = new SimpleListProperty<>(FXCollections.observableArrayList());
    scoresList.addListener( (observable, oldValue, newValue) -> {
      reveal();
    });
  }


  protected SimpleListProperty<Pair<String, Integer>> scoresList;

  public void reveal() {
    getChildren().clear();
    int index = 0;
    FadeTransition[] transitions = new FadeTransition[scoresList.getSize()];
    for (Pair<String, Integer> singleScore : scoresList) {
      logger.info("Item Added {} : {}", singleScore.getKey(), singleScore.getValue());
      var score = new Text(singleScore.getKey() + ":" + singleScore.getValue().toString());
      score.getStyleClass().add("scoreitem");
      FadeTransition transition = new FadeTransition(Duration.millis(250), score);
      transition.setFromValue(0.0);
      transition.setToValue(1.0);
      transitions[index] = transition;
      score.setFill(Color.BLACK);
      getChildren().add(score);
      index++;
    }

    if (scoresList.getSize() != 0){
      SequentialTransition sequentialTransition = new SequentialTransition(transitions);
      sequentialTransition.play();
    }
  }

  public ListProperty<Pair<String, Integer>> listProperty() {
    return scoresList;
  }
}
