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
  private TourFeu sourceFeu;
  private boolean estEnFeu;
  private boolean estFroid;

  private final Tuile IMAGE_ORIGINAL;
  private final Tuile IMAGE_FROID;
  private final Tuile IMAGE_FEU;

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
    this.IMAGE_ORIGINAL = image;
    this.IMAGE_FROID = image_froid;
    this.IMAGE_FEU = image_feu;
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
    IMAGE_ORIGINAL = original.IMAGE_ORIGINAL;
    IMAGE_FROID = original.IMAGE_FROID;
    IMAGE_FEU = original.IMAGE_FEU;
  }

  public static int comparer(Ennemi e1, Ennemi e2) {
    return Integer.compare(e1.distanceChateau(), e2.distanceChateau());
  }

  public int getValeurArgent() {
    return valeurArgent;
  }

  public boolean estEnFeu() {
    return estEnFeu;
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

  public boolean aAtteintChateau() {
    return chemin.nombreSegment() <= noSegment;
  }

  public int distanceChateau() {
    return chemin.getLongueur() - ((int) distance);
  }

  public PositionPixel getPositionPixel() {
    return chemin.calculerPosition(noSegment, distance);
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

  /**
   * Crée un effet de "froid" sur l'ennemi.
   *
   * <p>Cet effet ralenti sa vitesse et change sa couleur pour un bleu. L'effet de reduction sur la
   * vitesse est cumulatif.
   *
   * @param reduction % de reduction sur la vitesse courante.
   */
  public void appliquerFroid(int reduction) {
    vitesse = vitesse * (1 - (reduction / 100d));
    estFroid = true;
  }

  /** Retourne l'ennemi à sa vitesse et couleur originiale. */
  public void retirerFroid() {
    vitesse = VITESSE_ORIGINALE;
    estFroid = false;
  }

  /**
   * Crée un effet de feu sur l'ennemi.
   *
   * <p>L'effet retire des pv périodiquements pour une durée déterminé. Cette méthode change aussi
   * la couleur de l'ennemi pour un rouge. Si l'ennemi meurt de cet effet, sa valeur en argent est
   * transférée à la tour passée en paramètre.
   *
   * @param duration en tic. Les pv sont retirés à chaque tic.
   * @param source tour de feu ayant appliqué l'effet.
   * @see #brule()
   */
  public void appliquerFeu(int duration, TourFeu source) {
    estEnFeu = true;
    durationFeu = duration;
    sourceFeu = source;
  }

  /** Éteind l'effet de feu de l'ennemi. */
  public void retirerFeu() {
    estEnFeu = false;
    durationFeu = 0;
    sourceFeu = null;
  }

  /**
   * Brule l'ennemi avec l'effet de feu.
   *
   * <p>Doit être appellé seulement lorsque l'enenmi est en feu.
   *
   * @throws IllegalStateException lorsque l'ennemi n'est pas correctement en feu.
   */
  private void brule() {
    if (estEnFeu && (sourceFeu != null) && durationFeu > 0) {
      // Si reduire la vie tue l'ennemi, transfert argent.
      if (reduireVie(DOMMAGE_FEU)) {
        sourceFeu.transfertArgent(valeurArgent);
      }

      durationFeu--;
      if (durationFeu <= 0) {
        retirerFeu();
      }
    } else {
      throw new IllegalStateException("Essayer de bruler un ennemi qui n'est pas en feu");
    }
  }

  public void afficher(Graphics2D g2, AffineTransform affineTransform) {
    Tuile image;
    if (estEnFeu) {
      image = IMAGE_FEU;
    } else if (estFroid) {
      image = IMAGE_FROID;
    } else {
      image = IMAGE_ORIGINAL;
    }

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
}
