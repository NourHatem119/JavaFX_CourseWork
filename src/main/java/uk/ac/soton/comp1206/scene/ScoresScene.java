package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoresScene extends BaseScene {

  BorderPane mainPane = new BorderPane();

  Game game;

  int newScore;

  public ScoresScene(GameWindow window, Game game) {
    super(window);
    this.game = game;
    newScore = game.getScore();
  }

  private static final Logger logger = LogManager.getLogger(MenuScene.class);


  @Override
  public void initialise() {
    logger.info("Initialising the Scores Scene...");

  }

  @Override
  public void build() {
    //Setting Up the scores scene
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    var scoresPane = new StackPane();
    scoresPane.setMaxWidth(gameWindow.getWidth());
    scoresPane.setMaxHeight(gameWindow.getHeight());
    scoresPane.getStyleClass().add("menu-background");
    root.getChildren().add(scoresPane);

    mainPane = new BorderPane();
    scoresPane.getChildren().add(mainPane);
  }

  private void reveal() {
  }

  private ArrayList<Pair<SimpleStringProperty, SimpleIntegerProperty>> loadScores(File scoresFile) {
    BufferedReader reader;
    ArrayList<Pair<SimpleStringProperty, SimpleIntegerProperty>> scores =
        new ArrayList<>();
    try {
      FileReader fileReader = new FileReader(scoresFile);
      reader = new BufferedReader(fileReader);
      String line;
//      int index = 0;
      while ((line = reader.readLine()) != null) {
        String[] score = line.split(":");
        scores.add(new Pair<>(new SimpleStringProperty(score[0]),
            new SimpleIntegerProperty(Integer.parseInt(score[1]))));
//        index ++;
      }
    } catch (FileNotFoundException e) {
      logger.info("File Not Found...");
    } catch (IOException e) {
      logger.info("IO Exception...");
    }
    return scores;
  }
}
