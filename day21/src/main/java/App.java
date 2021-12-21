import java.io.IOException;
import java.util.List;

import static java.lang.System.getenv;
import static java.util.Comparator.comparingInt;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    static class Dice {
        static final int START_VALUE = 1, END_VALUE = 100;
        static long noofRolls = 0;
        static int current = START_VALUE;

        static int roll() {
            try {
                noofRolls++;
                return current++;
            } finally {
                if(current > END_VALUE)
                    current = START_VALUE;
            }
        }
    }

    static class Player {
        static final int GAME_BOARD_SIZE = 10;
        final String name;
        int position;
        int score = 0;

        public Player(final String name, final int startingPosition) {
            this.name = name;
            this.position = startingPosition;
        }

        void rollDiceAndMove() {
            final int sumOfDices = rangeClosed(1, 3).map(turn -> Dice.roll()).sum();
            move(sumOfDices);
            updateScore();
        }

        void move(final int sumOfDices) {
            position = (position + sumOfDices) % GAME_BOARD_SIZE;
            if(position == 0) position = GAME_BOARD_SIZE;
        }

        void updateScore() {
            score += position;
        }
    }

    private boolean noPlayerHasWon(final List<Player> players) {
        return players.stream().allMatch(p -> p.score < 1000);
    }

    public long solvePart1() { // 797160
        final Player player1 = new Player("Player 1", 2);
        final Player player2 = new Player("Player 2", 1);
        final List<Player> players = List.of(player1, player2);
        rangeClosed(1, Integer.MAX_VALUE).takeWhile(turn -> noPlayerHasWon(players)).forEach(turn ->
                players.stream().takeWhile(ignore -> noPlayerHasWon(players)).forEach(Player::rollDiceAndMove));
        return Dice.noofRolls * players.stream().min(comparingInt(p -> p.score)).orElseThrow().score;
    }

    public long solvePart2() { //
        return 0;
    }

    public static void main(String[] args) throws IOException {

        System.out.println((getenv("part") == null ? "part1" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1() :
                new App().solvePart2());
    }
}