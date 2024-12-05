package logiciel;

import java.awt.Color;
import java.util.List;
import java.util.NoSuchElementException;

/** TourFeu */
public class TourFeu extends Tour {
  public static final int PERIODE_RECHARGE_TIC = 30;
  public static final Tuile TOUR_BAS = new Tuile(Color.red, Color.lightGray, BitMap.MUR);
  public static final Tuile TOUR_HAUT = new Tuile(Color.red, Color.lightGray, BitMap.TOUR_DESSUS);

  private static final int NB_CARACTERISTIQUE = 2;

  private static final int DUR_FEU = 0;
  private static final int DISTANCE = 1;

  private static final int[] PRIX_AUGMENTATION_DUREE = {20, 30};
  private static final int[] DUREE_EFFET_FEU = {2000, 5, 7};

  private static final int[] PRIX_AUGMENTATION_DISTANCE = {30, 80};
  private static final int[] DISTANCE_MAX_TIR_PIXEL = {20, 30, 50};

  private int recharge_tic = 0;
  private int argentAccumulle = 0;

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

  public TourFeu(PositionTuile position) {
    super(position, TOUR_BAS, TOUR_HAUT);
  }

  @Override
  public int animer(List<Ennemi> ennemis) {
    boolean pretTirer = false;

    recharge_tic++;

    if (PERIODE_RECHARGE_TIC <= recharge_tic) {
      pretTirer = true;
      recharge_tic = 0;
    }

    if (pretTirer) {
      tirer(ennemis);
    }

    int gain = argentAccumulle;
    argentAccumulle = 0;
    return gain;
  }

  private void tirer(List<Ennemi> ennemis) {
    List<Ennemi> ennemisProches = getInRange(ennemis, DISTANCE);

    if (ennemisProches != null && !ennemisProches.isEmpty()) {
      try {
        ennemisProches.stream()
            .filter((e) -> (!e.estMort() && !e.estEnFeu()))
            .findAny()
            .get()
            .appliquerFeu(caracteristiques[DUR_FEU].getValeur(), this);
      } catch (NoSuchElementException e) {
        System.out.println("No valid ennemy for to shoot");
        // Aucun ennemi valide pour recevoir un tir
      }
    }
  }

  public void transfertArgent(int valeur) {
    if (valeur >= 0) {
      argentAccumulle += valeur;
    }
  }
}
