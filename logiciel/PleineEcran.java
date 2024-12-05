package logiciel;

import java.awt.*;
import javax.swing.*;

public class PleineEcran extends JFrame {
  public PleineEcran() throws HeadlessException {
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    GraphicsDevice gDevice =
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    if (gDevice.isFullScreenSupported()) {
      gDevice.setFullScreenWindow(this);
    } else {
      System.err.println("Mode plein écran non supporte.");
      System.exit(-1);
    }
    // setUndecorated(true);
    add(new Jeu(getSize()));
  }
}
