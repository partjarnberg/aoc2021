import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.lang.System.getenv;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;

public class App {
    record PairInsertionRule(String pattern, Character insert) {}
    record InsertInstruction(int index, Character insert) {}

    static class Polymer {
        List<Character> polymerTemplate;

        Polymer(final String polymerTemplate) {
            this.polymerTemplate = new LinkedList<>(polymerTemplate.chars().mapToObj(c -> (char) c).toList());
        }

        void applyRules(final List<PairInsertionRule> insertionRules) {
            final String currentPolymer = polymerTemplate.stream().map(String::valueOf).collect(joining());
            final AtomicInteger offset = new AtomicInteger();
            insertionRules.stream().map(rule -> {
                final List<Integer> indexes = new ArrayList<>();
                int index = 0, indexOffset = 0;
                while(index != -1){
                    index = currentPolymer.indexOf(rule.pattern, index + indexOffset);
                    if (index != -1) indexes.add(index);
                    indexOffset = 1;
                }
                return indexes.stream().map(indexOf -> new InsertInstruction(indexOf + 1, rule.insert)).toList();
            }).flatMap(List::stream).sorted(comparingInt(i -> i.index)).forEach(instruction ->
                    polymerTemplate.add(instruction.index + offset.getAndIncrement(), instruction.insert)
            );
        }

        Map<Character, Long> getOccurances() {
            return polymerTemplate.stream().collect(groupingBy(c -> c, counting()));
        }

        public String toString() {
            return polymerTemplate.stream().map(String::valueOf).collect(joining());
        }
    }

    public long solvePart1(final Polymer polymer, final List<PairInsertionRule> insertionRules) { // 2891
        IntStream.rangeClosed(1, 10).forEach(step -> polymer.applyRules(insertionRules));
        final Map.Entry<Character, Long> max = polymer.getOccurances().entrySet().stream().max(comparingLong(Map.Entry::getValue)).orElseThrow();
        final Map.Entry<Character, Long> min = polymer.getOccurances().entrySet().stream().min(comparingLong(Map.Entry::getValue)).orElseThrow();
        return max.getValue() - min.getValue();
    }

    public long solvePart2(final Polymer polymer, final List<PairInsertionRule> insertionRules) {
        IntStream.rangeClosed(1, 40).forEach(step -> {
            System.out.println("Step " + step);
            polymer.applyRules(insertionRules);
        });
        final Map.Entry<Character, Long> max = polymer.getOccurances().entrySet().stream().max(comparingLong(Map.Entry::getValue)).orElseThrow();
        final Map.Entry<Character, Long> min = polymer.getOccurances().entrySet().stream().min(comparingLong(Map.Entry::getValue)).orElseThrow();
        return max.getValue() - min.getValue();
    }

    public static void main(String[] args) throws IOException {
        final Polymer polymer = new Polymer(Files.lines(Path.of("input.txt")).limit(1).collect(joining()));
        final List<PairInsertionRule> insertionRules = Files.lines(Path.of("input.txt")).skip(2).map(line -> {
            final String[] split = line.split(" -> ");
            return new PairInsertionRule(split[0], split[1].charAt(0));
        }).toList();
        System.out.println((getenv("part") == null ? "part2" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1(polymer, insertionRules) :
                new App().solvePart2(polymer, insertionRules));
    }
}