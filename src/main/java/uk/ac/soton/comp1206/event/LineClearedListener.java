package uk.ac.soton.comp1206.event;

import java.util.HashSet;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * The Line Cleared Listener is used to handle when a line is cleared.
 */
public interface LineClearedListener {

  public void lineCleared(HashSet<GameBlockCoordinate> blocks);
}
