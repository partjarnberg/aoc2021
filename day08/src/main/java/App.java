import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

public class App {
    record Signal(List<String> segmentPatterns, List<String> outputs) {}
    private static class Display {
        final int digits;
        final Signal signal;

        Display(final Signal signal) {
            this.signal = signal;
            final String[] segments = new String[10];
            do {
                signal.segmentPatterns.forEach(pattern -> {
                    switch (pattern.length()) {
                        case 2 -> segments[1] = pattern;
                        case 3 -> segments[7] = pattern;
                        case 4 -> segments[4] = pattern;
                        case 5 -> { // 2, 3, 5
                            if(signalMatches(pattern, segments[1], 3))
                                segments[3] = pattern;
                            else if(signalMatches(pattern, segments[6], 1))
                                segments[5] = pattern;
                            else if(signalMatches(pattern, segments[3], 2))
                                segments[2] = pattern;
                        }
                        case 6 -> { // 0, 6, 9
                            if(signalMatches(pattern, segments[3], 1))
                                segments[9] = pattern;
                            else if(signalMatches(pattern, segments[7], 5))
                                segments[6] = pattern;
                            else if(signalMatches(pattern, segments[2], 3))
                                segments[0] = pattern;
                        }
                        case 7 -> segments[8] = pattern;
                        default -> throw new IllegalStateException();
                    }
                });
            } while(stream(segments).filter(Objects::nonNull).count() < segments.length);
            digits = calculate(signal.outputs, segments);
        }

        private boolean signalMatches(final String signal, final String pattern, final int matchingLength) {
            return nonNull(pattern) && xor(pattern, signal).length() == matchingLength;
        }

        private int calculate(final List<String> output, final String[] segments) {
            return parseInt(output.stream().map(digit -> "" + IntStream.rangeClosed(0, 9).filter(index -> xor(digit, segments[index]).isEmpty()).findFirst().orElseThrow()).collect(joining()));
        }

        private String xor(final String first, final String second){
            return concat(stream(first.split("")).filter(c -> !second.contains(c)), stream(second.split("")).filter(c -> !first.contains(c))).collect(joining());
        }
    }

    public long solvePart1(final List<Display> displays) {
        return displays.stream().mapToLong(display -> display.signal.outputs.stream().filter(digit -> asList(2, 3, 4, 7).contains(digit.length())).count()).sum();
    }

    public int solvePart2(final List<Display> displays) {
        return displays.stream().mapToInt(display -> display.digits).sum();
    }

    public static void main(String[] args) throws IOException {
        final List<Display> displays = Files.lines(Path.of("input.txt")).map(line -> {
            final String[] split = line.split(" \\| ");
            return new Display(new Signal(asList(split[0].split(" ")), asList(split[1].split(" "))));
        }).toList();
        final String part = getenv("part") == null ? "part2" : getenv("part");
        System.out.println(part.equalsIgnoreCase("part1") ? new App().solvePart1(displays) : new App().solvePart2(displays));
    }
}