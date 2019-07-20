import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by TONDEUR H on 16/05/2017.
 */
public class Tabou {


    private int TABOU_MAX_SIZE = 50;

    private int plateauSize;
    private Plateau currentPlateau;
    private int n;
    private Queue<Movement> tabou;
    private Plateau bestPlateau;

    public Tabou(int plateauSize) {
        this.plateauSize = plateauSize;
        currentPlateau = new Plateau(Plateau.getInitialPlateau(plateauSize));
        n = 2500;
        tabou = new LinkedList<>();
        bestPlateau = new Plateau(currentPlateau.getArrayPosition().clone(), currentPlateau.getNbConflits());
    }


    public Plateau start() {
        for (int k = 0; k < n; k++) {
            Plateau voisin = getBestVoisin();

            if (voisin.getNbConflits() < bestPlateau.getNbConflits()) {
                bestPlateau = voisin;
                System.out.println(bestPlateau.getNbConflits());
            }

            if (voisin.getNbConflits() - currentPlateau.getNbConflits() >= 0) {
                this.addTabou(voisin.getMovement());
            }
            currentPlateau = voisin;

            if (bestPlateau.getNbConflits() == 0) {
                System.out.println((k+1) + " itérations");
                return bestPlateau;
            }
        }
        return bestPlateau;
    }


    private Plateau getBestVoisin() {
        int iBestVoisin = 0, jBestVoisin = 0, nbConflitsBestVoisin = Integer.MAX_VALUE;
        int[] bestVoisinArray = new int[0];

        for (int i = 0; i < currentPlateau.getArrayPosition().length - 1; i++) {
            for (int j = i + 1; j < currentPlateau.getArrayPosition().length; j++) {
                boolean stop = false;
                for (Movement movement : tabou) {
                    if (movement.isSameMovement(i, j)) {
                        stop = true;
                        break;
                    }
                }
                if (stop) continue;

                int[] voisinArray = this.switchColums(i, j);

                int nbConflitsVoisin = Plateau.getConflicts(voisinArray);
                if (nbConflitsVoisin < nbConflitsBestVoisin) {
                    nbConflitsBestVoisin = nbConflitsVoisin;
                    bestVoisinArray = voisinArray;
                    iBestVoisin = i;
                    jBestVoisin = j;
                }
            }
        }

        return new Plateau(bestVoisinArray, new Movement(iBestVoisin, jBestVoisin), nbConflitsBestVoisin);
    }


    private int[] switchColums(int i, int j) {
        int[] voisinArray = currentPlateau.getArrayPosition().clone();
        int temp = voisinArray[i];
        voisinArray[i] = voisinArray[j];
        voisinArray[j] = temp;
        return voisinArray;
    }


    private void addTabou(Movement movement) {
        tabou.add(movement);
        if (tabou.size() >= TABOU_MAX_SIZE) {
            tabou.remove();
        }
    }
}
