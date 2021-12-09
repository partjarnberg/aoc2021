import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.System.getenv;
import static java.util.Arrays.stream;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.IntStream.range;

public class App {
    record Point(int x, int y) {}
    record Directions(List<List<Integer>> heightMap, int left, int right, int up, int down, int y, int x) {
        boolean isALowPoint() {
            return Stream.of(left, right, up, down).allMatch(neighbor -> heightMap.get(y).get(x) < neighbor);
        }
    }

    public long solvePart1(final List<List<Integer>> heightMap) {
        return range(0, heightMap.size()).mapToObj(y -> range(0, heightMap.get(0).size()).filter(x ->
                calculateDirections(heightMap, y, x).isALowPoint()).mapToLong(x -> heightMap.get(y).get(x) + 1))
                .flatMapToLong(longStream -> longStream).sum();
    }

    public long solvePart2(final List<List<Integer>> heightMap) { // 1059300
        final List<Long> longs = range(0, heightMap.size()).mapToObj(y -> range(0, heightMap.get(0).size()).filter(x ->
                calculateDirections(heightMap, y, x).isALowPoint())
                .mapToLong(x -> sizeOfBasin(heightMap, new ArrayList<>(), y, x)))
                .flatMapToLong(longStream -> longStream).boxed().sorted(reverseOrder()).toList();
        return longs.get(0) * longs.get(1) * longs.get(2);
    }

    private Directions calculateDirections(final List<List<Integer>> heightMap, final int y, final int x) {
        int left = x - 1 >= 0 ? heightMap.get(y).get(x - 1) : Integer.MAX_VALUE;
        int right = x + 1 < heightMap.get(0).size() ? heightMap.get(y).get(x + 1) : Integer.MAX_VALUE;
        int up = y - 1 >= 0 ? heightMap.get(y - 1).get(x) : Integer.MAX_VALUE;
        int down = y + 1 < heightMap.size() ? heightMap.get(y + 1).get(x) : Integer.MAX_VALUE;
        return new Directions(heightMap, left, right, up, down, y, x);
    }

    private long sizeOfBasin(final List<List<Integer>> heightMap, final List<Point> visited, final int y, final int x) {
        if(visited.contains(new Point(x, y)) || heightMap.get(y).get(x) == 9)
            return 0;
        long size = 1;
        visited.add(new Point(x, y));
        if(x > 0) size += sizeOfBasin(heightMap, visited, y, x - 1);
        if(x < heightMap.get(0).size() - 1) size += sizeOfBasin(heightMap, visited, y, x + 1);
        if(y > 0) size += sizeOfBasin(heightMap, visited, y - 1, x);
        if(y < heightMap.size() - 1) size += sizeOfBasin(heightMap, visited, y + 1, x);
        return size;
    }

    public static void main(String[] args) throws IOException {
        final List<List<Integer>> heightMap = Files.lines(Path.of("input.txt")).map(line ->
                        stream(line.split("")).mapToInt(Integer::parseInt).boxed().toList())
                .toList();
        final String part = getenv("part") == null ? "part1" : getenv("part");
        System.out.println(part.equalsIgnoreCase("part1") ? new App().solvePart1(heightMap) : new App().solvePart2(heightMap));
    }
}