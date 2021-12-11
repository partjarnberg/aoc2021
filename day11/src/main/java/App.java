import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.System.getenv;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

public class App {

    private static class Octopus {
        final static int FLASHING_THRESHOLD = 9;
        int energyLevel; boolean flashed = false;
        final List<Octopus> neighbors = new ArrayList<>();

        Octopus(final int energyLevel) {
            this.energyLevel = energyLevel;
        }

        void addNeighbor(final Octopus octopus) {
            neighbors.add(octopus);
        }

        void tick() {
            energyLevel++;
        }

        void tryToFlash() {
            if(!flashed && energyLevel > FLASHING_THRESHOLD) {
                flashed = true;
                neighbors.forEach(neighbor -> {
                    neighbor.tick(); neighbor.tryToFlash();
                });
            }
        }

        boolean reset() {
            if(energyLevel > FLASHING_THRESHOLD) energyLevel = 0;
            try {
                return flashed;
            } finally {
                flashed = false;
            }
        }
    }

    public long solvePart1(final List<Octopus> octopi) { // 1562
        return rangeClosed(1, 100).mapToLong(step -> {
            octopi.forEach(Octopus::tick);
            octopi.forEach(Octopus::tryToFlash);
            return octopi.stream().filter(Octopus::reset).count();
        }).sum();
    }

    public long solvePart2(final List<Octopus> octopi) { // 268
        return rangeClosed(1, MAX_VALUE).takeWhile(ignore -> octopi.stream().filter(Octopus::reset).count() != octopi.size()).mapToObj(step -> {
            octopi.forEach(Octopus::tick);
            octopi.forEach(Octopus::tryToFlash);
            return octopi.stream().filter(Octopus::reset).count() == octopi.size() ? step : null;
        }).filter(Objects::nonNull).findFirst().orElseThrow();
    }

    public static void main(String[] args) throws IOException {
        final Octopus[][] octopuses = Files.lines(Path.of("input.txt")).map(line -> stream(line.split("")).map(Integer::parseInt).map(Octopus::new).toArray(Octopus[]::new)).toArray(Octopus[][]::new);
        range(0, octopuses.length).forEach(y -> range(0, octopuses[0].length)
                .forEach(x -> {
                    final Octopus curr = octopuses[y][x];
                    // Neighbors above
                    if(y > 0) curr.addNeighbor(octopuses[y - 1][x]);
                    if(y > 0 && x > 0) curr.addNeighbor(octopuses[y - 1][x - 1]);
                    if(y > 0 && x < octopuses[0].length - 1) curr.addNeighbor(octopuses[y - 1][x + 1]);
                    // Neighbors on each side
                    if(x > 0) curr.addNeighbor(octopuses[y][x - 1]);
                    if(x < octopuses[0].length - 1) curr.addNeighbor(octopuses[y][x + 1]);
                    // Neighbors below
                    if(y < octopuses.length - 1 && x > 0) curr.addNeighbor(octopuses[y + 1][x - 1]);
                    if(y < octopuses.length - 1 && x < octopuses[0].length - 1) curr.addNeighbor(octopuses[y + 1][x + 1]);
                    if(y < octopuses.length - 1) curr.addNeighbor(octopuses[y + 1][x]);
                }));
        System.out.println((getenv("part") == null ? "part1" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1(stream(octopuses).flatMap(Arrays::stream).toList()) :
                new App().solvePart2(stream(octopuses).flatMap(Arrays::stream).toList()));
    }
}