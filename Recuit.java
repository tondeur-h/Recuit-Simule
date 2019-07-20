import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.Random;

/**
 * @author TONDEUR H
 */
public class Recuit {

    //Paramètres de l'algo
    private final double INITIAL_PROBABILITY = 0.00001;
    private final double N1 = 25000;
    private final double N2 = 10;
    private final double GAMMA = 0.99;

    private int plateauSize;
    private Double temperature;
    private Plateau currentPlateau;
    private Plateau bestPlateau;

    public Recuit(int plateauSize) {
        this.plateauSize = plateauSize;
        currentPlateau = new Plateau(Plateau.getInitialPlateau(plateauSize));
        bestPlateau = new Plateau(currentPlateau.getArrayPosition().clone(), currentPlateau.getNbConflits());
        temperature = calculInitialTemperature();
        System.out.println("temp : " + temperature);
    }

    public Plateau start() {
        int delta;
        Random rnd = new Random();
        Double p;

        for (int k = 0; k < N1; k++) {
            for (int l = 0; l < N2; l++) {
                Plateau plateauVoisin = Plateau.getVoisin(currentPlateau.getArrayPosition());
                delta = plateauVoisin.getNbConflits() - currentPlateau.getNbConflits();
                if (delta <= 0) {
                    currentPlateau = plateauVoisin;
                    if (plateauVoisin.getNbConflits() < bestPlateau.getNbConflits()) {
                        bestPlateau = new Plateau(plateauVoisin.getArrayPosition());
                        if (bestPlateau.getNbConflits() == 0) {
                            System.out.println(k*N2 + " itérations");
                            return bestPlateau;
                        }
                    }
                } else {
                    p = rnd.nextDouble();
                    if (p <= Math.exp(-((double) delta) / temperature)) {
                        currentPlateau = plateauVoisin;
                    }
                }
            }
            System.out.println(bestPlateau.getNbConflits());
            temperature = temperature > 0.0 ? temperature * GAMMA : temperature;
        }
        return bestPlateau;
    }

    private double calculInitialTemperature() {
        double averageDelta = 0.0;
        for (int i = 0; i < 20; i++) {
            averageDelta += Math.abs(Plateau.getVoisin(currentPlateau.getArrayPosition()).getNbConflits() - currentPlateau.getNbConflits());
        }
        averageDelta = averageDelta / 20;
        return -averageDelta / Math.log(INITIAL_PROBABILITY);
    }
}
