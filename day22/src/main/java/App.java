import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    static class Cube {
        enum State { ON, OFF;}
        State state = State.OFF;
        final int x, y, z;
        Cube(final int x, final int y, final int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void setState(final State state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return "Cube{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    ", state=" + state +
                    '}';
        }
    }

    record RebootAction(Cube.State state, int fromX, int toX, int fromY, int toY, int fromZ, int toZ) {}

    static class Reactor {
        private final List<Cube> cubes;
        public Reactor() {
             cubes = rangeClosed(-50, 50).mapToObj(x -> rangeClosed(-50, 50)
                    .mapToObj(y -> rangeClosed(-50, 50).mapToObj(z -> new Cube(x, y, z)).toList())
                    .flatMap(List::stream).toList()).flatMap(List::stream).toList();
        }

        void applyRebootActions(final List<RebootAction> rebootActions) {
            rebootActions.forEach(a -> cubes.stream()
                    .filter(cube -> cube.x >= a.fromX && cube.x <= a.toX)
                    .filter(cube -> cube.y >= a.fromY && cube.y <= a.toY)
                    .filter(cube -> cube.z >= a.fromZ && cube.z <= a.toZ)
                    .forEach(cube -> cube.setState(a.state)));
        }
    }

    public long solvePart1(final List<RebootAction> rebootActions) { // 583636
        final Reactor reactor = new Reactor();
        reactor.applyRebootActions(rebootActions);
        return reactor.cubes.stream().filter(cube -> cube.state == Cube.State.ON).count();
    }

    public long solvePart2() { //
        return 0;
    }

    public static void main(String[] args) throws IOException {
        final List<RebootAction> rebootActions = Files.lines(Path.of("input.txt")).map(line -> {
            final String[] split = line.split(" ");
            final Cube.State state = Cube.State.valueOf(split[0].toUpperCase());

            final String[] dimensions = split[1].split(",");
            String[] x = dimensions[0].substring(2).split("\\.\\.");
            String[] y = dimensions[1].substring(2).split("\\.\\.");
            String[] z = dimensions[2].substring(2).split("\\.\\.");
            return new RebootAction(state, parseInt(x[0]), parseInt(x[1]),
                    parseInt(y[0]), parseInt(y[1]),
                    parseInt(z[0]), parseInt(z[1]));
        }).toList();
        System.out.println((getenv("part") == null ? "part1" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1(rebootActions) :
                new App().solvePart2());
    }
}