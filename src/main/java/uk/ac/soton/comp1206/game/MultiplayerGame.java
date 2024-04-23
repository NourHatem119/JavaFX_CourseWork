package uk.ac.soton.comp1206.game;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scene.MultiPlayerScene;

public class MultiplayerGame extends Game{

  private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

  Communicator communicator;

  final Queue<GamePiece> pieces = new LinkedList<>();
  Timer timer;
  //Called in initialise to fetch the first 2 pieces and show them thread safe because linked
  // lists are not thread safe
  Runnable spawnThread = new Runnable() {
    @Override
    public void run() {
      if (currentPiece == null) {
        currentPiece = spawnPiece();
      }
      if (currentPiece != null && nextPiece == null) {
        nextPiece = spawnPiece();
        logger.info("Spawn Thread {}", this);
      }

      if (currentPiece == null || nextPiece == null) {
        Platform.runLater(spawnThread);
      }
      else if (nextPieceListener != null) {
        nextPieceListener.nextPiece(currentPiece, nextPiece);
      }
    }
  };

  @Override
  public void initialiseGame() {
    Platform.runLater(spawnThread);
//    Platform.runLater(() -> {
//      while(currentPiece==null ||nextPiece==null){
//        currentPiece = spawnPiece();
//        nextPiece = spawnPiece();
//        try {
//          Thread.sleep(100);
//        } catch (InterruptedException e) {
//          throw new RuntimeException(e);
//        }
//      }
//
//      if (nextPieceListener != null) {
//        nextPieceListener.nextPiece(currentPiece, nextPiece);
//      }
//    });
  }

  private  void handlePiece(String pieceNo) {
    logger.info("Handle Piece {}", this);
    pieceNo = pieceNo.replace("PIECE ", "");
    GamePiece piece = GamePiece.createPiece(Integer.parseInt(pieceNo.trim()));
    synchronized (pieces){
      pieces.add(piece);
    }

    logger.info("Piece Added ... {}/{}", piece, pieceNo);
  }

  @Override
  public void start() {
    Platform.runLater(this::initialiseGame);
    createTimer();
  }

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
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
    generatePieces();
  }

  @Override
  public GamePiece spawnPiece() {
//    communicator.send("PIECE");
    logger.info("Spawn Piece {}", this);
    synchronized (pieces){
      GamePiece piece = pieces.poll();
      logger.info("Spawning Piece  {}", piece);
      return piece;
    }


  }

  @Override
  protected void nextPiece() {
    currentPiece = nextPiece;
    nextPiece = spawnPiece();
    if (nextPieceListener != null) {
      nextPieceListener.nextPiece(currentPiece, nextPiece);
    }
    communicator.send("SCORES");
  }

  @Override
  protected void score(int numberOfLines, int numberOfBlocks) {
    super.score(numberOfLines, numberOfBlocks);
    communicator.send("SCORE " + getScore());
  }

  public void generatePieces() {
    timer = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        if (pieces.size() < 500) {
          communicator.send("PIECE");
        }
      }
    };
    timer.schedule(task, 0, 10);
  }

  @Override
  public void endGame() {
    super.endGame();
    communicator.send("DIE");
  }
}

