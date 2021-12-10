import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.System.getenv;
import static java.util.Objects.requireNonNull;

public class App {

    final Map<Character, Integer> valueOfIllegalCharacter = Map.of(')', 3, ']', 57, '}', 1197, '>', 25137);
    final Map<Character, Integer> valueOfIncompleteCharacter = Map.of(')', 1, ']', 2, '}', 3, '>', 4);
    final Map<Character, Character> closingCharacterFor = Map.of('(', ')', '[', ']', '{', '}', '<', '>');

    boolean isAnOpeningCharacter(final Character character) {
        return closingCharacterFor.containsKey(character);
    }

    Optional<Character> firstIllegalCharacterOf(final String line) {
        final Deque<Character> stack = new ArrayDeque<>();
        return line.chars().mapToObj(c -> (char) c).map(c -> {
            if (isAnOpeningCharacter(c)) stack.push(c);
            else if (!closingCharacterFor.get(stack.pop()).equals(c)) return c;
            return null;
        }).filter(Objects::nonNull).findFirst();
    }

    List<Character> complete(final String line) {
        final Deque<Character> stack = new ArrayDeque<>();
        line.chars().mapToObj(c -> (char) c).forEach(c -> {
            if (isAnOpeningCharacter(c)) stack.push(c);
            else if(closingCharacterFor.get(requireNonNull(stack.peek())).equals(c)) stack.pop();
        });
        return stack.stream().map(closingCharacterFor::get).toList();
    }

    public long solvePart1(final List<String> lines) { // 296535
        return lines.parallelStream().map(this::firstIllegalCharacterOf).filter(Optional::isPresent).mapToInt(c -> valueOfIllegalCharacter.get(c.get())).sum();
    }

    public long solvePart2(final List<String> lines) { // 4245130838
        final List<Long> incompleteValues = lines.parallelStream().filter(line -> firstIllegalCharacterOf(line).isEmpty()).map(line ->
                complete(line).stream().mapToLong(valueOfIncompleteCharacter::get).reduce((a, b) -> 5 * a + b).orElseThrow()).sorted().toList();
        return incompleteValues.stream().skip(incompleteValues.size() / 2).findFirst().orElseThrow();
    }

    public static void main(String[] args) throws IOException {
        final List<String> lines = Files.lines(Path.of("input.txt")).toList();
        System.out.println((getenv("part") == null ? "part1" : getenv("part")).equalsIgnoreCase("part1") ? new App().solvePart1(lines) : new App().solvePart2(lines));
    }
}