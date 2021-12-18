import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.System.getenv;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    record Point(int x, int y) {}
    record TargetArea(Point first, Point second) {}

    static class Velocity {
        int x, y;
        Velocity(final int x, final int y) {
            this.x = x; this.y = y;
        }
    }
    static class Measurement {
        final Velocity initialVelocity;
        Point probePosition;
        int maxY;

        public Measurement(final Velocity initialVelocity, final Point probePosition, final int maxY) {
            this.initialVelocity = new Velocity(initialVelocity.x, initialVelocity.y);
            this.probePosition = probePosition;
            this.maxY = maxY;
        }
    }

    static class Probelauncher {
        final TargetArea targetArea;

        public Probelauncher(final TargetArea targetArea) {
            this.targetArea = targetArea;
        }

        Measurement launch(final Velocity velocity) {
            var measurement = new Measurement(velocity, new Point(0, 0), 0);
            rangeClosed(1, Integer.MAX_VALUE).takeWhile(step -> !isWithinTargetArea(measurement.probePosition) &&
                                    notWithinTargetArea(measurement.probePosition) && !beyondTargetArea(measurement.probePosition)).forEach(step -> {
                int newX = measurement.probePosition.x, newY = measurement.probePosition.y;
                newX += velocity.x;
                newY += velocity.y;
                velocity.x = updateDragFactor(velocity.x);
                velocity.y -= 1;
                measurement.probePosition = new Point(newX, newY);
                measurement.maxY = max(newY, measurement.maxY);
            });
            return measurement;
        }

        boolean isWithinTargetArea(final Point probePosition) {
            return probePosition.x >= targetArea.first.x && probePosition.y <= targetArea.first.y &&
                    probePosition.x <= targetArea.second.x && probePosition.y >= targetArea.second.y;
        }

        boolean notWithinTargetArea(final Point probePosition) {
            return probePosition.x < targetArea.first.x || probePosition.y > targetArea.first.y;
        }

        boolean beyondTargetArea(final Point probePosition) {
            return probePosition.x > targetArea.second.x || probePosition.y < targetArea.second.y;
        }

        int updateDragFactor(int x) {
            return x < 0 ? x + 1 : x == 0 ? x : x - 1;
        }
    }

    public long solvePart1(final TargetArea targetArea) { // 19503
        final Probelauncher probelauncher = new Probelauncher(targetArea);
        return rangeClosed(1, targetArea.second.x).mapToObj(x -> rangeClosed(1, abs(targetArea.second.y)).mapToObj(y -> probelauncher.launch(new Velocity(x, y))).toList()).flatMap(List::stream)
                .filter(m -> probelauncher.isWithinTargetArea(m.probePosition)).max(Comparator.comparingInt(m -> m.maxY)).orElseThrow().maxY;
    }

    public long solvePart2(final TargetArea targetArea) { // 5200
        final Probelauncher probelauncher = new Probelauncher(targetArea);
        return rangeClosed(1, targetArea.second.x).mapToObj(x -> rangeClosed(-abs(targetArea.second.y), abs(targetArea.second.y)).mapToObj(y -> probelauncher.launch(new Velocity(x, y))).toList()).flatMap(List::stream)
                .filter(m -> probelauncher.isWithinTargetArea(m.probePosition)).count();
    }

    public static void main(String[] args) throws IOException {
        final TargetArea targetArea = Files.lines(Path.of("input.txt")).limit(1).map(line -> {
            final String[] split = line.replace("target area: ", "").split(", ");
            final String[] x = split[0].replace("x=", "").split("\\.\\.");
            final String[] y = split[1].replace("y=", "").split("\\.\\.");
            return new TargetArea(new Point(parseInt(x[0]), parseInt(y[1])), new Point(parseInt(x[1]), parseInt(y[0])));
        }).findFirst().orElseThrow();
        System.out.println((getenv("part") == null ? "part1" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1(targetArea) :
                new App().solvePart2(targetArea));
    }
}