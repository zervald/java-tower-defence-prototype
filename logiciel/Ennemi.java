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
  private int valeurArgent;

  private static final int DOMMAGE_FEU = 0;
  private static final int PERIODE_FEU_TIC = 5;
  private int durationFeu;
  private boolean estEnFeu;
  private TourFeu sourceFeu;

  private Tuile image;
  private final Tuile image_original;
  private final Tuile image_froid;
  private final Tuile image_feu;

  private int recharge_tic;

  public Ennemi(
      Chemin chemin,
      double vitesse,
      int pointVie,
      int valeurArgent,
      Tuile image,
      Tuile image_froid,
      Tuile image_feu) {

    this.chemin = chemin;
    this.vitesse = vitesse;
    this.VITESSE_ORIGINALE = vitesse;
    this.pointVie = pointVie;
    this.pointVieMax = pointVie;
    this.valeurArgent = valeurArgent;
    this.image = image;
    this.image_original = image;
    this.image_froid = image_froid;
    this.image_feu = image_feu;
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
    image_original = original.image_original;
    image_froid = original.image_froid;
    image_feu = original.image_feu;
  }

  public static int comparer(Ennemi e1, Ennemi e2) {
    return Integer.compare(e1.distanceChateau(), e2.distanceChateau());
  }

  public boolean estMort() {
    return pointVie <= 0;
  }

  /** return true if dead (pv <= 0) */
  public boolean reduireVie(int dommage) {
    pointVie -= dommage;
    // System.out.println(this + " dommage de : " + dommage + " PVs: " + pointVie + "/" +
    // pointVieMax);
    return estMort();
  }

  public void appliquerFroid(int reduction) {
    vitesse = vitesse * (1 - (reduction / 100d));
    image = image_froid;
  }

  public void retirerFroid() {
    vitesse = VITESSE_ORIGINALE;
    image = image_original;
  }

  public void appliquerFeu(int duration, TourFeu source) {
    image = image_feu;
    estEnFeu = true;
    durationFeu = duration;
    sourceFeu = source;
  }

  public void retirerFeu() {
    image = image_original;
    estEnFeu = false;
    durationFeu = 0;
    sourceFeu = null;
  }

  public boolean estEnFeu() {
    return estEnFeu;
  }

  private void brule() {
    if (reduireVie(DOMMAGE_FEU)) {
      sourceFeu.transfertArgent(valeurArgent);
    }

    durationFeu--;
    if (durationFeu <= 0) {
      retirerFeu();
    }
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
    recharge_tic++;
    if (estEnFeu && (PERIODE_FEU_TIC <= recharge_tic)) {
      recharge_tic = 0;
      brule();
    }
  }

  public int distanceChateau() {
    return chemin.getLongueur() - ((int) distance);
  }

  public PositionPixel getPositionPixel() {
    return chemin.calculerPosition(noSegment, distance);
  }

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
