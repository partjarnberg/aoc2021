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
    public Integer getSolutionPart1(List<int[]> report) { // 3242606
        final int[] bitOccur = new int[report.get(0).length], gammaRate = new int[report.get(0).length], epsilonRate = new int[report.get(0).length];
        report.forEach(ints -> range(0, bitOccur.length).forEach(i -> bitOccur[i] += ints[i]));
        range(0, bitOccur.length).forEach(i -> {
            if(bitOccur[i] > report.size()/2) {
                gammaRate[i] = 1; epsilonRate[i] = 0;
            } else {
                gammaRate[i] = 0; epsilonRate[i] = 1;
            }
        });
        return parseInt(stream(gammaRate).mapToObj(i -> i + "").reduce("", (s, s2) -> s + s2), 2) *
                parseInt(stream(epsilonRate).mapToObj(i -> i + "").reduce("", (s, s2) -> s + s2), 2);
    }

    public Integer getSolutionPart2(List<int[]> report) { // 4856080
        return parseInt(stream(getCandidate(report, true)).mapToObj(i -> i + "").reduce("", (s, s2) -> s + s2), 2) *
                parseInt(stream(getCandidate(report, false)).mapToObj(i -> i + "").reduce("", (s, s2) -> s + s2), 2);
    }

    private int[] getCandidate(final List<int[]> report, boolean oxygen) {
        final AtomicReference<List<int[]>> candidates = new AtomicReference<>(List.copyOf(report));
        range(0, report.get(0).length).takeWhile(ignore -> candidates.get().size() > 1).forEach(i -> {
            long ones = candidates.get().stream().filter(ints -> ints[i] == 1).count();
            long zeros = candidates.get().stream().filter(ints -> ints[i] == 0).count();
            candidates.set(candidates.get().stream().filter(ints -> ones < zeros == oxygen ? ints[i] == 0 : ints[i] == 1).toList());
        });
        if(candidates.get().size() > 1) throw new IllegalStateException();
        return candidates.get().get(0);
    }

    public static void main(String[] args) throws IOException {
        final List<int[]> report = Files.lines(Path.of("input.txt")).map(binaryString ->
                CharBuffer.wrap(binaryString.toCharArray()).chars().mapToObj(ch -> (char) ch)
                        .mapToInt(Character::getNumericValue).toArray()).collect(Collectors.toList());
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part2"))
            System.out.println(new App().getSolutionPart2(report));
        else
            System.out.println(new App().getSolutionPart1(report));
    }
}