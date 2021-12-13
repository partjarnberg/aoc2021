import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.System.getenv;
import static java.util.List.copyOf;
import static java.util.List.of;

public class App {
    enum Strategy {FIRST, SECOND}
    static class Cave {
        enum Size { BIG, SMALL }

        final String name;
        final Size size;
        Set<Cave> connectedTo = new HashSet<>();

        public Cave(final String name) {
            this.name = name;
            this.size = name.equals(name.toUpperCase()) ? Size.BIG : Size.SMALL;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    List<List<Cave>> visitCaveWithSmallRevisit(final Cave current, final Cave end, final Map<Cave, Integer> visited, final Deque<Cave> path, final List<List<Cave>> paths, final Strategy strategy) {
        path.addLast(current);
        if(current.size == Cave.Size.SMALL) visited.put(current, visited.getOrDefault(current, 0) + 1);
        if(current.equals(end)) paths.add(copyOf(path));

        switch (strategy) {
            case FIRST -> {
                current.connectedTo.forEach(cave -> {
                    if(!visited.containsKey(cave)) visitCaveWithSmallRevisit(cave, end, visited, path, paths, strategy);
                });
                visited.remove(current);
            }
            case SECOND -> {
                current.connectedTo.forEach(cave -> {
                    if(!visited.containsKey(cave)) visitCaveWithSmallRevisit(cave, end, visited, path, paths, strategy);
                    else if(!of("start", "end").contains(cave.name) && !visited.containsValue(2)) visitCaveWithSmallRevisit(cave, end, visited, path, paths, strategy);
                });
                if(visited.containsKey(current) && visited.get(current) > 1) visited.put(current, 1);
                else visited.remove(current);
            }
        }
        path.removeLastOccurrence(current);

        return paths;
    }

    public long solvePart1(final Cave start, final Cave end) { // 3856
        return visitCaveWithSmallRevisit(start, end, new HashMap<>(), new ArrayDeque<>(), new ArrayList<>(), Strategy.FIRST).size();
    }

    public long solvePart2(final Cave start, final Cave end) { // 116692
        return visitCaveWithSmallRevisit(start, end, new HashMap<>(), new ArrayDeque<>(), new ArrayList<>(), Strategy.SECOND).size();
    }

    public static void main(String[] args) throws IOException {
        final Map<String, Cave> caves = new HashMap<>();
        Files.lines(Path.of("input.txt")).forEach(line -> {
            final String[] split = line.split("-");
            final Cave first = caves.getOrDefault(split[0], new Cave(split[0]));
            final Cave second = caves.getOrDefault(split[1], new Cave(split[1]));
            first.connectedTo.add(second);
            second.connectedTo.add(first);
            caves.put(first.name, first);
            caves.put(second.name, second);
        });
        System.out.println((getenv("part") == null ? "part1" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1(caves.get("start"), caves.get("end")) :
                new App().solvePart2(caves.get("start"), caves.get("end")));
    }
}