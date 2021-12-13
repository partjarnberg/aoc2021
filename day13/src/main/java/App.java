import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

public class App {
    enum Direction { UP, LEFT }
    record Dot(int x, int y) {}
    record Fold(Direction direction, int position) {}

    static class Paper {
        int[][] paperXFirst, paperYFirst;

        Paper(final int maxX, final int maxY, final List<Dot> dots) {
            paperXFirst = new int[maxX + 1][maxY + 1];
            paperYFirst = new int[maxY + 1][maxX + 1];
            dots.forEach(dot -> {
                paperXFirst[dot.x][dot.y] = 1;
                paperYFirst[dot.y][dot.x] = 1;
            });
        }

        void foldLeft(final Fold fold) {
            final int lineX = fold.position;
            int[][] rightOf = new int[paperXFirst.length - 1 - lineX][paperYFirst.length];
            range(lineX + 1, paperXFirst.length).forEach(x ->
                    rightOf[x - lineX - 1] = copyOfRange(paperXFirst[x], 0, paperYFirst.length));
            int[][] leftOf = new int[lineX][paperYFirst.length];
            range(0, lineX).forEach(x -> leftOf[x] = copyOfRange(paperXFirst[x], 0, paperYFirst.length));

            range(0, rightOf.length).forEach(x ->
                    range(0, paperYFirst.length).forEach(y ->
                            leftOf[leftOf.length - 1 - x][y] |= rightOf[x][y]
                    ));
            paperXFirst = leftOf;
            paperYFirst = new int[paperYFirst.length][paperYFirst.length];
            range(0, paperXFirst.length).forEach(x -> {
                range(0, paperYFirst.length).forEach(y -> {
                    paperYFirst[y][x] = paperXFirst[x][y];
                });
            });
        }

        void foldUp(final Fold fold) {
            final int lineY = fold.position;
            int[][] belowLine = new int[paperYFirst.length - 1 - lineY][paperXFirst.length];
            range(lineY + 1, paperYFirst.length).forEach(y ->
                    belowLine[y - lineY - 1] = copyOfRange(paperYFirst[y], 0, paperXFirst.length));
            int[][] aboveLine = new int[lineY][paperXFirst.length];
            range(0, lineY).forEach(y -> aboveLine[y] = copyOfRange(paperYFirst[y], 0, paperXFirst.length));

            range(0, belowLine.length).forEach(y ->
                    range(0, paperXFirst.length).forEach(x ->
                            aboveLine[aboveLine.length - 1 - y][x] |= belowLine[y][x]
            ));
            paperYFirst = aboveLine;
            paperXFirst = new int[paperXFirst.length][paperYFirst.length];
            range(0, paperYFirst.length).forEach(y -> {
                range(0, paperXFirst.length).forEach(x -> {
                    paperXFirst[x][y] = paperYFirst[y][x];
                });
            });
        }

        public void fold(final Fold fold) {
            switch (fold.direction) {
                case UP -> foldUp(fold);
                case LEFT -> foldLeft(fold);
                default -> throw new IllegalStateException();
            }
        }
    }

    public long solvePart1(final Paper paper, final List<Fold> folds) { // 747
        paper.fold(folds.get(0));
        return stream(paper.paperXFirst).mapToLong(row -> stream(row, 0, row.length).sum()).sum();
    }

    public String solvePart2(final Paper paper, final List<Fold> folds) { // ARHZPCUH
        folds.forEach(paper::fold);
        return '\n' + stream(paper.paperYFirst).map(row -> {
            String rowString = stream(row).mapToObj(point -> point == 1 ? "#" : ".").collect(joining());
            return rowString + '\n';
        }).collect(joining());
    }

    public static void main(String[] args) throws IOException {
        final List<Dot> dots = Files.lines(Path.of("input.txt")).takeWhile(line -> !line.isEmpty()).map(line -> {
            final String[] split = line.split(",");
            return new Dot(parseInt(split[0]), parseInt(split[1]));
        }).toList();
        final List<Fold> folds = Files.lines(Path.of("input.txt")).filter(line -> line.startsWith("fold along")).map(line -> {
            if (line.startsWith("fold along y")) return new Fold(Direction.UP, parseInt(line.split("=")[1]));
            return new Fold(Direction.LEFT, parseInt(line.split("=")[1]));
        }).toList();
        final int maxX = dots.stream().max(comparingInt(p -> p.x)).orElseThrow().x;
        final int maxY = dots.stream().max(comparingInt(p -> p.y)).orElseThrow().y;
        final Paper paper = new Paper(maxX, maxY, dots);
        System.out.println((getenv("part") == null ? "part2" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1(paper, folds) :
                new App().solvePart2(paper, folds));
    }
}