package uk.ac.soton.comp1206.ui;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.SimpleListProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.scene.ScoresScene;

public class ScoresList extends VBox {

  private static final Logger logger = LogManager.getLogger(GameWindow.class);


  SimpleListProperty<Pair<String,Integer>> scoresList = new SimpleListProperty<>();

  ScoresScene scoresScene;

  public ScoresList(ScoresScene scoresScene) {
    this.scoresScene = scoresScene;
    scoresList.bind(scoresScene.localScores);
  }

  public void reveal(BorderPane pane) {
    int index = 0;
    FadeTransition[] transitions = new FadeTransition[scoresList.getSize()];
    for (Pair<String, Integer> singleScore : scoresList) {
      logger.info("Item Added {} : {}", singleScore.getKey(), singleScore.getValue());
      var score = new Text(singleScore.getKey() + ":" + singleScore.getValue().toString());
      score.getStyleClass().add("scorelist");
      score.setFill(GameBlock.COLOURS[index]);
      FadeTransition transition = new FadeTransition(Duration.millis(250), score);
      transition.setFromValue(0.0);
      transition.setToValue(1.0);
      transitions[index] = transition;
      this.getChildren().addAll(score);
      index++;
    }
    SequentialTransition sequentialTransition = new SequentialTransition(transitions);
    pane.setCenter(this);
    sequentialTransition.play();
  }
}
