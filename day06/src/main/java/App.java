import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    public long simulate(final long[] lanternfishLifecycle, final int numberOfDays) {
        rangeClosed(1, numberOfDays).forEach(day -> {
            var spawn = lanternfishLifecycle[0]; var justBecameParents = lanternfishLifecycle[0];
            rangeClosed(0, 7).forEach(stage -> lanternfishLifecycle[stage] = lanternfishLifecycle[stage + 1]);
            lanternfishLifecycle[8] = spawn; lanternfishLifecycle[6] += justBecameParents;
        });
        return stream(lanternfishLifecycle).sum();
    }

    public static void main(String[] args) throws IOException {
        final long[] lanternfishLifecycle = new long[9];
        stream(Files.readString(Path.of("input.txt")).split(",")).map(Integer::parseInt).forEach(stage -> lanternfishLifecycle[stage]++);
        System.out.println(new App().simulate(lanternfishLifecycle, (System.getenv("part") == null ? "part1" : System.getenv("part")).equalsIgnoreCase("part1") ? 80 : 256));
    }
}