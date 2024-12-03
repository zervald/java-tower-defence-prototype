package logiciel;

import java.awt.Color;
import java.util.List;

/** TourGlace */
public class TourGlace extends Tour {
  public static final Tuile TOUR_BAS = new Tuile(Color.darkGray, Color.lightGray, BitMap.MUR);
  public static final Tuile TOUR_HAUT = new Tuile(Color.cyan, Color.lightGray, BitMap.TOUR_DESSUS);

  private static final int NB_CARACTERISTIQUE = 2;

  private static final int DISTANCE = 0;
  private static final int FROID = 1;

  private static final int[] PRIX_AUGMENTATION_DISTANCE = {40, 60};
  private static final int[] DISTANCE_MAX_FROID_PIXEL = {24, 32, 40};

  private static final int[] PRIX_AUGMENTATION_REDUCTION = {60};
  private static final int[] REDUCTION_VITESSE_ENNEMIES = {20, 40};

  private List<Ennemi> ennemisPrecedents;

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

  public TourGlace(PositionTuile position) {
    super(position, TOUR_BAS, TOUR_HAUT);
  }

  @Override
  public int animer(List<Ennemi> ennemis) {
    if (ennemisPrecedents != null && !ennemisPrecedents.isEmpty()) {
      ennemisPrecedents.forEach(Ennemi::reinitialiserVitesse);
    }

    ennemis.forEach(e -> e.reduireVitesse(caracteristiques[FROID].getValeur()));
    ennemisPrecedents = ennemis;

    return 0;
  }
}
