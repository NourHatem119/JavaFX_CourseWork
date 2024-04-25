package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.ScoresList;

public class MultiPlayerScene extends ChallengeScene {

  private static final Logger logger = LogManager.getLogger(MultiPlayerScene.class);
  Communicator communicator;
  ObservableList<Pair<String, Integer>> currentPlayers;
  ArrayList<Pair<String, Integer>> players = new ArrayList<>();
  ArrayList<String> deadPlayers = new ArrayList<>();

  ScoresList currentScores;

  Timer requestScores;

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public MultiPlayerScene(GameWindow gameWindow) {
    super(gameWindow);
    communicator = gameWindow.getCommunicator();
  }

  public ScoresList getScores() {
    return currentScores;
  }

  @Override
  public void initialise() {
    super.initialise();
    communicator.addListener(message ->
        Platform.runLater(() -> {
          if (message.startsWith("SCORES")) {
            handleScores(message);
          }
        }));
    requestScores();
  }

  @Override
  protected VBox buildSideBar() {

    currentScores = new ScoresList();
    currentPlayers = FXCollections.observableArrayList(players);
    SimpleListProperty<Pair<String, Integer>> wrap = new SimpleListProperty<>(currentPlayers);
    currentScores.listProperty().bind(wrap);
    var rightPanel = new VBox();
    currentPieceShow.setIsCurrentPiece(true);
    rightPanel.setAlignment(Pos.CENTER);
    rightPanel.setSpacing(15);
    Text versusText = new Text("Versus");
    versusText.getStyleClass().add("title");
    VBox versusBox = new VBox(versusText, currentScores);
    currentScores.setAlignment(Pos.CENTER);
    versusBox.setAlignment(Pos.CENTER);
    rightPanel.getChildren().addAll(versusBox, currentPieceShow, nextPieceShow);
    return rightPanel;
  }


  public void kill(String player) {
    deadPlayers.add(player);
  }


  @Override
  public void setupGame() {
    logger.info("Starting Game");
    game = new MultiplayerGame(5, 5, communicator);
  }

  private void getCurrentScores() {
    communicator.send("SCORES");
  }

  private void handleScores(String s) {
    String[] message = s.split(" ", 2);
    String receivedScores = "";
    if (message[0].equals("SCORES")) {
      if (message[1].length() > 1) {
        receivedScores = message[1];
        logger.info("message: {}", receivedScores);
      }
    }

    String[] playersUpdate = receivedScores.split("\\n");
    logger.info("Players {}", playersUpdate);

    for (String player : playersUpdate) {
      String[] playerInfo = player.split(":");

      boolean found = false;
      for (int j = 0; j < players.size(); j++) {
        Pair<String, Integer> thePlayer = players.get(j);
        if (thePlayer.getKey().equals(playerInfo[0])) {
          if (playerInfo[2].contains("DEAD") && !deadPlayers.contains(playerInfo[0])) {
            logger.info("killing player");
            kill(playerInfo[0]);
          } else {
            players.set(j, new Pair<>(playerInfo[0], Integer.parseInt(playerInfo[1])));
          }
          found = true;
          break;
        }
      }
      if (!found) {
        players.add(new Pair<>(playerInfo[0], Integer.parseInt(playerInfo[1])));
      }

    }
    this.players.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    currentPlayers.clear();
    currentPlayers.addAll(this.players);
    logger.info("Players Size:{}", this.players.size());
    this.currentScores.reveal(deadPlayers);
  }

  private void requestScores() {
    requestScores = new Timer();
    TimerTask requestChannels = new TimerTask() {
      @Override
      public void run() {
        getCurrentScores();
      }
    };
    requestScores.schedule(requestChannels, 0, 5000);
  }

  @Override
  protected void gameOver(Game currentGame) {
    super.gameOver(currentGame);
    requestScores.cancel();
    requestScores.purge();
  }

  @Override
  protected void keyClicked(KeyEvent keyClicked) {
    super.keyClicked(keyClicked);
    if (keyClicked.getCode().equals(KeyCode.ESCAPE)) {
      requestScores.cancel();
      requestScores.purge();
    }
  }
}
