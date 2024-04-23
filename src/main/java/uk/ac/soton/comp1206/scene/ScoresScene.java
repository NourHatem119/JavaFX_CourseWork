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
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
      + "P_II\\Coursework\\coursework\\scores.txt");

  ArrayList<Pair<String, Integer>> scores = loadScores(scoresFile);

  public SimpleListProperty<Pair<String, Integer>> localScores =
      new SimpleListProperty<>(FXCollections.observableArrayList(scores));

  private ObservableList<Pair<String, Integer>> remoteScores;
  ArrayList<Pair<String, Integer>> remoteScoresList = new ArrayList<>();

  Game game;

  ScoresList localScoresList;
  ScoresList currentGameList;
  ScoresList onlineScoresList;

  int newScore;

  public ScoresScene(GameWindow window, Game game, ScoresList currentScores) {
    super(window);
    this.game = game;
    newScore = game.getScore();
    communicator = window.getCommunicator();
    currentGameList = currentScores;
  }

  private static final Logger logger = LogManager.getLogger(ScoresScene.class);


  @Override
  public void initialise() {
    logger.info("Initialising the Scores Scene...");
    communicator.addListener(e -> Platform.runLater(() -> {
      if (e.startsWith("HISCORES")) {
        receiveHighScores(e);
      }
    }));
    loadOnlineScores();
    scene.setOnKeyPressed(this::keyClicked);
  }

  @Override
  public void build() {
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    var scoresPane = new StackPane();
    scoresPane.setMaxWidth(gameWindow.getWidth());
    scoresPane.setMaxHeight(gameWindow.getHeight());
    scoresPane.getStyleClass().add("menu-background");
    root.getChildren().add(scoresPane);

    localScoresList = new ScoresList();
    onlineScoresList = new ScoresList();
    localScoresList.listProperty().bind(localScores);
    remoteScores = FXCollections.observableArrayList(remoteScoresList);
    SimpleListProperty<Pair<String, Integer>> wrap = new SimpleListProperty<>(remoteScores);
    onlineScoresList.listProperty().bind(wrap);


    localScoresList.setAlignment(Pos.CENTER);
    onlineScoresList.setAlignment(Pos.CENTER);

    mainPane = new BorderPane();

    int highScoreIndexLocal = highScoreIndexLocal();

    VBox localOrGameScores = new VBox();
    localOrGameScores.setAlignment(Pos.CENTER);


    if (currentGameList == null) {
      localOrGameScores.getChildren().add(new Text("Local Scores"));
      localOrGameScores.getChildren().add(localScoresList);
      if (highScoreIndexLocal != -1) {
        VBox addLocalHighScore = getNewLocalHighScore(highScoreIndexLocal);
        mainPane.setCenter(addLocalHighScore);
        addLocalHighScore.getChildren().get(1).setOnMouseReleased(e -> {
          mainPane.getChildren().remove(addLocalHighScore);
        });
      }
    } else {
      localOrGameScores.getChildren().add(new Text("This Game"));
      localOrGameScores.getChildren().add(currentGameList);
    }


    VBox onlineScoresBox = new VBox();
    onlineScoresBox.getChildren().add(new Text("Online Scores"));
    onlineScoresBox.getChildren().add(onlineScoresList);
    onlineScoresBox.setAlignment(Pos.CENTER);
    localOrGameScores.getChildren().get(0).getStyleClass().add("title");
    onlineScoresBox.getChildren().get(0).getStyleClass().add("title");
    HBox allScores = new HBox();
    allScores.getChildren().addAll(localOrGameScores, onlineScoresBox);
    allScores.setAlignment(Pos.CENTER);
    allScores.setSpacing(20);
    VBox scoresContainer = new VBox();
    scoresContainer.setAlignment(Pos.CENTER);
    Text gameoverText = new Text("GAMEOVER");
    gameoverText.getStyleClass().add("bigtitle");
    var highScoresText = new Text("HIGHSCORES");
    highScoresText.getStyleClass().add("bigtitle");
    scoresContainer.getChildren().addAll(gameoverText, highScoresText, allScores);
    mainPane.setCenter(scoresContainer);
    scoresPane.getChildren().add(mainPane);
  }

  private VBox getNewLocalHighScore(int highScoreIndexLocal) {
    var nameForNewScore = new TextField();
    nameForNewScore.setPromptText("Enter Name");
    nameForNewScore.setPrefWidth(50);
    var submitLocalScore = new Button("Confirm");
    submitLocalScore.setOnMousePressed(e -> {
      if (localScores.size() < 9) {
        localScores.add(new Pair<>(nameForNewScore.getText(), newScore));
      } else {
        localScores.add(highScoreIndexLocal, new Pair<>(nameForNewScore.getText(),
            newScore));
        localScores.remove(localScores.size() - 1);
      }
      writeScores(new File("scores.txt"));
    });
    return new VBox(nameForNewScore, submitLocalScore);
  }

  private int highScoreIndexLocal() {
    for (int i = 0; i < localScores.getSize(); i++) {
      if (newScore > localScores.get(i).getValue()) {
        return i;
      }
    }
    return -1;
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


  public static Integer getHighScore() {
    ArrayList<Pair<String, Integer>> scores = loadScores(scoresFile);
    if (!scores.isEmpty()) {
      return scores.get(0).getValue();
    } else {
      return 10000;
    }
  }
}
