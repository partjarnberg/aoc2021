import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class App {
    private static class Cell {
        final int number; boolean marked;
        private Cell(int number, final boolean marked) {
            this.number = number;
            this.marked = marked;
        }
    }

    public static class Board {
        final Cell[][] cells;
        int winningNumber = -1;

        public Board(final Cell[][] cells) {
            checkArgument(cells.length == 5);
            this.cells = cells;
        }

        public void mark(final int number) {
            range(0, 5).takeWhile(ignore -> !isAWinner()).forEach(row ->
                    stream(cells[row]).filter(cell -> number == cell.number)
                            .forEach(cell -> {
                                cell.marked = true;
                                if(hasWinningRow() || hasWinningCol()) {
                                    winningNumber = number;
                                }
                            }));
        }

        private boolean hasWinningRow() {
            return range(0, 5).anyMatch(row -> stream(cells[row]).allMatch(cell -> cell.marked));
        }

        private boolean hasWinningCol() {
            return range(0, 5).anyMatch(col -> range(0, 5).allMatch(row -> cells[row][col].marked)
            );
        }

        public boolean isAWinner() {
            return winningNumber != -1;
        }

        public int sumOfUnmarked() {
            return range(0, 5).map(row -> range(0, 5).filter(col -> !cells[row][col].marked).map(col -> cells[row][col].number).sum()).sum();
        }
    }

    public Integer getSolutionPart1(final List<Integer> randoms, final List<Board> boards) { // 72770
        randoms.stream().takeWhile(ignore -> boards.stream().noneMatch(Board::isAWinner)).forEach(number -> boards.forEach(board -> board.mark(number)));
        final Board winner = boards.stream().filter(Board::isAWinner).findFirst().orElseThrow();
        return winner.winningNumber * winner.sumOfUnmarked();
    }

    public Integer getSolutionPart2(final List<Integer> randoms, List<Board> boards) { // 13912
        randoms.forEach(number -> boards.forEach(board -> board.mark(number)));
        final Board winner = boards.stream().filter(Board::isAWinner).max(Comparator.comparingInt(board -> randoms.indexOf(board.winningNumber))).orElseThrow();
        return winner.winningNumber * winner.sumOfUnmarked();
    }

    public static void main(String[] args) throws IOException {
        final List<Integer> randoms = Files.lines(Path.of("input.txt")).limit(1).map(line -> stream(line.split(","))
                .mapToInt(Integer::valueOf).boxed().collect(toList())).flatMap(List::stream).collect(toList());
        final List<Board> boards = stream(Files.lines(Path.of("input.txt"))
                .skip(2).map(line -> line.isBlank() ? "#" : line.trim() + " ").collect(Collectors.joining()).split("#"))
                .map(boardString -> {
                    final String[] s = boardString.replace("  ", " ").split(" ");
                    final Cell[][] cells = new Cell[5][5];
                    final AtomicInteger i = new AtomicInteger();
                    range(0, 5).forEach(row -> range(0, 5).forEach(col -> cells[row][col] = new Cell(parseInt(s[i.getAndIncrement()]), false)));
                    return new Board(cells);
                }).collect(toList());
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part1"))
            System.out.println(new App().getSolutionPart2(randoms, boards));
        else
            System.out.println(new App().getSolutionPart1(randoms, boards));
    }
}