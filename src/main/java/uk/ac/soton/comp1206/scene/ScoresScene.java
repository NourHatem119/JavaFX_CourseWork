package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.ScoresList;

public class ScoresScene extends BaseScene {

  BorderPane mainPane = new BorderPane();
  Communicator communicator;

  static File scoresFile = new File("D:\\Uni\\"
      + "Programming_II\\Coursework\\coursework\\scores.txt");

  ArrayList<Pair<String, Integer>> scores = loadScores(scoresFile);

  public SimpleListProperty<Pair<String, Integer>> localScores =
      new SimpleListProperty<>(FXCollections.observableArrayList(scores));

  private ObservableList<Pair<String, Integer>> remoteScores;
  ArrayList<Pair<String, Integer>> remoteScoresList = new ArrayList<>();

  Game game;

  ScoresList scoresList;
  ScoresList onlineScoresList;

  int newScore;

  public ScoresScene(GameWindow window, Game game) {
    super(window);
    this.game = game;
    newScore = game.getScore();
    communicator = window.getCommunicator();
  }

  private static final Logger logger = LogManager.getLogger(ScoresScene.class);


  @Override
  public void initialise() {
    logger.info("Initialising the Scores Scene...");
    communicator.addListener(e -> {
      Platform.runLater(() -> receiveCommunication(e));
    });
    loadOnlineScores();
    scene.setOnKeyPressed(this::keyClicked);
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

    scoresList = new ScoresList(this, false);
    onlineScoresList = new ScoresList(this, true);
    remoteScores = FXCollections.observableArrayList(remoteScoresList);
    SimpleListProperty<Pair<String, Integer>> wrap = new SimpleListProperty<>(remoteScores);
    onlineScoresList.listProperty().bind(wrap);

    scoresList.setAlignment(Pos.CENTER);
    onlineScoresList.setAlignment(Pos.CENTER);

    mainPane = new BorderPane();

    var nameForNewScore = new TextField();
    var submitLocalScore = new Button("Confirm");
    var submitRemoteScore = new Button("Confirm");
    HBox addLocalHighScore = new HBox(nameForNewScore, submitLocalScore);
    HBox addRemoteHighScore = new HBox(nameForNewScore, submitRemoteScore);

    int highScoreLocal = highScoreLocal();
    int highScoreRemote = highScoreRemote();
    submitLocalScore.setOnAction(e -> {
      localScores.set(highScoreLocal, new Pair<>(nameForNewScore.getText(), newScore));
      scoresList.reveal();
      writeScores(new File("scores.txt"));
      new FadeTransition(Duration.millis(10), nameForNewScore).play();
      new FadeTransition(Duration.millis(10), submitLocalScore).play();
    });
    submitRemoteScore.setOnAction(e -> {
      writeOnlineScore(nameForNewScore.getText(), newScore);
      onlineScoresList.reveal();
      new FadeTransition(Duration.millis(10), nameForNewScore).play();
      new FadeTransition(Duration.millis(10), submitRemoteScore).play();
    });
    if (highScoreLocal != -1) {
      mainPane.setBottom(addLocalHighScore);
    }

    if (highScoreRemote != -1) {
      mainPane.setBottom(addRemoteHighScore);
    }

    var title = new Text("HIGHSCORES");
    title.getStyleClass().add("bigtitle");
    mainPane.setTop(title);
    mainPane.setLeft(scoresList);
    mainPane.setRight(onlineScoresList);
    scoresPane.getChildren().add(mainPane);
  }

  private int highScoreLocal() {
    for (int i = 0; i < localScores.getSize(); i++) {
      if (newScore > localScores.get(i).getValue()) {
        return i;
      }
    }
    return -1;
  }


  private int highScoreRemote() {
    for (int i = 0; i < remoteScores.size(); i++) {
      if (newScore > remoteScores.get(i).getValue()) {
        return i;
      }
    }
    return -1;
  }


  private static ArrayList<Pair<String, Integer>> loadScores(File scoresFile) {
    BufferedReader reader;
    ArrayList<Pair<String, Integer>> scores = new ArrayList<>();
    try {
      FileReader fileReader = new FileReader(scoresFile);
      reader = new BufferedReader(fileReader);
      String line;
      while ((line = reader.readLine()) != null) {
        String[] score = line.split(":");
        scores.add(new Pair<>(score[0],
            Integer.parseInt(score[1])));
      }
      scores.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    } catch (FileNotFoundException e) {
      logger.info("File Not Found...");
    } catch (IOException e) {
      logger.info("IO Exception...");
    }
    return scores;
  }

  private void loadOnlineScores() {
    communicator.send("HISCORES");
  }

  private void receiveCommunication(String s) {
    String[] message = s.split(" ", 2);
    String receivedScores = "";
    if (message[0].equals("HISCORES")) {
      if (message[1].length() > 1) {
        receivedScores = message[1];
      }
    }

    String[] scores = receivedScores.split("\n");
    for (String sc : scores) {
      String[] score = sc.split(":");
      remoteScoresList.add(new Pair<>(score[0], Integer.parseInt(score[1])));
    }
    remoteScoresList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    remoteScores.clear();
    remoteScores.addAll(remoteScoresList);

    this.scoresList.reveal();
    this.onlineScoresList.reveal();
  }

  private void writeScores(File file) {
    BufferedWriter writer;

    try {
      FileWriter fileWriter = new FileWriter(file);
      writer = new BufferedWriter(fileWriter);
      for (Pair<String, Integer> pair : localScores) {
        writer.write(pair.getKey() + ":" + pair.getValue().toString());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeOnlineScore(String nameForNewScore, int newScore) {
    communicator.send("HISCORE " + nameForNewScore + ":" + newScore);
  }


  public static Integer getHighScore() {
    return (loadScores(scoresFile)).get(0).getValue();
  }
}
