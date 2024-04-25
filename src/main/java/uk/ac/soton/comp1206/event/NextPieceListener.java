package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Next Piece Listener is used to handle when we have a new piece spawned.
 */
public interface NextPieceListener {

  public void nextPiece(GamePiece currentPiece, GamePiece nextPiece);

}
