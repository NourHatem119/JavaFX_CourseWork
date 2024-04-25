package uk.ac.soton.comp1206.event;

/**
 * The Game Loop listener is used to handle the event when one gameLoop is finished. It passes
 * the duration of one Game Loop.
 */
public interface GameLoopListener {

  public void gameLoop(int timeUntilFinished);
}
