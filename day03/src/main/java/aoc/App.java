package aoc;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

public class App {
    public Integer getSolutionPart1() throws IOException { // 3242606
        int[] gammaRate = new int[12], epsilonRate = new int[12];
        var result = new Object() {
            int noofRows = 0;
            final int[] bitOccur = new int[12];
        };
        Files.lines(Path.of("input.txt")).forEach(binaryString -> {
            result.noofRows++;
            char[] chars = binaryString.toCharArray();
            range(0, result.bitOccur.length).forEach(i -> {
                result.bitOccur[i] += Character.getNumericValue(chars[i]);
            });
        });

        range(0, result.bitOccur.length).forEach(i -> {
            if(result.bitOccur[i] > result.noofRows/2) {
                gammaRate[i] = 1;
                epsilonRate[i] = 0;
            } else {
                gammaRate[i] = 0;
                epsilonRate[i] = 1;
            }
        });

        return parseInt(stream(gammaRate).mapToObj(i -> i + "").reduce("", (s, s2) -> s + s2), 2) *
                parseInt(stream(epsilonRate).mapToObj(i -> i + "").reduce("", (s, s2) -> s + s2), 2);
    }

    public Integer getSolutionPart2() throws IOException { // 4856080
        final List<int[]> reports = Files.lines(Path.of("input.txt")).map(binaryString ->
                CharBuffer.wrap(binaryString.toCharArray()).chars().mapToObj(ch -> (char) ch)
                        .mapToInt(Character::getNumericValue).toArray()).collect(Collectors.toList());

        final AtomicReference<List<int[]>> oxygenRating = new AtomicReference<>(List.copyOf(reports));
        range(0, reports.get(0).length).takeWhile(ignore -> oxygenRating.get().size() > 1).forEach(i -> {
            long ones = oxygenRating.get().stream().filter(ints -> ints[i] == 1).count();
            long zeros = oxygenRating.get().stream().filter(ints -> ints[i] == 0).count();
            oxygenRating.set(oxygenRating.get().stream().filter(ints -> {
                if (ones >= zeros) {
                    return ints[i] == 1;
                }
                return ints[i] == 0;
            }).toList());
        });

        final AtomicReference<List<int[]>> co2Rating = new AtomicReference<>(List.copyOf(reports));
        range(0, reports.get(0).length).takeWhile(ignore -> co2Rating.get().size() > 1).forEach(i -> {
            long ones = co2Rating.get().stream().filter(ints -> ints[i] == 1).count();
            long zeros = co2Rating.get().stream().filter(ints -> ints[i] == 0).count();
            co2Rating.set(co2Rating.get().stream().filter(ints -> {
                if (zeros <= ones) {
                    return ints[i] == 0;
                }
                return ints[i] == 1;
            }).toList());
        });
        assert co2Rating.get().size() == 1;

        return parseInt(stream(oxygenRating.get().get(0)).mapToObj(i -> i + "").reduce("", (s, s2) -> s + s2), 2) *
                parseInt(stream(co2Rating.get().get(0)).mapToObj(i -> i + "").reduce("", (s, s2) -> s + s2), 2);
    }

    public static void main(String[] args) throws IOException {
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part1"))
            System.out.println(new App().getSolutionPart2());
        else
            System.out.println(new App().getSolutionPart1());
    }
}