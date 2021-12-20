import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.valueOf;
import static java.lang.System.getenv;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

public class App {
    record ImageEnhancer(String algorithm) {
        List<List<Character>> enhance(final ImageEnhancer imageEnhancer, final List<List<Character>> image, final int noofTimes) {
            final AtomicReference<List<List<Character>>> resizedImage = new AtomicReference<>(resize(image, '.'));
            return rangeClosed(1, noofTimes).mapToObj(times -> {
                final List<List<Character>> templateImage = createTemplateImageFrom(resizedImage.get());

                range(1, templateImage.size() - 1).forEach(y -> range(1, templateImage.get(0).size() - 1).forEach(x -> {
                    final List<List<Character>> x3SquareFor = get3x3SquareFor(y, x, resizedImage.get());
                    final Character pixel = imageEnhancer.calculateOutputPixelFor(x3SquareFor);
                    templateImage.get(y).set(x, pixel);
                }));

                removeUnusedBorder(templateImage);

                if(imageEnhancer.algorithm.startsWith("#"))
                    resizedImage.set(resize(templateImage, times % 2 == 0 ? '.' : '#'));
                else
                    resizedImage.set(resize(templateImage, '.'));
                return resizedImage.get();
            }).reduce((first, second) -> second).orElseThrow();
        }

        private List<List<Character>> get3x3SquareFor(final int y, final int x, final List<List<Character>> image) {
            final List<Character> above = image.get(y - 1).subList(x - 1, x + 2);
            final List<Character> middle = image.get(y).subList(x - 1, x + 2);
            final List<Character> below = image.get(y + 1).subList(x - 1, x + 2);
            return List.of(above, middle, below);
        }

        private void removeUnusedBorder(final List<List<Character>> templateImage) {
            templateImage.remove(0);
            templateImage.remove(templateImage.size() - 1);
            templateImage.forEach(row -> {
                row.remove(0);
                row.remove(row.size() - 1);
            });
        }

        private List<List<Character>> createTemplateImageFrom(final List<List<Character>> image) {
            return range(0, image.size())
                    .mapToObj(y -> range(0, image.get(0).size()).mapToObj(x -> '.').collect(toList()))
                    .collect(toList());
        }

        private List<List<Character>> resize(final List<List<Character>> image, final Character theInfinitChar) {
            final List<List<Character>> resizedImage = image.stream().peek(characters -> range(0, 2).forEach(times -> {
                characters.add(0, theInfinitChar);
                characters.add(theInfinitChar);
            })).collect(toList());
            range(0, 2).forEach(times -> resizedImage.add(0, nCopies(resizedImage.get(0).size(), theInfinitChar)));
            range(0, 2).forEach(times -> resizedImage.add(nCopies(resizedImage.get(0).size(), theInfinitChar)));
            return resizedImage;
        }

        Character calculateOutputPixelFor(final List<List<Character>> square) {
            final String binaryString = range(0, 3).mapToObj(y -> range(0, 3).mapToObj(x ->
                            valueOf(square.get(y).get(x))).toList()).flatMap(List::stream).collect(joining())
                    .replace(".", "0")
                    .replace("#", "1");
            int index = binaryToInt(binaryString);
            return algorithm.charAt(index);
        }

        int binaryToInt(final String binaryString) {
            return Integer.parseInt(binaryString, 2);
        }
    }

    public long solvePart1(final ImageEnhancer imageEnhancer, final List<List<Character>> image) { // 5563
        return imageEnhancer.enhance(imageEnhancer, image, 2).stream().flatMap(List::stream).filter(c -> c.equals('#')).count();
    }

    public long solvePart2(final ImageEnhancer imageEnhancer, final List<List<Character>> image) { // 19743
        return imageEnhancer.enhance(imageEnhancer, image, 50).stream().flatMap(List::stream).filter(c -> c.equals('#')).count();
    }

    public static void main(String[] args) throws IOException {
        ImageEnhancer imageEnhancer = Files.lines(Path.of("input.txt")).limit(1).map(ImageEnhancer::new).findFirst().orElseThrow();
        final List<List<Character>> image = Files.lines(Path.of("input.txt")).skip(2)
                .map(line -> line.chars().mapToObj(c -> (char) c).collect(toList())).collect(toList());
        System.out.println((getenv("part") == null ? "part2" : getenv("part")).equalsIgnoreCase("part1") ?
                new App().solvePart1(imageEnhancer, image) :
                new App().solvePart2(imageEnhancer, image));
    }
}