import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    private record Boards(List<Cell[][]> boards) {}

    public Integer getSolutionPart1(final List<Integer> randoms, final  Boards boards) { // 72770
        final AtomicInteger winningNumber = new AtomicInteger();
        randoms.stream().takeWhile(number -> weHaveAWinner(boards))
                .forEach(number -> {
                    winningNumber.set(number);
                    boards.boards.stream().takeWhile(ignore -> weHaveAWinner(boards))
                            .forEach(cells -> range(0, 5).forEach(row ->
                                    stream(cells[row]).forEach(cell -> {
                                        if (number == cell.number) {
                                            cell.marked = true;
                                        }
                                    })
                            ));
                });
        final Cell[][] winner = getWinner(boards).orElseThrow();
        final int sumOfUnmarked = range(0, 5).map(row -> range(0, 5)
                .filter(col -> !winner[row][col].marked)
                .map(col -> winner[row][col].number).sum()).sum();

        return winningNumber.get() * sumOfUnmarked;
    }

    public Integer getSolutionPart2(final List<Integer> randoms, Boards boards) { // 13912
        var ref = new Object() {
            Cell[][] winner = new Cell[5][5];
            int winningNumber = 0;
        };
        randoms.forEach(number ->
            boards.boards.forEach(board -> range(0, 5).forEach(row -> range(0, 5).forEach(col -> {
                if (!isAWinner(board) && number == board[row][col].number) {
                    board[row][col].marked = true;
                    if(isAWinner(board)) {
                        ref.winningNumber = number;
                        ref.winner = board;
                    }
                }
            })))
        );
        return ref.winningNumber * range(0, 5).map(row -> range(0, 5)
                .filter(col -> !ref.winner[row][col].marked)
                .map(col -> ref.winner[row][col].number).sum()).sum();
    }

    private boolean isAWinner(final Cell[][] board) {
        return range(0, 5).anyMatch(row -> {
            if (stream(board[row]).allMatch(cell -> cell.marked))
                return true;
            return range(0, 5).mapToObj(col -> board[col][row].marked).allMatch(isMarked -> isMarked);
        });
    }

    private Optional<Cell[][]> getWinner(final Boards boards) {
        return boards.boards.stream().filter(this::isAWinner).findFirst();
    }

    private boolean weHaveAWinner(final Boards boards) {
        return getWinner(boards).isEmpty();
    }

    public static void main(String[] args) throws IOException {
        final List<Integer> randoms = Files.lines(Path.of("input.txt")).limit(1).map(line -> stream(line.split(","))
                .mapToInt(Integer::valueOf).boxed().collect(toList())).flatMap(List::stream).collect(toList());
        final Boards boards = new Boards(stream(Files.lines(Path.of("input.txt"))
                .skip(2).map(line -> line.isBlank() ? "#" : line.trim() + " ").collect(Collectors.joining()).split("#"))
                .map(boardString -> {
                    final String[] s = boardString.replace("  ", " ").split(" ");
                    final Cell[][] cells = new Cell[5][5];
                    final AtomicInteger i = new AtomicInteger();
                    range(0, 5).forEach(row -> range(0, 5).forEach(col -> cells[row][col] = new Cell(parseInt(s[i.getAndIncrement()]), false)));
                    return cells;
                }).collect(toList()));
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part2"))
            System.out.println(new App().getSolutionPart2(randoms, boards));
        else
            System.out.println(new App().getSolutionPart1(randoms, boards));
    }
}