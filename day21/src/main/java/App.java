import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.getenv;
import static java.util.Comparator.comparingInt;
import static java.util.List.of;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    static class DeterministicDice {
        final int from, to;
        long noofRolls = 0;
        int current;

        DeterministicDice(final int from, final int to) {
            this.current = this.from = from;
            this.to = to;
        }

        int roll() {
            try {
                noofRolls++;
                return current++;
            } finally {
                if(current > to)
                    current = from;
            }
        }
    }

    static class DiracDice {
        final List<Integer> sumOfRolls = rangeClosed(1, 3)
                .mapToObj(first -> rangeClosed(1, 3)
                        .mapToObj(second -> rangeClosed(1, 3)
                                .mapToObj(third -> first + second + third).toList())
                .flatMap(List::stream).toList()).flatMap(List::stream).toList();

        List<Integer> roll() {
            return sumOfRolls;
        }
    }

    static class Player {
        final int number;
        int position;
        int score;

        public Player(final int number, final int startingPosition) {
            this.number = number;
            this.position = startingPosition;
            this.score = 0;
        }

        public Player(final int number, int position, int score) {
            this.number = number;
            this.position = position;
            this.score = score;
        }

        public Player(final Player player) {
            this.number = player.number;
            this.position = player.position;
            this.score = player.score;
        }

        int roll(final DeterministicDice dice) {
            return rangeClosed(1, 3).map(turn -> dice.roll()).sum();
        }

        List<Integer> roll(final DiracDice dice) {
            return dice.roll();
        }
    }

    static class DeterministicGameBoard {
        static final int SIZE = 10;
        final List<Player> players;
        final DeterministicDice dice;

        DeterministicGameBoard(final List<Player> players, final DeterministicDice dice) {
            this.players = players;
            this.dice = dice;
        }

        void playNextTurn() {
            players.stream().takeWhile(ignore -> hasNoWinner()).forEach(player -> {
                final int stepsToMove = player.roll(dice);
                player.position = (player.position + stepsToMove) % SIZE;
                if(player.position == 0) player.position = SIZE;
                player.score += player.position;
            });
        }

        boolean hasNoWinner() {
            return players.stream().allMatch(p -> p.score < 1000);
        }
    }

    static class DiracGameBoard {
        static final int SIZE = 10;
        static final HashMap<Integer, Long> accumulatedWins = new HashMap<>(ofEntries(entry(1, 0L), entry(2, 0L)));
        final List<Player> players;
        final DiracDice dice;

        DiracGameBoard(final List<Player> players, final DiracDice dice) {
            this.players = players;
            this.dice = dice;
            System.out.println(dice.roll());
        }

        long[] play(final int[] position, final int[] score, final int turn, final Map<String, long[]> cache) {
            final String cacheKey = position[0] + "" + position[1] + "" + score[0] + "" + score[1] + "" + turn;
            if(cache.containsKey(cacheKey))
                return cache.get(cacheKey);

            if(score[0] >= 21)
                return new long[]{1, 0};
            else if(score[1] >= 21)
                return new long[] {0, 1};

            long[] wins = new long[]{0, 0};
            dice.roll().forEach(outcome -> {
                final int[] newPosition = new int[]{position[0], position[1]};
                final int[] newScore = new int[]{score[0], score[1]};

                newPosition[turn] = (newPosition[turn] + outcome) % SIZE;
                if(newPosition[turn] == 0) newPosition[turn] = SIZE;
                newScore[turn] += newPosition[turn];

                long[] newWins = play(newPosition, newScore, turn == 1 ? 0 : 1, cache);
                wins[0] += newWins[0];
                wins[1] += newWins[1];
            });
            cache.put(cacheKey, wins);
            return wins;
        }
    }

    public long solvePart1() { // 797160
        final DeterministicGameBoard gameBoard = new DeterministicGameBoard(of(
                new Player(1, 2),
                new Player(2, 1)),
                new DeterministicDice(1, 100));
        rangeClosed(1, Integer.MAX_VALUE).takeWhile(turn -> gameBoard.hasNoWinner()).forEach(turn -> gameBoard.playNextTurn());
        return gameBoard.dice.noofRolls * gameBoard.players.stream().min(comparingInt(p -> p.score)).orElseThrow().score;
    }

    public long solvePart2() { // 27464148626406
        final DiracGameBoard diracGameBoard = new DiracGameBoard(of(
                new Player(1, 4),
                new Player(2, 8)),
                new DiracDice());
        long[] wins = diracGameBoard.play(new int[]{2, 1}, new int[]{0, 0}, 0, new HashMap<>());
        System.out.println("Wins " + Arrays.toString(wins));
        return Math.max(wins[0], wins[1]);
    }

    public static void main(String[] args) {
        System.out.println((getenv("part") == null ? "part2" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1() :
                new App().solvePart2());
    }
}