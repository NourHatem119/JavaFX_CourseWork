package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.ScoresList;

public class MultiPlayerScene extends ChallengeScene{
  private static final Logger logger = LogManager.getLogger(MultiPlayerScene.class);
  Communicator communicator;
  ObservableList<Pair<String, Integer>> currentPlayers;
  ArrayList<Pair<String, Integer>> players = new ArrayList<>();
  ArrayList<String> deadPlayers = new ArrayList<>();

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

  @Override
  public void initialise() {
    multimedia.playBackGroundMusic(music);
    scene.setOnKeyPressed(this::keyClicked);
    game.start();
    communicator.addListener(message ->
        Platform.runLater(() ->{
            if(message.startsWith("SCORES")) {
              handleScores(message);
            }
            }));
    getCurrentScores();
  }

  private void handleScores(String s) {
    String[] message = s.split(" ", 2);
    String receivedScores = "";
    if (message[0].equals("SCORES")) {
      if (message[1].length() > 1){
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
          if (playerInfo[2].equals("DEAD"))
            players.remove(j);
          else
            players.set(j, new Pair<>(playerInfo[0], Integer.parseInt(playerInfo[1])));
          found = true;
          break;
        }
      }
      if (!found)
        players.add(new Pair<>(playerInfo[0], Integer.parseInt(playerInfo[1])));

    }
    this.players.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    currentPlayers.clear();
    currentPlayers.addAll(this.players);
    logger.info("Players Size:{}",this.players.size());
    this.currentScores.reveal();
  }

  private void getCurrentScores() {
    communicator.send("SCORES");
  }

  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    setupGame();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var multiplayerChallengePane = new StackPane();
    multiplayerChallengePane.getStyleClass().add("challenge-background");
    root.getChildren().add(multiplayerChallengePane);

    currentScores = new ScoresList();
    currentPlayers = FXCollections.observableArrayList(players);
    SimpleListProperty<Pair<String, Integer>> wrap = new SimpleListProperty<>(currentPlayers);
    currentScores.listProperty().bind(wrap);
    var rightPanel = new VBox();
    rightPanel.setAlignment(Pos.CENTER);
    var score = new Label();
    score.setAlignment(Pos.TOP_LEFT);
    timeBar = new Rectangle();
    VBox bottomBar = new VBox(timeBar);
    timeBar.setFill(Color.LIMEGREEN);
    timeBar.setHeight(20.0);
    timeBar.setWidth(gameWindow.getWidth());
    board = new GameBoard(game.getGrid(),gameWindow.getWidth() / 2.0, gameWindow.getHeight() / 2.0);
    currentPieceShow.setIsCurrentPiece(true);
    rightPanel.getChildren().addAll(currentScores, currentPieceShow, nextPieceShow);

    var mainPane = new BorderPane();

    multiplayerChallengePane.getChildren().add(mainPane);
    mainPane.setLeft(score);
    mainPane.setRight(rightPanel);
    mainPane.setBottom(bottomBar);
    mainPane.setCenter(board);

    board.setOnBlockClick(this::blockClicked);
    currentPieceShow.setOnPieceClick(this::pieceClicked);
    nextPieceShow.setOnPieceClick(this::pieceClicked);
    game.setOnNextPiece(this::nextPiece);
    board.setOnRightClicked(this::rightClicked);
    game.setOnLineCleared(this::lineCleared);
    game.setGameLoop(this::gameLoop);
    game.setOnGameOver(this::gameOver);

  }

  public void kill(String player) {
    deadPlayers.add(player);
  }


  @Override
  public void setupGame() {
    logger.info("Starting Game");
    game = new MultiplayerGame(5,5, communicator);
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
}
