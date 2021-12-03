package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.util.List.of;
import static java.util.stream.Collectors.reducing;

public class App {
    private record Command(String direction, int amount) {}

    public Integer getSolutionPart1(final List<Command> input) { // 2091984
        final Map<String, Integer> sums = input.stream().collect(Collectors.groupingBy(
                Command::direction, reducing(0, c -> switch (c.direction) {
                    case "up" -> -c.amount;
                    case "down", "forward" -> c.amount;
                    default -> throw new IllegalStateException("Unexpected value");
                }, Integer::sum)
        ));
        return sums.get("forward") * sums.entrySet().stream().filter(entry -> of("up", "down").contains(entry.getKey()))
                .map(Map.Entry::getValue).reduce(0, Integer::sum);
    }

    public Integer getSolutionPart2(final List<Command> input) { // 2086261056
        final int[] aim = new int[1], depth = new int[1], horizontal = new int[1];
        input.forEach(c -> {
            switch (c.direction) {
                case "up" -> aim[0] -= c.amount;
                case "down" -> aim[0] += c.amount;
                case "forward" -> { horizontal[0] += c.amount; depth[0] += aim[0] * c.amount; }
            }
        });
        return horizontal[0] * depth[0];
    }

    public static void main(String[] args) throws IOException {
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part2"))
            System.out.println(new App().getSolutionPart2(parseInput()));
        else
            System.out.println(new App().getSolutionPart1(parseInput()));
    }

    private static List<Command> parseInput() throws IOException {
        return Files.lines(Path.of("input.txt")).map(line -> line.split(" "))
                .map(a -> new Command(a[0], parseInt(a[1]))).collect(Collectors.toList());
    }
}