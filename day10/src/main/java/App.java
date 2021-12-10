import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.System.getenv;
import static java.util.List.of;
import static java.util.Objects.requireNonNull;

public class App {

    boolean isAnOpeningCharacter(final Character character) {
        return of('(', '[', '{', '<').contains(character);
    }

    int valueOfIllegalCharacter(final Character closing) {
        switch (closing) {
            case ')' -> { return 3; }
            case ']' -> { return 57; }
            case '}' -> { return 1197; }
            case '>' -> { return 25137; }
            default -> throw new IllegalStateException();
        }
    }

    int valueOfIncompleteCharacter(final Character closing) {
        switch (closing) {
            case ')' -> { return 1; }
            case ']' -> { return 2; }
            case '}' -> { return 3; }
            case '>' -> { return 4; }
            default -> throw new IllegalStateException();
        }
    }

    Character closingCharacterFor(final Character opening) {
        switch (opening) {
            case '(' -> { return ')'; }
            case '[' -> { return ']'; }
            case '{' -> { return '}'; }
            case '<' -> { return '>'; }
            default -> throw new IllegalStateException();
        }
    }

    Optional<Character> firstIllegalCharacter(final String line) {
        final Deque<Character> stack = new ArrayDeque<>();
        return line.chars().mapToObj(c -> (char) c).map(c -> {
            if (isAnOpeningCharacter(c)) stack.push(c);
            else if (!closingCharacterFor(stack.pop()).equals(c)) return c;
            return null;
        }).filter(Objects::nonNull).findFirst();
    }

    List<Character> complete(final String line) {
        final Deque<Character> stack = new ArrayDeque<>();
        line.chars().mapToObj(c -> (char) c).forEach(c -> {
            if (isAnOpeningCharacter(c)) stack.push(c);
            else if(closingCharacterFor(requireNonNull(stack.peek())).equals(c)) stack.pop();
        });
        return stack.stream().map(this::closingCharacterFor).toList();
    }

    public long solvePart1(final List<String> lines) { // 296535
        return lines.stream().map(this::firstIllegalCharacter).filter(Optional::isPresent).mapToInt(c -> valueOfIllegalCharacter(c.get())).sum();
    }

    public long solvePart2(final List<String> lines) { // 4245130838
        final List<String> inComplete = lines.stream().filter(line -> firstIllegalCharacter(line).isEmpty()).toList();
        return inComplete.stream().map(line -> complete(line).stream().mapToLong(this::valueOfIncompleteCharacter).reduce((a, b) -> 5 * a + b).orElseThrow())
                .sorted().skip(inComplete.size() / 2).findFirst().orElseThrow();
    }

    public static void main(String[] args) throws IOException {
        final List<String> lines = Files.lines(Path.of("input.txt")).toList();
        System.out.println((getenv("part") == null ? "part1" : getenv("part")).equalsIgnoreCase("part1") ? new App().solvePart1(lines) : new App().solvePart2(lines));
    }
}