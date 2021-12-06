import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.rotate;
import static java.util.stream.IntStream.range;

public class App {

    private int usingWindow(final List<Integer> input, int windowSize) {
        return range(0, input.size()).map(x -> {
            final int previousWindow = range(0, windowSize).map(input::get).sum();
            rotate(input, -1);
            final int currentWindow = range(0, windowSize).map(input::get).sum();
            return currentWindow > previousWindow ? 1 : 0;
        }).sum();
    }

    public Integer getSolutionPart1(final List<Integer> input) { // 1387
        return usingWindow(input, 1);
    }

    public Integer getSolutionPart2(final List<Integer> input) { // 1362
        return usingWindow(input, 3);
    }

    public static void main(String[] args) throws IOException {
        final List<Integer> input = Files.lines(Path.of("input.txt")).map(Integer::parseInt).collect(Collectors.toList());
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part2"))
            System.out.println(new App().getSolutionPart2(input));
        else
            System.out.println(new App().getSolutionPart1(input));
    }
}