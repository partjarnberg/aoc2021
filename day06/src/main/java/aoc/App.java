package aoc;
import java.io.IOException; import java.nio.file.*; import java.util.*; import java.util.stream.*;

public class App {
    public long simulate(final List<Long> lanternfishLifecycle, final int numberOfDays) {
        IntStream.rangeClosed(1, numberOfDays).forEach(day -> {
            Collections.rotate(lanternfishLifecycle, -1);
            lanternfishLifecycle.set(6, lanternfishLifecycle.get(6) + lanternfishLifecycle.get(8));
        });
        return lanternfishLifecycle.stream().reduce(Long::sum).orElseThrow();
    }

    public static void main(String[] args) throws IOException {
        final List<Long> lanternfishLifecycle = new ArrayList<>(Arrays.stream(new long[9]).boxed().toList());
        Arrays.stream(Files.readString(Path.of("input.txt")).split(",")).map(Integer::parseInt).forEach(stage -> lanternfishLifecycle.set(stage, lanternfishLifecycle.get(stage) + 1 ));
        System.out.println(new App().simulate(lanternfishLifecycle, (System.getenv("part") == null ? "part1" : System.getenv("part")).equalsIgnoreCase("part1") ? 80 : 256));
    }
}