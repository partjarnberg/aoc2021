import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.System.getenv;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

public class App {
    record Position(int x, int y) {}
    static class Node {
        final Position position;
        final int riskLevel;
        final HashMap<Node, Integer> neighbors = new HashMap<>();
        boolean visited = false;
        int distance = Integer.MAX_VALUE;

        Node(final Position position, final int riskLevel) {
            this.position = position;
            this.riskLevel = riskLevel;
        }

        void addNeighbor(final Node neighbor) {
            neighbors.put(neighbor, neighbor.riskLevel);
        }
    }

    // Finding the minimum distance
    Node findMinDistance(final List<Node> graph) {
        var ref = new Object() {
            int minDistance = Integer.MAX_VALUE;
            Node minDistanceNode = null;
        };

        graph.forEach(node -> {
            if(!node.visited && node.distance < ref.minDistance) {
                ref.minDistance = node.distance;
                ref.minDistanceNode = node;
            }
        });

        return ref.minDistanceNode;
    }

    int dijkstra(final List<Node> graph, final Node start) {
        start.distance = 0;
        IntStream.range(0, graph.size()).forEach(ignore -> {
            // Update the distance between neighbouring vertex and source vertex
            Node visited = findMinDistance(graph);
            visited.visited = true;

            // Update all the neighbouring vertex distances
            visited.neighbors.forEach((neighbor, riskLevel) -> {
                if(!neighbor.visited && riskLevel != 0 && (visited.distance + riskLevel < neighbor.distance)) {
                    neighbor.distance = visited.distance + riskLevel;
                }
            });
        });
        return graph.get(graph.size() - 1).distance;
    }

    public long solvePart1(final List<List<Node>> graph) { // 748
        final Node start = graph.get(0).get(0);
        return dijkstra(graph.stream().flatMap(List::stream).toList(), start);
    }

    public long solvePart2(final List<List<Node>> graph) { // 3045
        final List<List<Node>> biggerGraph = connectWithNeighbors(range(0, 5 * graph.size()).mapToObj(y -> range(0, 5 * graph.get(0).size()).mapToObj(x -> {
            final Node node = graph.get(y % graph.size()).get(x % graph.get(0).size());
            int newRiskLevel = (node.riskLevel + y / graph.size() + x / graph.get(0).size());
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