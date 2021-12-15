import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.lang.System.getenv;
import static java.util.Comparator.comparingLong;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    record PairInsertionRule(String pair, String firstNewPair, String secondNewPair) {}

    static class Polymer {
        final Map<String, Long> polymerTemplate = new HashMap<>();

        Polymer(final String polymerTemplate) {
            IntStream.range(0, polymerTemplate.length() - 1).forEach(index -> {
                final String pair = polymerTemplate.substring(index, index + 2);
                this.polymerTemplate.put(pair, this.polymerTemplate.getOrDefault(pair, 0L) + 1);
            });
        }

        void applyRules(final List<PairInsertionRule> insertionRules) {
            final Map<String, Long> polymerInserts = new HashMap<>();
            insertionRules.forEach(rule -> {
                final long count = polymerTemplate.getOrDefault(rule.pair, 0L);
                polymerInserts.put(rule.firstNewPair, polymerInserts.getOrDefault(rule.firstNewPair, 0L) + count);
                polymerInserts.put(rule.secondNewPair, polymerInserts.getOrDefault(rule.secondNewPair, 0L) + count);
                polymerInserts.put(rule.pair, polymerInserts.getOrDefault(rule.pair, 0L) - count);
            });
            polymerInserts.forEach((key, value) -> {
                long previousValue = ofNullable(polymerTemplate.get(key)).orElse(0L);
                if(previousValue + value <= 0)
                    polymerTemplate.remove(key);
                else polymerTemplate.put(key, previousValue + value);
            });
        }

        Map<Character, Long> getOccurances() {
            final Map<Character, Long> charCounts = new HashMap<>();
            polymerTemplate.forEach((key, value) -> {
                if(charCounts.containsKey(key.charAt(0)))
                    charCounts.put(key.charAt(0), charCounts.get(key.charAt(0)) + value);
                else
                    charCounts.put(key.charAt(0), value);
            });
            return charCounts;
        }
    }

    public long solvePart1(final Polymer polymer, final List<PairInsertionRule> insertionRules) { // 2891
        rangeClosed(1, 10).forEach(step -> polymer.applyRules(insertionRules));
        final Map.Entry<Character, Long> max = polymer.getOccurances().entrySet().stream().max(comparingLong(Map.Entry::getValue)).orElseThrow();
        final Map.Entry<Character, Long> min = polymer.getOccurances().entrySet().stream().min(comparingLong(Map.Entry::getValue)).orElseThrow();
        return max.getValue() - min.getValue();
    }

    public long solvePart2(final Polymer polymer, final List<PairInsertionRule> insertionRules) { // 4607749009683
        rangeClosed(1, 40).forEach(step -> polymer.applyRules(insertionRules));
        final Map.Entry<Character, Long> max = polymer.getOccurances().entrySet().stream().max(comparingLong(Map.Entry::getValue)).orElseThrow();
        final Map.Entry<Character, Long> min = polymer.getOccurances().entrySet().stream().min(comparingLong(Map.Entry::getValue)).orElseThrow();
        return max.getValue() - min.getValue();
    }

    public static void main(String[] args) throws IOException {
        final Polymer polymer = new Polymer(Files.lines(Path.of("input.txt")).limit(1).collect(joining()));
        final List<PairInsertionRule> insertionRules = Files.lines(Path.of("input.txt")).skip(2).map(line -> {
            final String[] split = line.split(" -> ");
            final String pair = split[0]; final char insertElement = split[1].charAt(0);
            return new PairInsertionRule(pair, "" + pair.charAt(0) + insertElement, "" + insertElement + pair.charAt(1));
        }).toList();
        System.out.println((getenv("part") == null ? "part1" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1(polymer, insertionRules) :
                new App().solvePart2(polymer, insertionRules));
    }
}