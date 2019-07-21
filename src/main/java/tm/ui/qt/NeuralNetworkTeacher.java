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

import java.util.List;
import java.util.stream.DoubleStream;

public class NeuralNetworkTeacher {

    private final GameWorld gameWorld;
    private MultiLayerPerceptron bestFoundPerceptron;

    public NeuralNetworkTeacher(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public MultiLayerPerceptron getBestFoundPerceptron() {
        return bestFoundPerceptron;
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
        return new MultiLayerPerceptron(6, 20, 12, 2);
    }

    private Double eval(Genotype<DoubleGene> gt) {
        double[] doubles = gt.getChromosome().as(DoubleChromosome.class).toArray();
        MultiLayerPerceptron perceptron = generateTemplatePerceptron();
        perceptron.setWeights(doubles);
        Strategy opposingStrategy = bestFoundPerceptron != null ? new NeuralNetworkStrategy(bestFoundPerceptron) : new StandardStrategy();
        StrategyProvider strategyProvider = new StrategyProvider(new NeuralNetworkStrategy(perceptron), opposingStrategy);
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
