import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.getenv;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

public class App {
    record Display(List<String> signalPatterns, List<String> output) {}


    public long solvePart1(final List<Display> displays) {
        return displays.stream().mapToLong(display -> display.output.stream().filter(digit -> asList(2, 3, 4, 7).contains(digit.length())).count()).sum();
    }

    public int solvePart2(final List<Display> displays) {
        return displays.stream().mapToInt(display -> {
            final Map<Integer, String> dictionary = new HashMap<>(ofEntries(
                    entry(0, ""), entry(1, ""), entry(2, ""), entry(3, ""), entry(4, ""),
                    entry(5, ""), entry(6, ""), entry(7, ""), entry(8, ""), entry(9, "")));
            do {
                display.signalPatterns.forEach(signal -> {
                    switch (signal.length()) {
                        case 2 -> dictionary.put(1, signal);
                        case 3 -> dictionary.put(7, signal);
                        case 4 -> dictionary.put(4, signal);
                        case 5 -> { // 2, 3, 5
                            if(!dictionary.get(1).isBlank() && xor(dictionary.get(1), signal).length() == 3)
                                dictionary.put(3, signal);
                            else if(!dictionary.get(4).isBlank() && !dictionary.get(7).isBlank()) {
                                if(xor(xor(dictionary.get(4), dictionary.get(7)), signal).length() == 2)
                                    dictionary.put(5, signal);
                                if(xor(xor(dictionary.get(4), dictionary.get(7)), signal).length() == 4)
                                    dictionary.put(2, signal);
                            }
                        }
                        case 6 -> { // 0, 6, 9
                            if(!dictionary.get(3).isBlank() && xor(dictionary.get(3), signal).length() == 1)
                                dictionary.put(9, signal);
                            else if(!dictionary.get(7).isBlank() && xor(dictionary.get(7), signal).length() == 5)
                                dictionary.put(6, signal);
                            else if(!dictionary.get(2).isBlank() && xor(dictionary.get(2), signal).length() == 3)
                                dictionary.put(0, signal);
                        }
                        case 7 -> dictionary.put(8, signal);
                        default -> throw new IllegalStateException();
                    }
                });
            } while(dictionary.values().stream().filter(value -> !value.isBlank()).count() < 10);
            return Integer.parseInt(display.output.stream().map(digit -> "" + dictionary.entrySet().stream()
                    .filter(entry -> xor(entry.getValue(), digit).length() == 0).findFirst().orElseThrow().getKey())
                    .collect(joining()));
        }).sum();
    }

    public String xor(final String first, final String second){
        return concat(stream(first.split("")).filter(c -> !second.contains(c)),
                stream(second.split("")).filter(c -> !first.contains(c)))
                .collect(joining());
    }

    public static void main(String[] args) throws IOException {
        final List<Display> signalPatterns = Files.lines(Path.of("input.txt")).map(line -> {
            final String[] split = line.split(" \\| ");
            return new Display(asList(split[0].split(" ")), asList(split[1].split(" ")));
        }).toList();
        final String part = getenv("part") == null ? "part1" : getenv("part");
        System.out.println(part.equalsIgnoreCase("part1") ? new App().solvePart1(signalPatterns) : new App().solvePart2(signalPatterns));
    }
}