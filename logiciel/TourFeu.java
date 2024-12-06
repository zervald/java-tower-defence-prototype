package logiciel;

import java.awt.Color;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Tour qui "tire une flêche de feu" sur un ennemi proche.
 *
 * <p>Cet effet de feu endommage l'ennemi légèrement à chaque tic. La tour recoit de l'argent si
 * l'effet de feu est responçable de la mort de l'ennemi.
 *
 * <p>La tour a une apparence rouge et orange.
 */
public class TourFeu extends Tour {
  public static final int PERIODE_RECHARGE_TIC = 30;
  public static final Tuile TOUR_BAS = new Tuile(Color.RED, Color.ORANGE, BitMap.MUR);
  public static final Tuile TOUR_HAUT = new Tuile(Color.RED, Color.ORANGE, BitMap.TOUR_DESSUS);

  private static final int NB_CARACTERISTIQUE = 2;

  private static final int DUR_FEU = 0;
  private static final int DISTANCE = 1;

  private static final int[] PRIX_AUGMENTATION_DUREE = {20, 30};
  private static final int[] DUREE_EFFET_FEU = {3, 5, 7};

  private static final int[] PRIX_AUGMENTATION_DISTANCE = {30, 80};
  private static final int[] DISTANCE_MAX_TIR_PIXEL = {20, 30, 50};

  private int recharge_tic = 0;
  private int argentAccumulee = 0;

  {
    caracteristiques = new Caracteristique[NB_CARACTERISTIQUE];

    caracteristiques[DUR_FEU] =
        new Caracteristique(
            "feu",
            DUREE_EFFET_FEU,
            PRIX_AUGMENTATION_DUREE,
            Constantes.POSITION_CARACTERISTIQUE_Y + 0);
    caracteristiques[DISTANCE] =
        new Caracteristique(
            "dis",
            DISTANCE_MAX_TIR_PIXEL,
            PRIX_AUGMENTATION_DISTANCE,
            Constantes.POSITION_CARACTERISTIQUE_Y + 2);
  }

  /**
   * Constructeur appel sa classe parent avec la position donnée et fourni l'apparence des tour de
   * feu.
   *
   * @param position où la base de la tour se trouve dans l'espace de jeu.
   */
  public TourFeu(PositionTuile position) {
    super(position, TOUR_BAS, TOUR_HAUT);
  }

  /**
   * Méthode racine des actions de la tour.
   *
   * <p>À chaque periode de recharge, la tour tir sur un ennemi parmis ceux suffisament proches.
   *
   * <p>Elle retourne aussi l'argent accumulée à la mort des ennemis. Ces transferts en provenance
   * des ennemis ce font durant leur animation. (Contrairement à la tour d'archées)
   */
  @Override
  public int animer(List<Ennemi> ennemis) {

    recharge_tic++;

    if (PERIODE_RECHARGE_TIC <= recharge_tic) {
      tirer(estAPortee(ennemis, DISTANCE));
      recharge_tic = 0;
    }

    int gain = argentAccumulee;
    argentAccumulee = 0;
    return gain;
  }

  /**
   * Applique l'effet de feu sur un ennemi parmi la liste qui n'est pas déjà en feu.
   *
   * @param ennemisProches liste contenant seulement les ennemis à porter de la tour.
   */
  private void tirer(List<Ennemi> ennemisProches) {
    if (ennemisProches != null && !ennemisProches.isEmpty()) {
      try {
        // Il n'est pas garanti de trouver un ennemi valide,
        // alors il est necessaire de gerer l'exception.
        ennemisProches.stream()
            .filter((e) -> (!e.estMort() && !e.estEnFeu()))
            .findAny()
            .get()
            .appliquerFeu(caracteristiques[DUR_FEU].getValeur(), this);
      } catch (NoSuchElementException e) {
        // Fait rien, aucun ennemi valide pour recevoir un tir
      }
    }
  }

  /**
   * Utilisé par les ennemis lorsqu'ils meurt du feu.
   *
   * <p>La valeur en argent est accumulé dans un tampon, puis celui-ci sera retourné au jeu lors du
   * prochain appel/tic de la tour.
   *
   * @param valeur en argent de l'ennemi.
   */
  public void transfertArgent(int valeur) {
    if (valeur >= 0) {
      argentAccumulee += valeur;
    }
  }
}
