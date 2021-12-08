import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.rangeClosed;
import static java.util.stream.Stream.concat;

public class App {
    record Signal(List<String> signalPatterns, List<String> outputs) {}
    private static class Display {
        final int digits;
        final Signal signal;

        Display(final Signal signal) {
            this.signal = signal;
            final Map<Integer, String> dictionary = rangeClosed(0, 9).mapToObj(key -> entry(key, "")).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
            do {
                signal.signalPatterns.forEach(signalPattern -> {
                    switch (signalPattern.length()) {
                        case 2 -> dictionary.put(1, signalPattern);
                        case 3 -> dictionary.put(7, signalPattern);
                        case 4 -> dictionary.put(4, signalPattern);
                        case 5 -> { // 2, 3, 5
                            if(signalIsValidFor(signalPattern, dictionary.get(1), 3))
                                dictionary.put(3, signalPattern);
                            else if(signalIsValidFor(signalPattern, dictionary.get(6), 1))
                                dictionary.put(5, signalPattern);
                            else if(signalIsValidFor(signalPattern, dictionary.get(3), 2))
                                dictionary.put(2, signalPattern);
                        }
                        case 6 -> { // 0, 6, 9
                            if(signalIsValidFor(signalPattern, dictionary.get(3), 1))
                                dictionary.put(9, signalPattern);
                            else if(signalIsValidFor(signalPattern, dictionary.get(7), 5))
                                dictionary.put(6, signalPattern);
                            else if(signalIsValidFor(signalPattern, dictionary.get(2), 3))
                                dictionary.put(0, signalPattern);
                        }
                        case 7 -> dictionary.put(8, signalPattern);
                        default -> throw new IllegalStateException();
                    }
                });
            } while(dictionary.values().stream().filter(value -> !value.isBlank()).count() < dictionary.size());
            digits = calculate(signal.outputs, dictionary);
        }

        private boolean signalIsValidFor(final String signal, final String pattern, final int matchingLength) {
            return !pattern.isBlank() && xor(pattern, signal).length() == matchingLength;
        }

        private int calculate(final List<String> output, final Map<Integer, String> dictionary) {
            return parseInt(output.stream().map(digit -> "" + dictionary.entrySet().stream().filter(entry -> xor(entry.getValue(), digit).length() == 0).findFirst().orElseThrow().getKey()).collect(joining()));
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
        final String part = getenv("part") == null ? "part1" : getenv("part");
        System.out.println(part.equalsIgnoreCase("part1") ? new App().solvePart1(displays) : new App().solvePart2(displays));
    }
}