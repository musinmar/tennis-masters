package tm.ui.qt;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.GaussianMutator;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;
import org.neuroph.nnet.MultiLayerPerceptron;
import tm.lib.domain.core.Match;
import tm.lib.domain.core.Stadium;
import tm.lib.domain.world.GameWorld;
import tm.lib.engine.MatchSimulator;
import tm.lib.engine.Side;
import tm.lib.engine.Strategy;
import tm.lib.engine.StrategyProvider;
import tm.lib.engine.strategies.NeuralNetworkStrategy;
import tm.lib.engine.strategies.StandardStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static org.jenetics.internal.math.base.clamp;
import static org.jenetics.internal.math.random.indexes;
import static org.jenetics.internal.math.random.nextDouble;
import static org.jenetics.util.RandomRegistry.getRandom;

public class NeuralNetworkTeacher {

    private static final int POPULATION_SIZE = 128;
    private static final int EVOLUTION_STEPS = 2000;
    private static final int OFFSPRING_MATCH_COUNT = 1;
    private static final int SELECTION_MATCH_COUNT = 10;
    private static final double MIN_WEIGHT = -10;
    private static final double MAX_WEIGHT = 10;

    private final GameWorld gameWorld;
    private MultiLayerPerceptron bestFoundPerceptron;

    public NeuralNetworkTeacher(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public MultiLayerPerceptron getBestFoundPerceptron() {
        return bestFoundPerceptron;
    }

    public void teachWithCustomEvolution() {
        List<MultiLayerPerceptron> population = generateInitialPopulation();

        for (int i = 0; i < EVOLUTION_STEPS; i++) {
            population = performEvolutionStep(population);

            double approximateScore = eval(population.get(0));
            System.out.println("Iteration " + i + ": " + approximateScore);
        }

        bestFoundPerceptron = findBestPerceptron(population);
        double approximateScore = eval(bestFoundPerceptron);
        System.out.println("Final score: " + approximateScore);
    }

    private List<MultiLayerPerceptron> generateInitialPopulation() {
        List<MultiLayerPerceptron> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            MultiLayerPerceptron perceptron = generateTemplatePerceptron();
            perceptron.randomizeWeights(MIN_WEIGHT, MAX_WEIGHT);
            population.add(perceptron);
        }
        return population;
    }

    private List<MultiLayerPerceptron> performEvolutionStep(List<MultiLayerPerceptron> population) {
        List<MultiLayerPerceptron> populationWithOffspring = createOffspring(population);
        Collections.shuffle(populationWithOffspring);
        return fightBetweenPerceptrons(populationWithOffspring, OFFSPRING_MATCH_COUNT);
    }

    private List<MultiLayerPerceptron> createOffspring(List<MultiLayerPerceptron> population) {
        List<MultiLayerPerceptron> newPopulation = new ArrayList<>();
        for (MultiLayerPerceptron perceptron : population) {
            MultiLayerPerceptron child = createChild(perceptron);
            newPopulation.add(perceptron);
            newPopulation.add(child);
        }
        return newPopulation;
    }

    private MultiLayerPerceptron createChild(MultiLayerPerceptron parent) {
        Double[] weights = parent.getWeights();

        Random random = RandomRegistry.getRandom();
        indexes(random, weights.length, 0.1)
                .forEach(i -> weights[i] = mutateGaussian(weights[i], MIN_WEIGHT, MAX_WEIGHT, random));
        indexes(random, weights.length, 0.05)
                .forEach(i -> weights[i] = mutateOverall(MIN_WEIGHT, MAX_WEIGHT, random));

        MultiLayerPerceptron child = generateTemplatePerceptron();
        child.setWeights(Arrays.stream(weights).mapToDouble(d -> d).toArray());
        return child;
    }

    private double mutateGaussian(double value, double min, double max, final Random random) {
        final double std = (max - min) * 0.25;
        final double gaussian = random.nextGaussian();
        return clamp(gaussian * std + value, min, max);
    }

    private double mutateOverall(double min, double max, final Random random) {
        return nextDouble(getRandom(), min, max);
    }

    private List<MultiLayerPerceptron> fightBetweenPerceptrons(List<MultiLayerPerceptron> population, int matchCount) {
        return IntStream.range(0, population.size() / 2)
                .parallel()
                .mapToObj(i -> selectBetterPerceptron(population.get(i * 2), population.get(i * 2 + 1), matchCount))
                .collect(Collectors.toList());
    }

    private MultiLayerPerceptron findBestPerceptron(List<MultiLayerPerceptron> population) {
        while (population.size() > 1) {
            population = fightBetweenPerceptrons(population, SELECTION_MATCH_COUNT);
        }
        return population.get(0);
    }

