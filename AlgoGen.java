import java.util.*;

/**
 * Created by TONDEUR H on 21/05/2017.
 */
public class AlgoGen {

    private final int NB_INDIVDU_SELECTIONNE = 100;
    private final double PROBA_MUTATION = 0.10;
    private final int NB_ITERATION = 10000000;
    private final int SIZE_POPULATION = 500;
    private final boolean WITH_CORRECTION = false;

    private Plateau bestPlateau;
    private Random random;
    private int plateauSize;

    public AlgoGen(int plateauSize) {
        this.plateauSize = plateauSize;
        random = new Random();
        bestPlateau = new Plateau(Plateau.getInitialPlateau(plateauSize));
    }

    private List<Plateau> createInitialPopulation(int size) {
        List<Plateau> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int[] plateauArray = new int[plateauSize];
            for (int j = 0; j < plateauSize; j++) {
                plateauArray[j] = (j + i) % plateauSize;
            }
            Plateau plateau = new Plateau(plateauArray);
            if (plateau.getNbConflits() < bestPlateau.getNbConflits()) {
                bestPlateau = plateau;
            }
            population.add(plateau);
        }
        return population;
    }

    public Plateau start() {
        List<Plateau> population = createInitialPopulation(SIZE_POPULATION);
        for (int i = 0; i < NB_ITERATION; i++) {
            population = nextGeneration(population);
            if (bestPlateau.getNbConflits() == 0) {
                System.out.println("nb iter : " + i);
                return bestPlateau;
            }
        }
        return bestPlateau;
    }

    private List<Plateau> selectPopulation(List<Plateau> population, int nb) {
        List<Plateau> selectedPopulation = new ArrayList<>();
        Random random = new Random();
        Double finesseInverseTotale = 0.0;
        for (Plateau plateau : population) {
            finesseInverseTotale += 1.0 / plateau.getNbConflits();
        }
        double randomDouble = (random.nextDouble() * finesseInverseTotale) / nb;

        for (int i = 0; i < nb; i++) {
            double randomDoubleMoved = randomDouble + (finesseInverseTotale / nb) * i;
            Plateau selectedPlateau = null;

            int j = 0;
            double somme = 0.0;
            while (somme <= randomDoubleMoved) {
                selectedPlateau = population.get(j);
                somme += 1.0 / selectedPlateau.getNbConflits();
                j++;
            }
            selectedPopulation.add(selectedPlateau);
        }
        for (Plateau plateau : selectedPopulation) {
            population.remove(plateau);
        }
        return selectedPopulation;
    }


    private List<Plateau> nextGeneration(List<Plateau> population) {
        List<Plateau> newGeneration = new ArrayList<>();
        List<Plateau> selectedPopulation = selectPopulation(population, NB_INDIVDU_SELECTIONNE);

        for (int i = 0; i < selectedPopulation.size(); i += 2) {
            Plateau plateau1 = selectedPopulation.get(i);
            Plateau plateau2 = selectedPopulation.get(i + 1);
            Plateau[] enfants = croisePlateaux(plateau1, plateau2);
            for (Plateau enfant : enfants) {
                if (enfant.getNbConflits() < bestPlateau.getNbConflits()) {
                    bestPlateau = enfant;
                    System.out.println(bestPlateau.getNbConflits());
                }
                newGeneration.add(enfant);
            }
            newGeneration.add(plateau1);
            newGeneration.add(plateau2);
        }

        for (Plateau plateau : population) {
            if (newGeneration.size() >= SIZE_POPULATION) break;
            newGeneration.add(plateau);
        }
        newGeneration = muteGeneration(newGeneration);
        return newGeneration;
    }

    private List<Plateau> muteGeneration(List<Plateau> population) {
        for (int i = 0; i < population.size(); i++) {
            //Mutation d'un individu selon la probabilité choisie, si mutation on choisit un voisin
            double randomDouble = random.nextDouble();
            if (randomDouble < PROBA_MUTATION) {
                Plateau plateau = Plateau.getVoisin(population.get(i).getArrayPosition());
                population.set(i, plateau);
                if (plateau.getNbConflits() < bestPlateau.getNbConflits()) {
                    bestPlateau = plateau;
                    System.out.println(bestPlateau.getNbConflits());
                }
            }
        }
        return population;
    }

    private Plateau[] croisePlateaux(Plateau plateau1, Plateau plateau2) {
        Plateau[] enfants = new Plateau[2];
        Plateau enfant1, enfant2;
        Random random = new Random();


        int rnd = random.nextInt(plateauSize - 1);
        int[] enfant1Array = new int[plateauSize];
        int[] enfant2Array = new int[plateauSize];

        //Découpe des parents
        int[] plateau1part1 = Arrays.copyOfRange(plateau1.getArrayPosition(), 0, rnd + 1);
        int[] plateau1part2 = Arrays.copyOfRange(plateau1.getArrayPosition(), rnd + 1, plateauSize);
        int[] plateau2part1 = Arrays.copyOfRange(plateau2.getArrayPosition(), 0, rnd + 1);
        int[] plateau2part2 = Arrays.copyOfRange(plateau2.getArrayPosition(), rnd + 1, plateauSize);

        //Fusion des 2 parties pour le premier enfant
        System.arraycopy(plateau1part1, 0, enfant1Array, 0, rnd + 1);
        System.arraycopy(plateau2part2, 0, enfant1Array, rnd + 1, plateauSize - rnd - 1);

        //Fusion des 2 parties pour le deuxième enfant
        System.arraycopy(plateau2part1, 0, enfant2Array, 0, rnd + 1);
        System.arraycopy(plateau1part2, 0, enfant2Array, rnd + 1, plateauSize - rnd - 1);

        if (WITH_CORRECTION) {
            enfant1Array = corrigePlateauArray(enfant1Array);
            enfant2Array = corrigePlateauArray(enfant2Array);
        }

        enfant1 = new Plateau(enfant1Array);
        enfant2 = new Plateau(enfant2Array);
        enfants[0] = enfant1;
        enfants[1] = enfant2;

        return enfants;
    }


    private int[] corrigePlateauArray(int[] plateauArray) {
        int[] sortedArray = plateauArray.clone();
        Arrays.sort(sortedArray);
        int current = -1;
        List<Integer> doublons = new ArrayList<>();
        List<Integer> manquants = new ArrayList<>();

        for (int value : sortedArray) {
            if (value == current) {
                doublons.add(current);
            } else if (value > current + 1) {
                while (value > current + 1) {
                    manquants.add(current + 1);
                    current++;
                }
                current++;
            } else {
                current++;
            }
        }

        if (sortedArray[plateauSize - 1] < plateauSize - 1) {
            for (int x = sortedArray[plateauSize - 1]; x < plateauSize - 1; x++) {
                manquants.add(x);
            }
        }

        if (doublons.isEmpty()) return plateauArray;
        int index;
        for (int i = 0; i < plateauArray.length; i++) {
            index = doublons.indexOf(plateauArray[i]);
            if (index > -1) {
                doublons.remove(index);
                plateauArray[i] = manquants.remove(0);
            }
        }
        return plateauArray;
    }
}
