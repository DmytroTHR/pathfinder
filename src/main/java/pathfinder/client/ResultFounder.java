package pathfinder.client;

import lombok.Data;
import pathfinder.server.FileCrawler;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

@Data
public class ResultFounder {
    private String mask;
    private int depth;

    public ResultFounder() {
        mask = "";
        depth = -1;
    }

    public long searchForResult(Writer writer) {
        int cntWait = 1;
        while (FileCrawler.foundFilesCache.size() <= depth + 1) {
            try {
                writer.write("Getting results on depth " + depth + ". Wait...\n");
                writer.flush();
                Thread.sleep(1000 * cntWait++);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + mask);

        return FileCrawler.foundFilesCache.get(depth).stream()
                .filter(p -> matchesPattern(matcher, p))
                .peek(p -> {
                    try {
                        writer.write("\t" + p + "\n");
                    } catch (IOException ex) {
                        System.err.println("Error writing to client socket");
                        ex.printStackTrace();
                    }
                })
                .count();
    }

    private boolean matchesPattern(PathMatcher matcher, Path path) {
        Path fileName = path.getFileName();

        return fileName != null && matcher.matches(fileName);
    }
}
