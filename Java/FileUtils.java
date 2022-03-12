import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileUtils {

    public static void copyFolder(String sourceDirectory, String destinationDirectory) {
        try {
            File fileSource = new File(sourceDirectory);
            Arrays.stream(fileSource.listFiles())
                    .filter(File::isDirectory)
                    .forEach(f -> {
                        String newDirect = destinationDirectory + File.separatorChar + f.getName();
                        new File(newDirect).mkdirs();
                        FileUtils.copyFolder(f.getPath(), newDirect);
                    });
            Arrays.stream(fileSource.listFiles())
                    .filter(File::isFile)
                    .forEach(f -> {
                        String newDirect = destinationDirectory + File.separatorChar + f.getName();
                        File newFile = new File(newDirect);
                        try {
                            Files.copy(f.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void copyFolder2(String sourceDirectory, String destinationDirectory, List<String> existsFiles) {
        try {
            File fileSource = new File(sourceDirectory);
            for (File file : fileSource.listFiles()) {
                String newDirect = destinationDirectory + File.separatorChar + file.getName();
                if (existsFiles.contains(newDirect)) {
                    System.out.printf("пропустим файл %s%n", newDirect);
                    continue;
                }
                if (file.isDirectory()) {
                    new File(newDirect).mkdirs();
                    FileUtils.copyFolder2(file.getPath(), newDirect, existsFiles);
                } else if (file.isFile()) {
                    File newFile = new File(newDirect);
                    newFile.mkdirs();
                    Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Optional<List<String>> getExistsFiles(String sourceDirectory, String destinationDirectory) {
        List<String> result = new ArrayList<>();
        try {
            File fileSource = new File(sourceDirectory);
            for (File file : fileSource.listFiles()) {
                String newPath = destinationDirectory + File.separatorChar + file.getName();
                File newFile = new File(newPath);
                if (file.isDirectory()) {
                    if (newFile.exists()) {
                        var existFiles = FileUtils.getExistsFiles(file.getPath(), newPath);
                        existFiles.ifPresent(result::addAll);
                    }
                } else if (file.isFile()) {
                    if (newFile.exists() && file.length() == newFile.length()) {
                        result.add(newPath);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.of(result);
    }

    public static List<String> getExistsFiles2(String sourceDirectory, String destinationDirectory) {
        List<File> sourceFiles;
        List<File> destFiles = new ArrayList<>();
        try {
            sourceFiles = getAllFilesFromDirectory(sourceDirectory, x -> true);
            destFiles = getAllFilesFromDirectory(
                    destinationDirectory,
                    x -> sourceFiles.stream().anyMatch(f -> f.getName().equals(x.getName()) && f.length() == x.length())
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return destFiles.stream().map(File::getPath).collect(Collectors.toList());
    }

    public static List<File> getAllFilesFromDirectory(String pathDirectory, Function<File, Boolean> func) throws IOException {
        List<File> result = new ArrayList<>();
        Files.walkFileTree(Path.of(pathDirectory), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                File file = path.toFile();
                if (func.apply(file)) {
                    result.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }
}
