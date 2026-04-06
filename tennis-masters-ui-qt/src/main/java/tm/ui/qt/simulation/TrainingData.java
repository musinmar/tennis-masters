package tm.ui.qt.simulation;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import tm.lib.engine.VectorUtils;

import java.util.ArrayList;
import java.util.List;

import static tm.lib.engine.Pitch.HALF_HEIGHT;
import static tm.lib.engine.Pitch.WIDTH;
import static tm.lib.engine.Pitch.normalize;

public class TrainingData {

    private static class StructuredSample {
        Vector2D homePlayerPosition;
        Vector2D awayPlayerPosition;
        Vector2D ballPosition;
        Vector2D ballTarget;
        Vector2D moveToPosition;
        Vector2D shootTarget;

        public StructuredSample(Vector2D homePlayerPosition, Vector2D awayPlayerPosition, Vector2D ballPosition, Vector2D ballTarget, Vector2D moveToPosition, Vector2D shootTarget) {
            this.homePlayerPosition = homePlayerPosition;
            this.awayPlayerPosition = awayPlayerPosition;
            this.ballPosition = ballPosition;
            this.ballTarget = ballTarget;
            this.moveToPosition = moveToPosition;
            this.shootTarget = shootTarget;
        }

        DataSetRow toDataSetRow() {
            Vector2D normalizedMoveToPosition = normalize(moveToPosition.getX(), moveToPosition.getY());
            Vector2D normalizedShootTarget = normalize(shootTarget.getX(), shootTarget.getY());
            return new DataSetRow(
                    new double[]{
                            normalize(homePlayerPosition).getX(),
                            normalize(homePlayerPosition).getY(),
                            normalize(awayPlayerPosition).getX(),
                            normalize(awayPlayerPosition).getY(),
                            normalize(ballPosition).getX(),
                            normalize(ballPosition).getY(),
                            normalize(ballTarget).getX(),
                            normalize(ballTarget).getY()
                    },
                    new double[]{
                            normalizedMoveToPosition.getX(),
                            normalizedMoveToPosition.getY(),
                            normalizedShootTarget.getX(),
                            normalizedShootTarget.getY()
                    }
            );
        }
    }

    public static DataSet createTrainingDataSet() {
        DataSet dataSet = new DataSet(8, 4);

        List<StructuredSample> samples = new ArrayList<>();
        final double STEP = 100;
        for (double dx = 0; dx < WIDTH + 1; dx += STEP) {
            for (double dy = -HALF_HEIGHT; dy < HALF_HEIGHT + 1; dy += STEP) {
                for (double dk = 0; dk < WIDTH + 1; dk += STEP) {
                    for (double dl = -HALF_HEIGHT; dl < HALF_HEIGHT + 1; dl += STEP) {
                        samples.add(new StructuredSample(
                                new Vector2D(dk, dl),
                                //new Vector2D(0, 0),
                                VectorUtils.generateRandomVector(0, WIDTH, -HALF_HEIGHT, HALF_HEIGHT),
                                new Vector2D(dx, dy),
                                //new Vector2D(0, 0),
                                VectorUtils.generateRandomVector(0, WIDTH, -HALF_HEIGHT, HALF_HEIGHT),
                                new Vector2D(dx, dy),
                                new Vector2D(100, -150)
                        ));
                    }
                }
            }
        }

        samples.stream().map(StructuredSample::toDataSetRow).forEach(dataSet::add);
        return dataSet;
    }
}
