package logiciel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public abstract class Tour {
  protected PositionTuile position;
  protected Caracteristique[] caracteristiques;

  protected final Image TOUR_BAS;
  protected final Image TOUR_HAUT;

  public Tour(PositionTuile position, Tuile TOUR_BAS, Tuile TOUR_HAUT) {
    this.position = position;
    this.TOUR_BAS = TOUR_BAS;
    this.TOUR_HAUT = TOUR_HAUT;
  }

  public abstract int animer(List<Ennemi> ennemis);

  public void inscrireEvenements(GestionSouris gestionSouris) {
    for (int i = 0; i < caracteristiques.length; ++i) {
      caracteristiques[i].inscrireEvenement(gestionSouris);
    }
  }

  public void desinscrireEvenements(GestionSouris gestionSouris) {
    for (int i = 0; i < caracteristiques.length; ++i) {
      caracteristiques[i].desinscrireEvenement(gestionSouris);
    }
  }

  public void afficherControl(Graphics2D g2, Jeu jeu) {
    AffineTransform pCug = (AffineTransform) jeu.origine_pixel.clone();
    pCug.translate(getPositionPixel().x(), getPositionPixel().y());
    g2.drawImage(Constantes.SOUSLIGNE_TOUR, pCug, null);

    for (int i = 0; i < caracteristiques.length; ++i) {
      caracteristiques[i].afficherControl(g2, jeu);
    }
  }

  public void afficherTour(Graphics2D g2, AffineTransform origine_pixel) {
    AffineTransform pCurseur = (AffineTransform) origine_pixel.clone();
    PositionPixel pos = getPositionTuile().positionPixel();
    pCurseur.translate(pos.x(), pos.y());

    g2.drawImage(TOUR_BAS, pCurseur, null);
    pCurseur.translate(0, -Constantes.TAILLE_TUILE);
    g2.drawImage(TOUR_HAUT, pCurseur, null);
  }

  public PositionTuile getPositionTuile() {
    return position;
  }

  public PositionPixel getPositionPixel() {
    return position.positionPixel();
  }

  /**
   * Calcule les ennemis qui sont à portée de la tour.
   *
   * @param ennemis List d'ennemis présent dans le jeu.
   * @param DISTANCE maximale en "pixel" où un ennemi est considéré à portée.
   * @return List des ennemis à portée de la tour. La liste retourner peut être vide, mais jamais
   *     null.
   */
  protected List<Ennemi> estAPortee(List<Ennemi> ennemis, int DISTANCE) {
    List<Ennemi> ennemisProches = new ArrayList<>();
    for (Ennemi ennemi : ennemis) {
      if (ennemi.getPositionPixel().distance(getPositionPixel())
          < caracteristiques[DISTANCE].getValeur()) {
        ennemisProches.add(ennemi);
      }
    }
    return ennemisProches;
  }
}
