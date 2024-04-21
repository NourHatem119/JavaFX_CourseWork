package uk.ac.soton.comp1206.ui;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.scene.ScoresScene;

public class ScoresList extends VBox {

  private static final Logger logger = LogManager.getLogger(ScoresList.class);


  protected SimpleListProperty<Pair<String, Integer>> scoresList = new SimpleListProperty<>();
  Text identifier = new Text("Online");

  public ScoresList(boolean online) {
    if (!online) {
      identifier.setText("Local");
    }
  }

  public ScoresList() {

  }

  public void reveal() {
    getChildren().clear();
    identifier.getStyleClass().add("title");
    getChildren().add(identifier);
    int index = 0;
    FadeTransition[] transitions = new FadeTransition[scoresList.getSize()];
    for (Pair<String, Integer> singleScore : scoresList) {
      logger.info("Item Added {} : {}", singleScore.getKey(), singleScore.getValue());
      var score = new Text(singleScore.getKey() + ":" + singleScore.getValue().toString());
      score.getStyleClass().add("score");
      FadeTransition transition = new FadeTransition(Duration.millis(250), score);
      transition.setFromValue(0.0);
      transition.setToValue(1.0);
      transitions[index] = transition;
      score.setFill(GameBlock.COLOURS[index + 1]);
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
