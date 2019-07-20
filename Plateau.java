import java.util.Random;

/**
 * Created by TONDEUR H on 16/05/2017.
 */
public class Plateau implements Cloneable {

    private int[] arrayPosition;
    private Movement movement;
    private int nbConflits;

    public Plateau(int[] arrayPosition) {
        this.arrayPosition = arrayPosition;
        nbConflits = Plateau.getConflicts(arrayPosition);
    }

    public Plateau(int[] plateau, Movement movement, int nbConflits) {
        this.arrayPosition = plateau;
        this.movement = movement;
        this.nbConflits = nbConflits;
    }

    public Plateau(int[] plateau, int nbConflits) {
        this.arrayPosition = plateau;
        this.nbConflits = nbConflits;
    }

    public static int[] getInitialPlateau(int size) {
        int[] plateau = new int[size];
        for (int i = 0; i < size; i++) {
            plateau[i] = i;
        }
        return plateau;
    }

    public static Plateau getVoisin(int[] plateau) {
        Random rnd = new Random();
        int a = rnd.nextInt(plateau.length);
        int b = a;
        int temp;
        while (a == b) {
            b = rnd.nextInt(plateau.length);
        }
        int[] voisin = plateau.clone();
        temp = voisin[a];
        voisin[a] = voisin[b];
        voisin[b] = temp;
        return new Plateau(voisin, Plateau.getConflicts(voisin));
    }


    public static int getConflicts(int[] plateau) {
        int nbConflicts = 0;
        for (int i = 0; i < plateau.length - 1; i++) {
            for (int j = i + 1; j < plateau.length; j++) {
                if (((j - i) == (plateau[j] - plateau[i])) || ((j - i) == -(plateau[j] - plateau[i]))) {
                    nbConflicts++;
                }
            }
        }
        return nbConflicts;
    }

    public int[] getArrayPosition() {
        return arrayPosition;
    }

    public Movement getMovement() {
        return movement;
    }

    public int getNbConflits() {
        return nbConflits;
    }
}
