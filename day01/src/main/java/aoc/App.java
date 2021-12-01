package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    private final List<Integer> input;

    public App(List<Integer> input) {
        this.input = input;
    }

    public Integer getSolutionPart1() { // 1387
        int prev = input.get(0), increased = 0;
        for(int i = 1; i < input.size(); i++) {
            if(input.get(i) > prev)
                increased++;
            prev = input.get(i);
        }
        return increased;
    }

    public Integer getSolutionPart2() { // 1362
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
        System.out.println("java");
        List<Integer> input = parseInput("input.txt");
        String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part2"))
            System.out.println(new App(input).getSolutionPart2());
        else
            System.out.println(new App(input).getSolutionPart1());
    }

    private static List<Integer> parseInput(String filename) throws IOException {
        return Files.lines(Path.of(filename))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
