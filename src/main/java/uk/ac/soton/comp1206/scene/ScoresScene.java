package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.ScoresList;
import uk.ac.soton.comp1206.ux.Multimedia;

public class ScoresScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(ScoresScene.class);
  static File scoresFile = new File("D:\\Uni\\"
      + "P_II\\Coursework\\coursework\\scores.txt");
  BorderPane mainPane = new BorderPane();
  Communicator communicator;
  ArrayList<Pair<String, Integer>> scores = loadScores(scoresFile);
  public SimpleListProperty<Pair<String, Integer>> localScores =
      new SimpleListProperty<>(FXCollections.observableArrayList(scores));
  ArrayList<Pair<String, Integer>> remoteScoresList = new ArrayList<>();

  Game game;

  ScoresList localScoresList;
  ScoresList currentGameList;
  ScoresList onlineScoresList;

  int newScore;
  private ObservableList<Pair<String, Integer>> remoteScores;

  public ScoresScene(GameWindow window, Game game, ScoresList currentScores) {
    super(window);
    this.game = game;
    newScore = game.getScore();
    communicator = window.getCommunicator();
    currentGameList = currentScores;

//    Multimedia.playAudio(Multimedia.challengeMusic);
  }

  private static ArrayList<Pair<String, Integer>> loadScores(File file) {
    if (!file.exists()) {
      try {
        boolean created = file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < 10; i++) {
          writer.write("NourEldin:" + i * 1000 + "\n");
        }
        writer.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    ArrayList<Pair<String, Integer>> scores = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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

  public static Integer getHighScore() {
    ArrayList<Pair<String, Integer>> scores = loadScores(scoresFile);
    if (!scores.isEmpty()) {
      return scores.get(0).getValue();
    } else {
      return 10000;
    }
  }

  @Override
  public void initialise() {
    logger.info("Initialising the Scores Scene...");
    Multimedia.playBackGroundMusic(Multimedia.challengeMusic);
    communicator.addListener(e -> Platform.runLater(() -> {
      if (e.startsWith("HISCORES")) {
        receiveHighScores(e);
      }
    }));
    loadOnlineScores();
    scene.setOnKeyPressed(this::keyClicked);
  }

  /**
   * Generates a VBox depending on the type given
   *
   * @param type type of the score box to be generated
   * @return the score box generated
   */
  VBox buildScoreBox(String type) {
    VBox scoreBox = new VBox();
    scoreBox.setAlignment(Pos.TOP_CENTER);

    switch (type) {
      case "Online Scores" -> {
        onlineScoresList = new ScoresList();
        remoteScores = FXCollections.observableArrayList(remoteScoresList);
        SimpleListProperty<Pair<String, Integer>> wrap = new SimpleListProperty<>(remoteScores);
        onlineScoresList.listProperty().bind(wrap);
        onlineScoresList.setAlignment(Pos.CENTER);

        scoreBox.getChildren().add(new Text(type));
        scoreBox.getChildren().add(onlineScoresList);
      }
      case "Local Scores" -> {
        localScoresList = new ScoresList();
        localScoresList.listProperty().bind(localScores);
        localScoresList.setAlignment(Pos.CENTER);

        scoreBox.getChildren().add(new Text(type));
        scoreBox.getChildren().add(localScoresList);
      }
      case "This Game" -> {
        scoreBox.getChildren().add(new Text(type));
        scoreBox.getChildren().add(currentGameList);
        currentGameList.setAlignment(Pos.CENTER);
      }
    }

    scoreBox.getChildren().get(0).getStyleClass().add("title");
    return scoreBox;
  }

  private VBox buildHighScoreAdding(int highScoreIndexLocal, VBox leaderBoard) {
    var nameForNewScore = new TextField();
    nameForNewScore.setPromptText("Enter Name");
    nameForNewScore.setPrefWidth(50);
    var submitLocalScore = new Button("Confirm");
    submitLocalScore.getStyleClass().add("submitButton");
    submitLocalScore.setOnMouseClicked(e -> {
      if (localScores.size() < 9) {
        localScores.add(new Pair<>(nameForNewScore.getText(), newScore));
      } else {
        localScores.add(highScoreIndexLocal, new Pair<>(nameForNewScore.getText(),
            newScore));
        localScores.remove(localScores.size() - 1);
      }
      mainPane.setCenter(leaderBoard);
      writeScores(new File("scores.txt"));
    });

    return new VBox(nameForNewScore, submitLocalScore);
  }

  /**
   * generates an HBox with all the High Score Components
   *
   * @return An HBox containing all the scoring stuff
   */
  HBox buildScoresBox() {

    VBox localOrGameScores;
    if (currentGameList == null) {
      localOrGameScores = buildScoreBox("Local Scores");
    } else {
      localOrGameScores = buildScoreBox("This Game");
    }

    VBox onlineScoresBox = buildScoreBox("Online Scores");
    HBox allScores = new HBox();
    allScores.getChildren().addAll(localOrGameScores, onlineScoresBox);
    allScores.setAlignment(Pos.CENTER);
    allScores.setSpacing(20);
    return allScores;
  }

  @Override
  public void build() {
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    var scoresPane = new StackPane();
    scoresPane.setMaxWidth(gameWindow.getWidth());
    scoresPane.setMaxHeight(gameWindow.getHeight());
    scoresPane.getStyleClass().add("scores-background");
    root.getChildren().add(scoresPane);

    mainPane = new BorderPane();

    int highScoreIndexLocal = highScoreIndexLocal();

    var scoresContainer = new VBox();
    scoresContainer.setAlignment(Pos.CENTER);

    var highScoresText = new Text("HIGHSCORES");
    highScoresText.getStyleClass().add("bigtitle");

    scoresContainer.getChildren().addAll(highScoresText, buildScoresBox());

    if (currentGameList == null) {
      if (highScoreIndexLocal != -1) {
        VBox addLocalHighScore = buildHighScoreAdding(highScoreIndexLocal, scoresContainer);
        addLocalHighScore.setVisible(true);

        mainPane.setCenter(addLocalHighScore);

      }
    } else {
      mainPane.setCenter(scoresContainer);
    }

    scoresPane.getChildren().add(mainPane);
  }

  private int highScoreIndexLocal() {
    for (int i = 0; i < localScores.getSize(); i++) {
      if (newScore > localScores.get(i).getValue()) {
        return i;
      }
    }
    return -1;
  }

  private void loadOnlineScores() {
    communicator.send("HISCORES");
  }

  private void receiveHighScores(String s) {
    String[] message = s.split(" ", 2);
    String receivedScores = "";
    if (message[1].length() > 1) {
      receivedScores = message[1];
    }

    String[] scores = receivedScores.split("\n");
    for (String sc : scores) {
      String[] score = sc.split(":");
      remoteScoresList.add(new Pair<>(score[0], Integer.parseInt(score[1])));
    }
    remoteScoresList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    remoteScores.clear();
    remoteScores.addAll(remoteScoresList);
  }

  private void writeScores(File file) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      for (Pair<String, Integer> pair : localScores) {
        writer.write(pair.getKey() + ":" + pair.getValue().toString() + "\n");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeOnlineScore(String nameForNewScore, int newScore) {
    communicator.send("HISCORE " + nameForNewScore + ":" + newScore);
  }
}
