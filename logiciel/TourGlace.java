package logiciel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Tour qui ralenti tout les ennemis à l'intérieur de son rayon d'action.
 *
 * <p>Cet effet de ralentissement est appellé "froid" et est cumulatif si plusieurs tours de glace
 * sont suffisament proches.
 *
 * <p>Cette tour est bleu pour rapeller le froid et de la glace.
 */
public class TourGlace extends Tour {
  public static final Tuile TOUR_BAS = new Tuile(Color.BLUE, Color.CYAN, BitMap.MUR);
  public static final Tuile TOUR_HAUT = new Tuile(Color.BLUE, Color.CYAN, BitMap.TOUR_DESSUS);

  private static final int NB_CARACTERISTIQUE = 2;

  private static final int DISTANCE = 0;
  private static final int FROID = 1;

  private static final int[] PRIX_AUGMENTATION_DISTANCE = {40, 60};
  private static final int[] DISTANCE_MAX_FROID_PIXEL = {24, 32, 40};

  private static final int[] PRIX_AUGMENTATION_REDUCTION = {60};
  private static final int[] REDUCTION_VITESSE_ENNEMIES = {20, 40};

  private List<Ennemi> ennemisAffectés = new ArrayList<>();

  {
    caracteristiques = new Caracteristique[NB_CARACTERISTIQUE];

    caracteristiques[DISTANCE] =
        new Caracteristique(
            "dis",
            DISTANCE_MAX_FROID_PIXEL,
            PRIX_AUGMENTATION_DISTANCE,
            Constantes.POSITION_CARACTERISTIQUE_Y);
    caracteristiques[FROID] =
        new Caracteristique(
            "frd",
            REDUCTION_VITESSE_ENNEMIES,
            PRIX_AUGMENTATION_REDUCTION,
            Constantes.POSITION_CARACTERISTIQUE_Y + 2);
  }

  /**
   * Constructeur appel sa classe parent avec la position donnée et fourni l'apparence des tour de
   * glace.
   *
   * @param position où la base de la tour se trouve dans l'espace de jeu.
   */
  public TourGlace(PositionTuile position) {
    super(position, TOUR_BAS, TOUR_HAUT);
  }

  /**
   * Méthode racine des actions de la tour.
   *
   * <p>À chaque tic, une tour de glace applique le froid à tout les ennemis à sa portée. Également,
   * à chaque début de tic / d'animation, elle retire l'effet sur les ennemis qui fût affectés au
   * tic passée.
   *
   * <p>Cette façon de faire évite le besoin de trouver et filtrer les ennemis qui ont quittés la
   * portée.
   */
  @Override
  public int animer(List<Ennemi> ennemis) {
    retirerFroid(ennemisAffectés);

    List<Ennemi> ennemisProches = estAPortee(ennemis, DISTANCE);
    appliquerFroid(ennemisProches);
    ennemisAffectés.addAll(ennemisProches);

    return 0;
  }

  /**
   * Applique l'effet "froid" sur tout les ennemis fourni.
   *
   * <p>L'effe
   *
   * @param ennemisProches soit les ennemis suffisament proches et valides.
   */
  private void appliquerFroid(List<Ennemi> ennemisProches) {
    ennemisProches.stream().forEach(e -> e.appliquerFroid(caracteristiques[FROID].getValeur()));
  }

  /**
   * Retire l'effet "froid" sur tout les ennemis fournis.
   *
   * <p>L'objectif de cette methode est de retirer l'effet sur les ennemis affectés au tic
   * précédent.
   *
   * @param ennemisPrecedents listes d'ennemis à retourner à leur vitesse et couleur originale.
   */
  private void retirerFroid(List<Ennemi> ennemisPrecedents) {
    if (ennemisPrecedents != null) {
      ennemisPrecedents.stream().forEach(Ennemi::retirerFroid);
    }
  }
}
