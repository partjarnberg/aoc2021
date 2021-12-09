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
    public long solvePart1(final List<List<Integer>> heightMap) {
        return range(0, heightMap.size()).mapToObj(y -> range(0, heightMap.get(0).size()).filter(x -> {
            int currentHeight = heightMap.get(y).get(x);
            int left = x - 1 >= 0 ? heightMap.get(y).get(x - 1) : Integer.MAX_VALUE;
            int right = x + 1 < heightMap.get(0).size() ? heightMap.get(y).get(x + 1) : Integer.MAX_VALUE;
            int up = y - 1 >= 0 ? heightMap.get(y - 1).get(x) : Integer.MAX_VALUE;
            int down = y + 1 < heightMap.size() ? heightMap.get(y + 1).get(x) : Integer.MAX_VALUE;
            return Stream.of(left, right, up, down).allMatch(neighbor -> currentHeight < neighbor);
        }).mapToLong(x -> heightMap.get(y).get(x) + 1)).flatMapToLong(longStream -> longStream).sum();
    }

     long sizeOfBasin(final List<List<Integer>> heightMap, final List<Point> visited, final int y, final int x, long size) {
         int currentHeight = heightMap.get(y).get(x);
         visited.add(new Point(x, y));
         int left = x - 1 >= 0 ? heightMap.get(y).get(x - 1) : Integer.MAX_VALUE;
         int right = x + 1 < heightMap.get(0).size() ? heightMap.get(y).get(x + 1) : Integer.MAX_VALUE;
         int up = y - 1 >= 0 ? heightMap.get(y - 1).get(x) : Integer.MAX_VALUE;
         int down = y + 1 < heightMap.size() ? heightMap.get(y + 1).get(x) : Integer.MAX_VALUE;

         if(left < 9 && currentHeight < left && !visited.contains(new Point(x - 1, y))) {
             size = sizeOfBasin(heightMap, visited, y, x - 1, size + 1);
         }
         if(right < 9 && currentHeight < right && !visited.contains(new Point(x + 1, y))) {
             size = sizeOfBasin(heightMap, visited, y, x + 1, size + 1);
         }
         if(up < 9 && currentHeight < up && !visited.contains(new Point(x, y - 1))) {
             size = sizeOfBasin(heightMap, visited, y - 1, x, size + 1);
         }
         if(down < 9 && currentHeight < down && !visited.contains(new Point(x, y + 1))) {
             size = sizeOfBasin(heightMap, visited, y + 1, x, size + 1);
         }

         return size;
    }

    public long solvePart2(final List<List<Integer>> heightMap) { // 1059300
        final List<Long> longs = range(0, heightMap.size()).mapToObj(y -> range(0, heightMap.get(0).size()).filter(x -> {
            int currentHeight = heightMap.get(y).get(x);
            int left = x - 1 >= 0 ? heightMap.get(y).get(x - 1) : Integer.MAX_VALUE;
            int right = x + 1 < heightMap.get(0).size() ? heightMap.get(y).get(x + 1) : Integer.MAX_VALUE;
            int up = y - 1 >= 0 ? heightMap.get(y - 1).get(x) : Integer.MAX_VALUE;
            int down = y + 1 < heightMap.size() ? heightMap.get(y + 1).get(x) : Integer.MAX_VALUE;
            return Stream.of(left, right, up, down).allMatch(neighbor -> currentHeight < neighbor);
        }).mapToLong(x -> sizeOfBasin(heightMap, new ArrayList<>(), y, x, 1))).flatMapToLong(longStream -> longStream).boxed().sorted(reverseOrder()).toList();
        return longs.get(0) * longs.get(1) * longs.get(2);
    }

    public static void main(String[] args) throws IOException {
        final List<List<Integer>> heightMap = Files.lines(Path.of("input.txt")).map(line ->
                        stream(line.split("")).mapToInt(Integer::parseInt).boxed().toList())
                .toList();
        final String part = getenv("part") == null ? "part2" : getenv("part");
        System.out.println(part.equalsIgnoreCase("part1") ? new App().solvePart1(heightMap) : new App().solvePart2(heightMap));
    }
}