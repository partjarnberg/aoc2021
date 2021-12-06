import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    public long simulate(final long[] lanternFishTimers, final int numberOfDays) {
        rangeClosed(1, numberOfDays).forEach(day -> {
            var spawn = lanternFishTimers[0]; var newParents = lanternFishTimers[0];
            rangeClosed(0, 7).forEach(timer -> lanternFishTimers[timer] = lanternFishTimers[timer + 1]);
            lanternFishTimers[8] = spawn; lanternFishTimers[6] += newParents;
        });
        return stream(lanternFishTimers).sum();
    }

    public static void main(String[] args) throws IOException {
        final long[] lanternFishTimers = new long[9];
        stream(Files.readString(Path.of("input.txt")).split(",")).map(Integer::parseInt).forEach(timer -> lanternFishTimers[timer]++);
        System.out.println(new App().simulate(lanternFishTimers, (System.getenv("part") == null ? "part1" : System.getenv("part")).equalsIgnoreCase("part1") ? 80 : 256));
    }
}