    private MultiLayerPerceptron selectBetterPerceptron(MultiLayerPerceptron a, MultiLayerPerceptron b, int matchCount) {
        StrategyProvider strategyProvider = new StrategyProvider(new NeuralNetworkStrategy(a), new NeuralNetworkStrategy(b));
        Match match = new Match();
        match.setHomePlayer(gameWorld.getPlayers().get(5));
        match.setAwayPlayer(gameWorld.getPlayers().get(5));
        match.setSets(2);
        match.setPlayoff(false);
        match.setVenue(Stadium.test_stadium());

        int winCount = 0;
        int lossCount = 0;
        long winningTime = 0;
        long losingTime = 0;
        for (int i = 0; i < matchCount; i++) {
            MatchSimulator matchSimulator = simulateMatch(match, strategyProvider);
            List<MatchSimulator.PointResult> pointResults = matchSimulator.getPointResults();
            for (MatchSimulator.PointResult result : pointResults) {
                if (result.getWinningSide() == Side.HOME) {
                    winCount++;
                    winningTime += result.getTime();
                } else {
                    lossCount++;
                    losingTime += result.getTime();
                }
            }
        }

        return winCount >= lossCount ? a : b;
    }

    public void teach() {
        MultiLayerPerceptron perceptron = generateTemplatePerceptron();
        Double[] weights = perceptron.getWeights();

        Factory<Genotype<DoubleGene>> gtf = Genotype.of(DoubleChromosome.of(-10, 10, weights.length));
        Engine<DoubleGene, Double> engine = Engine.builder(this::eval, gtf)
                .populationSize(200)
                .maximalPhenotypeAge(10)
                .alterers(
                        new SinglePointCrossover<>(0.15),
                        new GaussianMutator<>(0.15),
                        new Mutator<>(0.15))
                .survivorsSelector(new RouletteWheelSelector<>())
                .build();
        EvolutionStatistics<Double, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();
        Genotype<DoubleGene> result = engine.stream()
                //.limit(limit.bySteadyFitness(200))
                .limit(50)
                .peek(statistics)
                .peek(r -> {
                    //double average = r.getPopulation().stream().mapToDouble(Phenotype::getFitness).average().orElseGet(() -> 0);
                    DoubleStream sortedStream = r.getPopulation().stream().mapToDouble(Phenotype::getFitness).sorted();
                    double median = r.getPopulation().size() % 2 == 0 ?
                            sortedStream.skip(r.getPopulation().size() / 2 - 1).limit(2).average().getAsDouble() :
                            sortedStream.skip(r.getPopulation().size() / 2).findFirst().getAsDouble();
                    System.out.println("Iteration " + r.getGeneration() + ": " + r.getBestFitness() + ", median: " + median);
                })
                .collect(EvolutionResult.toBestGenotype());
        System.out.println(statistics);
        double[] doubles = result.getChromosome().as(DoubleChromosome.class).toArray();
        perceptron.setWeights(doubles);
        bestFoundPerceptron = perceptron;
    }

    private MultiLayerPerceptron generateTemplatePerceptron() {
        return new MultiLayerPerceptron(6, 20, 12, 8, 2);
    }

    private Double eval(Genotype<DoubleGene> gt) {
        double[] doubles = gt.getChromosome().as(DoubleChromosome.class).toArray();
        MultiLayerPerceptron perceptron = generateTemplatePerceptron();
        perceptron.setWeights(doubles);
        return eval(perceptron);
    }

    private Double eval(MultiLayerPerceptron perceptron) {
        StrategyProvider strategyProvider = new StrategyProvider(new NeuralNetworkStrategy(perceptron), new StandardStrategy());
        Match match = new Match();
        match.setHomePlayer(gameWorld.getPlayers().get(5));
        match.setAwayPlayer(gameWorld.getPlayers().get(5));
        match.setSets(2);
        match.setPlayoff(false);
        match.setVenue(Stadium.test_stadium());

        int winCount = 0;
        int lossCount = 0;
        long winningTime = 0;
        long losingTime = 0;
        for (int i = 0; i < 10; i++) {
            MatchSimulator matchSimulator = simulateMatch(match, strategyProvider);
            List<MatchSimulator.PointResult> pointResults = matchSimulator.getPointResults();
            for (MatchSimulator.PointResult result : pointResults) {
                if (result.getWinningSide() == Side.HOME) {
                    winCount++;
                    winningTime += result.getTime();
                } else {
                    lossCount++;
                    losingTime += result.getTime();
                }
            }
        }


        double losingTimeFactor = lossCount > 0 ? losingTime / (double) lossCount / 100000000.0 : 0;
        double winningTimeFactor = winCount > 0 ? winningTime / (double) winCount / 100000000.0 : 0;
        double pointsFactor = winCount / (double) (winCount + lossCount) * 1000;
        return pointsFactor - winningTimeFactor + losingTimeFactor;
    }

    private MatchSimulator simulateMatch(Match match, StrategyProvider strategyProvider) {
        MatchSimulator matchSimulator = new MatchSimulator(match, strategyProvider);
        MatchSimulator.State state;
        do {
            state = matchSimulator.proceed();
        } while (state != MatchSimulator.State.MATCH_ENDED);
        return matchSimulator;
    }
}
