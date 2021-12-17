import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.System.getenv;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.List.of;
import static java.util.stream.IntStream.range;

public class App {
    record Position(int x, int y) {}
    static class Node {
        final Position position;
        final int risklevel;
        final HashMap<Node, Integer> neighbors = new HashMap<>();
        int accumulatedRisklevels = Integer.MAX_VALUE;

        Node(final Position position, final int riskLevel) {
            this.position = position;
            this.risklevel = riskLevel;
        }

        void addNeighbor(final Node neighbor) {
            neighbors.put(neighbor, neighbor.risklevel);
        }
    }

    int dijkstra(final List<Node> graph, final Node start) {
        start.accumulatedRisklevels = 0;
        final Set<Node> visitedNodes = new HashSet<>();
        final Set<Node> toBeEvaluated = new HashSet<>(of(start));

        range(0, Integer.MAX_VALUE).takeWhile(ignore -> !toBeEvaluated.isEmpty()).forEach(ignore -> {

            final Node nodeWithLowestAccumulatedRisklevel = toBeEvaluated.stream().min(comparingInt(node -> node.accumulatedRisklevels)).orElseThrow();
            toBeEvaluated.remove(nodeWithLowestAccumulatedRisklevel);
            nodeWithLowestAccumulatedRisklevel.neighbors.forEach((neighbor, riskLevel) -> {
                if (!visitedNodes.contains(neighbor)) {
                    if (nodeWithLowestAccumulatedRisklevel.accumulatedRisklevels + riskLevel < neighbor.accumulatedRisklevels)
                        neighbor.accumulatedRisklevels = nodeWithLowestAccumulatedRisklevel.accumulatedRisklevels + riskLevel;
                    toBeEvaluated.add(neighbor);
                }
            });
            visitedNodes.add(nodeWithLowestAccumulatedRisklevel);
        });
        return graph.get(graph.size() - 1).accumulatedRisklevels;
    }

    public long solvePart1(final List<List<Node>> graph) { // 748
        final Node start = graph.get(0).get(0);
        return dijkstra(graph.stream().flatMap(List::stream).toList(), start);
    }

    public long solvePart2(final List<List<Node>> graph) { // 3045
        final List<List<Node>> biggerGraph = connectWithNeighbors(range(0, 5 * graph.size()).mapToObj(y -> range(0, 5 * graph.get(0).size()).mapToObj(x -> {
            final Node node = graph.get(y % graph.size()).get(x % graph.get(0).size());
            int newRiskLevel = (node.risklevel + y / graph.size() + x / graph.get(0).size());
            if(newRiskLevel > 9)
                newRiskLevel = (newRiskLevel % 10) + 1;
            return new Node(new Position(x, y), newRiskLevel);
        }).toList()).toList());

        final Node start = biggerGraph.get(0).get(0);
        return dijkstra(biggerGraph.stream().flatMap(List::stream).toList(), start);
    }

    static List<List<Node>> connectWithNeighbors(final List<List<Node>> graph) {
        range(0, graph.size()).forEach(y -> range(0, graph.get(0).size()).forEach(x -> {
            final Node node = graph.get(y).get(x);
            if (x > 0) node.addNeighbor(graph.get(y).get(x - 1));
            if (x < graph.get(0).size() - 1) node.addNeighbor(graph.get(y).get(x + 1));
            if (y > 0) node.addNeighbor(graph.get(y - 1).get(x));
            if (y < graph.size() - 1) node.addNeighbor(graph.get(y + 1).get(x));
        }));
        return graph;
    }

    public static void main(String[] args) throws IOException {
        final List<List<Integer>> riskLevels = Files.lines(Path.of("input.txt")).map(line -> stream(line.split("")).map(Integer::parseInt).toList()).toList();
        final List<List<Node>> graph = connectWithNeighbors(range(0, riskLevels.size()).mapToObj(y -> range(0, riskLevels.get(0).size()).mapToObj(x -> new Node(new Position(x, y), riskLevels.get(y).get(x))).toList()).toList());
        System.out.println((getenv("part") == null ? "part1" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1(graph) :
                new App().solvePart2(graph));
    }
}