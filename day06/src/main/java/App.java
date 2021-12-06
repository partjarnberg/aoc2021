import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    public long simulate(final long[] fishTimers, final int numberOfDays) {
        range(0, numberOfDays).forEach(day -> {
            var spawn = fishTimers[0];
            rangeClosed(0, 7).forEach(timer -> fishTimers[timer] = fishTimers[timer + 1]);
            fishTimers[6] += spawn;
            fishTimers[8] = spawn;
        });
        return stream(fishTimers).sum();
    }

    public static void main(String[] args) throws IOException {
        final long[] fishTimers = new long[9];
        stream(Files.readString(Path.of("input.txt")).split(",")).map(Integer::parseInt).forEach(timer -> fishTimers[timer]++);
        final String part = System.getenv("part") == null ? "part1" : System.getenv("part");
        System.out.println(new App().simulate(fishTimers, part.equalsIgnoreCase("part1") ? 80 : 256));
    }
}