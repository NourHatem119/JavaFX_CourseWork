package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
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

  public ScoresList getScores() {
    return currentScores;
  }

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public MultiPlayerScene(GameWindow gameWindow) {
    super(gameWindow);
    communicator = gameWindow.getCommunicator();

  }

  @Override
  public void initialise() {
//    multimedia.playBackGroundMusic(music);
    scene.setOnKeyPressed(this::keyClicked);
    game.start();
    communicator.addListener(message ->
        Platform.runLater(() -> {
          if (message.startsWith("SCORES")) {
            handleScores(message);
          }
        }));
    getCurrentScores();
  }

  @Override
  protected VBox buildSideBar() {
//    return super.buildSideBar();

    currentScores = new ScoresList();
    currentPlayers = FXCollections.observableArrayList(players);
    SimpleListProperty<Pair<String, Integer>> wrap = new SimpleListProperty<>(currentPlayers);
    currentScores.listProperty().bind(wrap);
    var rightPanel = new VBox();
    currentPieceShow.setIsCurrentPiece(true);
    rightPanel.setAlignment(Pos.CENTER);
    rightPanel.setSpacing(15);
    rightPanel.getChildren().addAll(currentScores, currentPieceShow, nextPieceShow);
    return rightPanel;
  }

//  @Override
//  public void build() {
//    logger.info("Building {}", this.getClass().getName());
//
//    setupGame();
//
//    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
//
//    var multiplayerChallengePane = new StackPane();
//    multiplayerChallengePane.setMaxWidth(gameWindow.getWidth());
//    multiplayerChallengePane.setMaxHeight(gameWindow.getHeight());
//    multiplayerChallengePane.getStyleClass().add("challenge-background");
//    root.getChildren().add(multiplayerChallengePane);
//
//
//
//    logger.info("Width: {}, Height: {}",gameWindow.getWidth() / 2, gameWindow.getHeight() / 2);
//    board = new GameBoard(game.getGrid(),gameWindow.getWidth() / 2,
//        gameWindow.getHeight() / 2);
//
//
//    var mainPane = new BorderPane();
//
//    multiplayerChallengePane.getChildren().add(mainPane);
//    mainPane.setRight(buildSideBar());
//    mainPane.setBottom(buildBottomBar());
//    mainPane.setTop(buildTopBar("Multiplayer Match"));
//    mainPane.setCenter(board);
//
//    board.setOnBlockClick(this::blockClicked);
//    currentPieceShow.setOnPieceClick(this::pieceClicked);
//    nextPieceShow.setOnPieceClick(this::pieceClicked);
//    game.setOnNextPiece(this::nextPiece);
//    board.setOnRightClicked(this::rightClicked);
//    game.setOnLineCleared(this::lineCleared);
//    game.setGameLoop(this::gameLoop);
//    game.setOnGameOver(this::gameOver);
//
//  }

  public void kill(String player) {
    deadPlayers.add(player);
  }


  @Override
  public void setupGame() {
    logger.info("Starting Game");
    game = new MultiplayerGame(5, 5, communicator);
  }

  @Override
  protected void gameLoop(int i) {
    super.gameLoop(i);
  }

  @Override
  protected void rightClicked(MouseEvent event) {
    super.rightClicked(event);
  }

  @Override
  protected void blockClicked(GameBlock gameBlock, MouseEvent event) {
    super.blockClicked(gameBlock, event);
  }

  @Override
  protected void pieceClicked(GameBlock block, MouseEvent event) {
    super.pieceClicked(block, event);
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

//    logger.info("Length {}", players[0].split(":").length);
    for (String player : playersUpdate) {
      String[] playerInfo = player.split(":");

      boolean found = false;
      for (int j = 0; j < players.size(); j++) {
        Pair<String, Integer> thePlayer = players.get(j);
        if (thePlayer.getKey().equals(playerInfo[0])) {
          if (playerInfo[2].equals("DEAD") && !deadPlayers.contains(playerInfo[0])) {
            kill(playerInfo[0]);
            currentScores.getChildren().get(j).getStyleClass().add("eliminated");
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
    this.currentScores.reveal();
  }
}
