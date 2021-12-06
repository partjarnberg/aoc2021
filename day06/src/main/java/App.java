import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.System.arraycopy;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class App {
    private static class LanternFish {
        static final int INITIAL_TIMER = 6, FIRST_INTIAL_TIMER = 8;
        int timer;
        LanternFish(final int timer) {
            this.timer = timer;
        }

        Optional<LanternFish> trySpawn() {
            if(--timer < 0) {
                timer = INITIAL_TIMER;
                return of(new LanternFish(FIRST_INTIAL_TIMER));
            }
            return empty();
        }
    }

    public Integer getSolutionPart1(final List<LanternFish> lanternFishes) { // 350149
        range(0, 80).forEach(day -> lanternFishes.addAll(lanternFishes.stream().map(LanternFish::trySpawn).filter(Optional::isPresent).map(Optional::get).collect(toList())));
        return lanternFishes.size();
    }

    public long getSolutionPart2(final List<LanternFish> lanternFishes) { // 1590327954513
        var ref = new Object() {
            long[] fishTimers = new long[9];
        };
        lanternFishes.forEach(fish -> ref.fishTimers[fish.timer]++);
        range(0, 256).forEach(day -> {
            var kids = ref.fishTimers[0];
            var resetFishes = ref.fishTimers[0];
            var temp = new long[ref.fishTimers.length];
            arraycopy(ref.fishTimers, 1, temp, 0, temp.length - 1);
            temp[6] += kids;
            temp[8] += resetFishes;
            ref.fishTimers = temp;
        });

        return stream(ref.fishTimers).sum();
    }

    public static void main(String[] args) throws IOException {
        final List<LanternFish> lanternFishes = new ArrayList<>(stream(Files.readString(Path.of("input.txt")).split(","))
                .map(timerString -> new LanternFish(Integer.parseInt(timerString))).toList());
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        if (part.equals("part1"))
            System.out.println(new App().getSolutionPart2(lanternFishes));
        else
            System.out.println(new App().getSolutionPart1(lanternFishes));
    }
}