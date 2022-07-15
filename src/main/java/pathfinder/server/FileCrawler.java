package pathfinder.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class FileCrawler implements Runnable {
    public static CopyOnWriteArrayList<Set<Path>> foundFilesCache;
    @NonNull
    private final Path rootPath;

    @Data
    @AllArgsConstructor
    class LeveledPath {
        private Path path;
        private int level;
    }

    public FileCrawler(String rootPath) {
        this.rootPath = Path.of(rootPath);
        foundFilesCache = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        traverseFileTree();

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            foundFilesCache.stream().flatMap(Collection::stream)
                    .filter(Files::isDirectory)
                    .forEach(path -> {
                        try {
                            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                                    StandardWatchEventKinds.ENTRY_DELETE);
                        } catch (IOException ex) {
                        }
                    });
            watchFileChanges(watchService);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void traverseFileTree() {
        Set<Path> curLevelFiles = new HashSet<>();
        Queue<LeveledPath> queue = new ArrayDeque<>();

        curLevelFiles.add(rootPath);
        queue.add(new LeveledPath(rootPath, 0));
        foundFilesCache.add(0, curLevelFiles);

        while (!queue.isEmpty()) {
            LeveledPath curLPath = queue.poll();
            Path curPath = curLPath.getPath();
            if (!Files.isDirectory(curPath)
                    || !Files.isReadable(curPath)) {
                continue;
            }

            curLevelFiles.clear();
            try (Stream<Path> listNewLevel = Files.list(curPath)) {
                int newLevel = curLPath.getLevel() + 1;
                listNewLevel.forEach(p -> {
                    curLevelFiles.add(p);
                    queue.add(new LeveledPath(p, newLevel));
                });

                if (foundFilesCache.size() <= newLevel) {
                    foundFilesCache.add(newLevel, new HashSet<>(curLevelFiles));
                } else {
                    curLevelFiles.forEach(foundFilesCache.get(newLevel)::add);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void watchFileChanges(WatchService watchService) {
        try {
            WatchKey key;
            while ((key = watchService.take()) != null) {
                Path dir = (Path)key.watchable();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kindOfEvent = event.kind();
                    Path eventPath = dir.resolve((Path)event.context());
                    if (kindOfEvent == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("New file added:\t" + eventPath);
                        addPathToResult(eventPath);
                    } else if (kindOfEvent == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("File deleted:\t" + eventPath);
                        deletePathFromResult(eventPath);
                    }
                }

                key.reset();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private int currentLevel(@NonNull Path path) {
        return path.getNameCount() - rootPath.getNameCount();
    }

    private void addPathToResult(@NonNull Path path) {
        int level = currentLevel(path);
        if (foundFilesCache.size() <= level) {
            foundFilesCache.add(level, new HashSet<>());
        }
        foundFilesCache.get(level).add(path);
    }

    private void deletePathFromResult(@NonNull Path path) {
        int level = currentLevel(path);
        if (foundFilesCache.size() <= level) {
            return;
        }
        foundFilesCache.get(level).remove(path);
    }
}