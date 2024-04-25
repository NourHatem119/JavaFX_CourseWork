package uk.ac.soton.comp1206.game;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;

/**
 * The MultiplayerGame class handles the main logic, state and properties of the TetrECS online
 * Multiplayer game.
 */
public class MultiplayerGame extends Game {

  private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

  /**
   * A Queue containing pieces that has been received from the server.
   */
  final Queue<GamePiece> pieces = new LinkedList<>();
  Communicator communicator;
  Timer timer;

  /**
   * Create a new Multiplayer game with the specified rows and columns. Creates a corresponding grid
   * model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public MultiplayerGame(int cols, int rows, Communicator communicator) {
    super(cols, rows);
    this.communicator = communicator;
    logger.info("Multiplayer Game Constructor {}", this);
    this.communicator.addListener(message -> Platform.runLater(() -> {
      logger.info("listening on pieces {}", message);
      if (message.startsWith("PIECE")) {
        handlePiece(message);
      }
    }));
    for (int i = 0; i < 5; i++) {
      communicator.send("PIECE");
    }
  }

  @Override
  public void initialiseGame() {
    Platform.runLater(spawnThread);
  }  /**
   * Called in initialise to fetch the first 2 pieces and show them thread safely because linked
   * lists are not thread safe.
   */
  Runnable spawnThread = new Runnable() {
    @Override
    public void run() {
      if (currentPiece == null) {
        currentPiece = spawnPiece();
      }
      if (currentPiece != null && nextPiece == null) {
        nextPiece = spawnPiece();
      }

      if (currentPiece == null || nextPiece == null) {
        Platform.runLater(spawnThread);
      } else if (nextPieceListener != null) {
        nextPieceListener.nextPiece(currentPiece, nextPiece);
      }
      logger.info("Current Piece {}, and nextPiece {} initialised successfully...",
          currentPiece, nextPiece);
    }
  };

  /**
   * Receives the message containing the requested piece from the server and creates a new piece
   * object with this message, and adds it synchronously.
   *
   * @param pieceNo
   */
  private void handlePiece(String pieceNo) {
    logger.info("Handle Piece {}", this);
    pieceNo = pieceNo.replace("PIECE ", "");
    GamePiece piece = GamePiece.createPiece(Integer.parseInt(pieceNo.trim()));
    synchronized (pieces) {
      pieces.add(piece);
    }

    logger.info("Piece Added ... {}/({})", piece.toString(), pieceNo + 1);
    String queueContents = "";
    for (GamePiece gamePiece : pieces) {
      queueContents = "Queue Contains [";
      queueContents += gamePiece.toString() + "(" + gamePiece.getValue() + ")";
    }
    logger.info(queueContents);
  }

  /**
   * Starts the Multiplayer Game.
   */
  @Override
  public void start() {
    Platform.runLater(this::initialiseGame);
    createTimer();
  }

  /**
   * Gets the next piece in the queue(Linked List), sends a message to the communicator to replace
   * it.
   *
   * @return the game piece received from the communicator
   */
  @Override
  public GamePiece spawnPiece() {
    communicator.send("PIECE");
    synchronized (pieces) {
      GamePiece piece = pieces.poll();
      if (piece != null) {
        logger.info("Spawned Piece  {}/({})", piece.toString(), piece.getValue());
      }
      return piece;
    }
  }

  /**
   * Replaces the current piece with the next piece, spawns a new piece, and updates the ui
   * accordingly.
   */
  @Override
  protected void nextPiece() {
    logger.info("Next piece loaded...");
    currentPiece = nextPiece;
    nextPiece = spawnPiece();
    if (nextPieceListener != null) {
      nextPieceListener.nextPiece(currentPiece, nextPiece);
    }
    communicator.send("SCORES");
  }

  /**
   * Updates the score according to the lines cleared and blocks cleared, sends a message to the
   * communicator telling it the updated score.
   *
   * @param numberOfLines  number of lines cleared
   * @param numberOfBlocks number of blocks cleared
   */
  @Override
  protected void score(int numberOfLines, int numberOfBlocks) {
    super.score(numberOfLines, numberOfBlocks);
    communicator.send("SCORE " + getScore());
  }

  /**
   * Ends the game and sends a message to the communicator indicating that the game has been ended
   * for the current player.
   */
  @Override
  public void endGame() {
    super.endGame();
    communicator.send("DIE");
  }




}

