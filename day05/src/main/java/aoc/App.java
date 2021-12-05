package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    private record Coordinate(int x, int y) {}
    private record Line(Coordinate from, Coordinate to) {}
    private record Diagram(int[][] map) {
        void mark(final Coordinate coordinate) {
            map[coordinate.x][coordinate.y]++;
        }

        int coordinatesWithOverlap() {
            return stream(map).mapToInt(x -> stream(x).map(i -> i > 1 ? 1 : 0).sum()).sum();
        }
    }

    public Integer getSolutionPart1(final Diagram diagram, final List<Line> lines) { // 5167
        markStraightLines(diagram, lines);
        return diagram.coordinatesWithOverlap();
    }

    public Integer getSolutionPart2(final Diagram diagram, final List<Line> lines) { // 17604
        markStraightLines(diagram, lines);
        markDiagonalLines(diagram, lines);
        return diagram.coordinatesWithOverlap();
    }

    private void markDiagonalLines(final Diagram diagram, final List<Line> lines) {
        lines.stream().filter(line -> line.from.x != line.to.x && line.from.y != line.to.y).forEach(line -> {
            final Coordinate start = findStart(line), end = findEnd(line);
            if(start.y < end.y)
                for(int x = start.x, y = start.y; x <= end.x && y <= end.y; x++, y++)
                    diagram.mark(new Coordinate(x, y));
            else
                for(int x = start.x, y = start.y; x <= end.x && y >= end.y; x++, y--)
                    diagram.mark(new Coordinate(x, y));
        });
    }

    private void markStraightLines(final Diagram diagram, final List<Line> lines) {
        lines.stream().filter(line -> line.from.x == line.to.x || line.from.y == line.to.y).forEach(line -> {
            final Coordinate start = findStart(line), end = findEnd(line);
            if(start.x < end.x)
                rangeClosed(start.x, end.x).forEach(x -> diagram.mark(new Coordinate(x, start.y)));
            else
                if(start.y < end.y)
                    rangeClosed(start.y, end.y).forEach(y -> diagram.mark(new Coordinate(start.x, y)));
                else
                    rangeClosed(end.y, start.y).forEach(y -> diagram.mark(new Coordinate(start.x, y)));
        });
    }

    private Coordinate findStart(final Line line) {
        return line.from.x < line.to.x ? line.from : line.to;
    }

    private Coordinate findEnd(final Line line) {
        return line.from.x < line.to.x ? line.to : line.from;
    }

    public static void main(String[] args) throws IOException {
        final int[] boundaries = new int[2];
        final List<Line> lines = Files.lines(Path.of("input.txt")).map(line -> {
            final List<Coordinate> coordinates = stream(line.split(" -> ")).map(coordinateString -> {
                final String[] split = coordinateString.split(",");
                int x = parseInt(split[0]); int y = parseInt(split[1]);
                boundaries[0] = max(x, boundaries[0]); boundaries[1] = max(y, boundaries[1]);
                return new Coordinate(x, y);
            }).collect(toList());
            return new Line(coordinates.get(0), coordinates.get(1));
        }).collect(toList());

        final Diagram diagram = new Diagram(new int[boundaries[0] + 1][boundaries[1] + 1]);
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part2"))
            System.out.println(new App().getSolutionPart2(diagram, lines));
        else
            System.out.println(new App().getSolutionPart1(diagram, lines));
    }
}