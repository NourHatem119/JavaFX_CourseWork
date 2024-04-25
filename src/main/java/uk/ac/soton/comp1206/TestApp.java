package uk.ac.soton.comp1206;

import uk.ac.soton.comp1206.network.Communicator;

public class TestApp {

  static Communicator communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9700");

  public static void main(String[] args) {

    communicator.addListener(message -> {
      System.out.println(message);
      if (message.startsWith("START")) {
        for (int i = 0; i < 10; i++) {
          communicator.send("PIECE");
        }
      }
    });
    communicator.send("JOIN Hatem");
//    communicator.send("START");

  }
}
