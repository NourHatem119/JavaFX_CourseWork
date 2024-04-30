package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
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

/**
 * The Multiplayer scene. Holds the UI for the MultiPlayer mode in the game.
 */
public class MultiPlayerScene extends ChallengeScene {

  private static final Logger logger = LogManager.getLogger(MultiPlayerScene.class);
  Communicator communicator;
  ObservableList<Pair<String, Integer>> currentPlayers;
  ArrayList<Pair<String, Integer>> players = new ArrayList<>();
  ArrayList<String> deadPlayers = new ArrayList<>();

  /**
   * A Custom Component holding all the scores in the current Multiplayer Game.
   */
  ScoresList currentScores;

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

  /**
   * Initialises the MultiPlayer Game Scene.
   */
  @Override
  public void initialise() {
    super.initialise();
    communicator.addListener(message ->
        Platform.runLater(() -> {
          if (message.startsWith("SCORES")) {
            handleScores(message);
          }
        }));
    getCurrentScores();
    game.scoreProperty().addListener((observable, oldValue, newValue) -> {
      getCurrentScores();
      currentScores.reveal(deadPlayers);
    });
  }

  /**
   * Builds the Multiplayer Game's sideBar.
   *
   * @return the Multiplayer Game's sideBar
   */
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

  /**
   * Adds the given Player to the list of eliminated Players in the current Game.
   *
   * @param player player to eliminate
   */
  public void eliminate(String player) {
    deadPlayers.add(player);
  }

  /**
   * Set up the MultiPlayerGame object and model.
   */
  @Override
  public void setupGame() {
    logger.info("Starting Game");
    game = new MultiplayerGame(5, 5, communicator);
  }

  /**
   * Request the current scores of the game from the server.
   */
  private void getCurrentScores() {
    communicator.send("SCORES");
  }

  /**
   * Handles receiving the list of scores from the server, clears the current list and adds the new
   * List.
   *
   * @param s the message received from the server
   */
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
            eliminate(playerInfo[0]);
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


  /**
   * Calls the challenge scene's gameOver and cancels the timer that requests the scores.
   *
   * @param currentGame the game that has been played
   */
  @Override
  protected void gameOver(Game currentGame) {
    super.gameOver(currentGame);
  }

  /**
   * Handles when a key is clicked, the same as the challenge scene but cancels the timer when Exit
   * is requested.
   *
   * @param keyClicked the key that has been clicked
   */
  @Override
  protected void keyClicked(KeyEvent keyClicked) {
    super.keyClicked(keyClicked);
  }
}
