package logiciel;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Ennemi {
  private Chemin chemin;
  private int noSegment;
  private double distance;

  private final double VITESSE_ORIGINALE;
  private double vitesse;
  private int pointVie;
  private int pointVieMax;

  private Tuile image;
  private Color effetCouleur;
  private int valeurArgent;

  public Ennemi(Chemin chemin, double vitesse, int pointVie, int valeurArgent, Tuile image) {
    this.chemin = chemin;
    this.vitesse = vitesse;
    this.VITESSE_ORIGINALE = vitesse;
    this.pointVie = pointVie;
    this.pointVieMax = pointVie;
    this.valeurArgent = valeurArgent;
    this.image = image;
    this.effetCouleur = new Color(00000000, true);
    this.distance = 0.0;
    this.noSegment = 0;
  }

  public Ennemi(Ennemi original) {
    chemin = original.chemin;
    noSegment = original.noSegment;
    distance = original.distance;
    vitesse = original.vitesse;
    VITESSE_ORIGINALE = original.VITESSE_ORIGINALE;
    pointVie = original.pointVie;
    pointVieMax = original.pointVieMax;
    valeurArgent = original.valeurArgent;
    image = original.image;
    effetCouleur = original.effetCouleur;
  }

  public static int comparer(Ennemi e1, Ennemi e2) {
    return Integer.compare(e1.distanceChateau(), e2.distanceChateau());
  }

  public boolean reduireVie(int dommage) {
    pointVie -= dommage;
    return pointVie <= 0;
  }

  public void reduireVitesse(int reduction) {
    vitesse = vitesse * (1 - (reduction / 100));
  }

  public void reinitialiserVitesse() {
    vitesse = VITESSE_ORIGINALE;
  }

  public boolean aAtteintChateau() {
    return chemin.nombreSegment() <= noSegment;
  }

  public void avancer() {
    if (noSegment < chemin.nombreSegment()) {
      distance += vitesse;
      int longueur = chemin.getSegment(noSegment).longueur();
      if (longueur < distance) {
        distance -= longueur;
        ++noSegment;
      }
    }
  }

  public int distanceChateau() {
    return chemin.getLongueur() - ((int) distance);
  }

  public PositionPixel getPositionPixel() {
    return chemin.calculerPosition(noSegment, distance);
  }

  // TODO change ennemi color when frost
  public void afficher(Graphics2D g2, AffineTransform affineTransform) {
    if (noSegment < chemin.nombreSegment()) {
      AffineTransform pCurseur = (AffineTransform) affineTransform.clone();
      PositionPixel position = getPositionPixel();
      pCurseur.translate(position.x(), position.y());
      g2.drawImage(image, pCurseur, null);
      pCurseur.translate(0, -2);
      int rPV = (pointVie * Constantes.FACTEUR_PV) / pointVieMax;
      g2.drawImage(Constantes.PV_ENNEMI[rPV], pCurseur, null);
    }
  }

  public int getValeurArgent() {
    return valeurArgent;
  }
}
