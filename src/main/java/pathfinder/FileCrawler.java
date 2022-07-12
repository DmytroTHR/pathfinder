package pathfinder;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

public class FileCrawler {
    private final Path rootPath;
    private final int maxDepth;
    private final PathMatcher matcher;

    public FileCrawler(String path, int depth, String fileMask) {
        this.rootPath = Paths.get(path);
        this.maxDepth = depth;
        this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + fileMask);
    }

    public List<Path> findFilesThatMatch() {
        List<Path> result = new ArrayList<>();
        if (maxDepth == 0) {
            if (matchesPattern(rootPath)) {
                result.add(rootPath);
            }
            return result;
        }

        Deque<Path> queue = new ArrayDeque<>();
        queue.add(rootPath);
        int curDepth = 0;

        while (!queue.isEmpty()) {
            try (Stream<Path> listNewLevel = Files.list(queue.poll())) {
                if (curDepth + 1 == maxDepth) {
                    listNewLevel.filter(this::matchesPattern)
                            .forEach(result::add);
                } else {
                    listNewLevel.forEach(p -> queue.add(p));
                    curDepth = currentDepth(queue.peek());
                }
            } catch (NotDirectoryException ex) {
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    private boolean matchesPattern(Path path) {
        Path fileName = path.getFileName();

        return fileName != null && matcher.matches(fileName);
    }

    private int currentDepth(Path withPath) {
        return withPath.getNameCount() - this.rootPath.getNameCount();
    }
}