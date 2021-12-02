package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class App {
    private record Command(String direction, int amount) {}

    private final List<Command> input;

    public App(List<Command> input) {
        this.input = input;
    }

    public Integer getSolutionPart1() { // 2091984
        final Integer horizontal = input.parallelStream()
                .filter(c -> "forward".equalsIgnoreCase(c.direction))
                .map(c -> c.amount).reduce(0, Integer::sum);
        final Integer depth = input.parallelStream()
                .filter(c -> Set.of("down", "up").contains(c.direction))
                .map(c -> switch (c.direction) {
                    case "up" -> -c.amount;
                    case "down" -> c.amount;
                    default -> 0;
                }).reduce(0, Integer::sum);
        return horizontal * depth;
    }

    public Integer getSolutionPart2() { // 2086261056
        int aim = 0;
        int depth = 0;
        int horizontal = 0;
        for (Command command : input) {
            switch (command.direction) {
                case "up" -> {
                    aim -= command.amount;
                }
                case "down" -> {
                    aim += command.amount;
                }
                case "forward" -> {
                    horizontal += command.amount;
                    depth += aim * command.amount;
                }
            }
        }
        return horizontal * depth;
    }

    public static void main(String[] args) throws IOException {
        final List<Command> input = parseInput("input.txt");
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part2"))
            System.out.println(new App(input).getSolutionPart2());
        else
            System.out.println(new App(input).getSolutionPart1());
    }

    private static List<Command> parseInput(String filename) throws IOException {
        return Files.lines(Path.of(filename))
                .map(s -> {
                    final String[] split = s.split(" ");
                    return new Command(split[0], parseInt(split[1]));
                })
                .collect(Collectors.toList());
    }
}

