package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    private record Position(int x, int y) {}
    private record Line(Position from, Position to) {}

    private static class Diagram {
        final int[][] points;

        Diagram(final int[][] points) {
            this.points = points;
            range(0, this.points.length).forEach(x -> range(0, this.points[x].length).forEach(y -> this.points[x][y] = 0));
        }

        void mark(final Position position) {
            points[position.x][position.y]++;
        }

        int pointsWithLinesOverlap() {
            final AtomicInteger counter = new AtomicInteger();
            IntStream.range(0, points.length).forEach(x -> IntStream.range(0, points[x].length).forEach(y -> {
                if(points[x][y] > 1)
                    counter.getAndIncrement();
            }));
            return counter.get();
        }
    }

    public Integer getSolutionPart1(final Diagram diagram, final List<Line> lines) { // 5167
        markStraightLines(diagram, lines);
        return diagram.pointsWithLinesOverlap();
    }

    public Integer getSolutionPart2(final Diagram diagram, final List<Line> lines) { // 17604
        markStraightLines(diagram, lines);
        markDiagonalLines(diagram, lines);
        return diagram.pointsWithLinesOverlap();
    }

    private void markDiagonalLines(final Diagram diagram, final List<Line> lines) {
        final List<Line> diagonalLines = lines.stream().filter(line -> line.from.x != line.to.x && line.from.y != line.to.y).collect(toList());
        diagonalLines.forEach(line -> {
            final Position start = findStartPosition(line);
            final Position end = findEndPosition(line);
            if(start.y < end.y) {
                for(int x = start.x, y = start.y; x <= end.x && y <= end.y; x++, y++) {
                    diagram.mark(new Position(x, y));
                }
            } else {
                for(int x = start.x, y = start.y; x <= end.x && y >= end.y; x++, y--) {
                    diagram.mark(new Position(x, y));
                }
            }
        });
    }

    private void markStraightLines(final Diagram diagram, final List<Line> lines) {
        final List<Line> straightLines = lines.stream().filter(line -> line.from.x == line.to.x || line.from.y == line.to.y).collect(toList());
        straightLines.forEach(line -> {
            final Position start = findStartPosition(line);
            final Position end = findEndPosition(line);
            if(start.x < end.x) {
                rangeClosed(start.x, end.x).forEach(x -> diagram.mark(new Position(x, start.y)));
            } else {
                if(start.y < end.y)
                    rangeClosed(start.y, end.y).forEach(y -> diagram.mark(new Position(start.x, y)));
                else
                    rangeClosed(end.y, start.y).forEach(y -> diagram.mark(new Position(start.x, y)));
            }
        });
    }

    private Position findStartPosition(final Line line) {
        return line.from.x < line.to.x ? line.from : line.to;
    }

    private Position findEndPosition(final Line line) {
        return line.from.x < line.to.x ? line.to : line.from;
    }

    public static void main(String[] args) throws IOException {
        final int[] boundaries = new int[2];
        final List<Line> lines = Files.lines(Path.of("input.txt")).map(line -> {
            final List<Position> positions = stream(line.split(" -> ")).map(coordinateString -> {
                final String[] split = coordinateString.split(",");
                int x = parseInt(split[0]); int y = parseInt(split[1]);
                boundaries[0] = max(x, boundaries[0]); boundaries[1] = max(y, boundaries[1]);
                return new Position(x, y);
            }).collect(toList());
            return new Line(positions.get(0), positions.get(1));
        }).collect(toList());

        final Diagram diagram = new Diagram(new int[boundaries[0] + 1][boundaries[1] + 1]);
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part1"))
            System.out.println(new App().getSolutionPart2(diagram, lines));
        else
            System.out.println(new App().getSolutionPart1(diagram, lines));
    }
}