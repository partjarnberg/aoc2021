package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    public Integer getSolutionPart1(final List<Integer> input) { // 1387
        final int[] increased = { 0 };
        input.stream().reduce((a, b) -> {
            if(a != 0 && b > a)
                increased[0]++;
            return b;
        });
        return increased[0];
    }

    public Integer getSolutionPart2(final List<Integer> input) { // 1362
        int prev = input.get(0) + input.get(1) + input.get(2), increased = 0;
        for(int i = 2; i < input.size(); i++) {
            int currentWindow = input.get(i - 2) + input.get(i - 1) + input.get(i);
            if(currentWindow > prev)
                increased++;
            prev = currentWindow;
        }
        return increased;
    }

    public static void main(String[] args) throws IOException {
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part2"))
            System.out.println(new App().getSolutionPart2(parseInput()));
        else
            System.out.println(new App().getSolutionPart1(parseInput()));
    }

    private static List<Integer> parseInput() throws IOException {
        return Files.lines(Path.of("input.txt")).map(Integer::parseInt).collect(Collectors.toList());
    }
}
