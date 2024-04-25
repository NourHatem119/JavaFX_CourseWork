package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.Game;

/**
 * The Game Over Listener is used to handle the event when the game is over.
 */
public interface GameOverListener {

  public void gameOver(Game game);
}
