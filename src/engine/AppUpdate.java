package engine;

import engine.utils.logger.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class AppUpdate {
    private final URL remoteCheckSum;
    private final URL remoteUpdate;

    public static AppUpdate of(String removeCheckSumUrl, String remoteUpdateUrl) {
        URL urlCheckSum = null;
        URL urlUpdate = null;
        try {
            urlCheckSum = URI.create(removeCheckSumUrl).toURL();
            urlUpdate = URI.create(remoteUpdateUrl).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
        }
        return new AppUpdate(urlCheckSum, urlUpdate);
    }

    private AppUpdate(URL removeCheckSumUrl, URL remoteUpdateUrl) {
        this.remoteCheckSum = removeCheckSumUrl;
        this.remoteUpdate = remoteUpdateUrl;
    }

    private boolean isJarApplication() {
        String classPath = System.getProperty("java.class.path");
        return classPath != null && classPath.endsWith(".jar");
    }

    private Path getJarFilePath() {
        String classPath = System.getProperty("java.class.path");
        if (classPath != null && classPath.endsWith(".jar")) {
            return Path.of(classPath);
        }
        return null;
    }

    private String checkSum(String path) {
        try (FileInputStream in = new FileInputStream(path)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            in.transferTo(new DigestOutputStream(OutputStream.nullOutputStream(), digest));
            return HexFormat.of().formatHex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    private String fetchRemoteChecksum() {
        if (this.remoteCheckSum == null) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.remoteCheckSum.openStream()))) {
            return reader.readLine().trim().substring(0, 64);
        } catch (IOException e) {
            return null;
        }
    }

    private Path downloadUpdate() {
        Log.message("Application update downloading from: %s", this.remoteUpdate);

        Path jarDirPath = this.getJarFilePath().getParent();
        if (jarDirPath == null) {
            Log.error("Application update download failed: unable to determine jar directory.");
            return null;
        }

        Path tempFilePath = Path.of(jarDirPath.toString(), "update.tmp");
        try (InputStream in = this.remoteUpdate.openStream();
                FileOutputStream out = new FileOutputStream(tempFilePath.toFile())) {
            in.transferTo(out);
            return tempFilePath;
        } catch (IOException e) {
            Log.error("Application update download failed: %s", e.getMessage());
        }
        return null;
    }

    private void executeUpdate(Path appJarPath, Path tmpUpdatePath) {
        try {
            Application.getContext().getFrame().showMessage(
                    "Application Update",
                    "Update downloaded. The application will now restart.",
                    () -> restartApplication(appJarPath, tmpUpdatePath));
        } catch (Exception e) {
            Log.error("Application update failed: %s", e.getMessage());
        }
    }

    private void restartApplication(Path appJarPath, Path tmpUpdatePath) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (os.contains("win")) {
                Path scriptPath = tmpUpdatePath.getParent().resolve("update.bat");
                String script = String.format(
                        "@echo off\n" +
                                "timeout /t 2 /nobreak > nul\n" +
                                "move /y \"%s\" \"%s\"\n" +
                                "start \"\" \"%s\"\n" +
                                "del \"%%~f0\"\n",
                        tmpUpdatePath, appJarPath, appJarPath);

                Files.writeString(scriptPath, script);
                pb = new ProcessBuilder("cmd", "/c", scriptPath.toString());
            } else {
                Path scriptPath = tmpUpdatePath.getParent().resolve("update.sh");
                String script = String.format(
                        "#!/bin/bash\n" +
                                "sleep 2\n" +
                                "mv \"%s\" \"%s\"\n" +
                                "java -jar \"%s\" &\n" +
                                "rm \"$0\"\n",
                        tmpUpdatePath, appJarPath, appJarPath);

               Files.writeString(scriptPath, script);
                scriptPath.toFile().setExecutable(true);
                pb = new ProcessBuilder("sh", scriptPath.toString());
            }
            pb.start();
            System.exit(0);
        } catch (IOException e) {
            Log.error("Application update failed to restart: %s", e.getMessage());
        }
    }

    public void load() {
        Path appJarPath = this.getJarFilePath();
        if (!this.isJarApplication() || appJarPath == null) {
            Log.message("Application update check skipped: not a jar application.");
            return;
        }

        String currentVersion = this.checkSum(appJarPath.toString());
        String remoteVersion = this.fetchRemoteChecksum();
        if (currentVersion == null || remoteVersion == null) {
            Log.error("Application update check failed: unable to compute checksums.");
            return;
        }

        if (currentVersion.equals(remoteVersion)) {
            Log.message("Application is up to date.");
            return;
        }

        Log.message("Application update available: %s -> %s", currentVersion, remoteVersion);
        Path tmpUpdatePath = this.downloadUpdate();
        if (tmpUpdatePath == null) {
            return;
        }

        this.executeUpdate(appJarPath, tmpUpdatePath);
    }

}
