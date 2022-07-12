package pathfinder;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class FileCrawlerTest {
    private final String path = System.getProperty("user.home");
    private final int depth = 2;
    private final String mask = "*.doc*";

    @Test
    public void test_findFilesThatMatch() {
        FileCrawler fc = new FileCrawler(path, depth, mask);
        List<Path> actual = fc.findFilesThatMatch();

        List<Path> expected = findRecursively(path, depth, mask);

        assertIterableEquals(actual, expected);
    }

    List<Path> findRecursively(String root, int depth, String mask) {
        List<Path> result = new ArrayList<>();
        Path rootPath = Path.of(root);
        try (Stream<Path> walker = Files.walk(rootPath, depth)) {
            result = walker.filter(p -> currentDepth(p, rootPath) == depth)
                    .filter(p -> matchesPattern(p, mask))
                    .collect(Collectors.toList());
        } catch (IOException ex) {
        }
        return result;
    }

    static boolean matchesPattern(Path path, String pattern) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        Path fileName = path.getFileName();

        return fileName != null && matcher.matches(fileName);
    }

    static int currentDepth(Path withPath, Path rootPath) {
        return withPath.getNameCount() - rootPath.getNameCount();
    }
}
