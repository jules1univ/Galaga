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
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    private Path downloadUpdate() {
        Log.message("Application update downloading from: %s", this.remoteUpdate);

        String jarDirPath = this.getJarFilePath().getParent().toString();
        Path tempFilePath = Path.of(jarDirPath, "update.tmp");
        try (InputStream in = this.remoteUpdate.openStream();
                FileOutputStream out = new FileOutputStream(tempFilePath.toFile())) {
            in.transferTo(out);
            Log.message("Application downloaded to: %s", tempFilePath);

            return tempFilePath;
        } catch (IOException e) {
            Log.error("Application update download failed: %s", e.getMessage());
        }
        return null;
    }

    private boolean executeUpdate(Path appJarPath, Path tmpUpdatePath) {
        try {
            try (FileInputStream in = new FileInputStream(tmpUpdatePath.toFile());
                    FileOutputStream out = new FileOutputStream(appJarPath.toFile())) {
                in.transferTo(out);
            }

            tmpUpdatePath.toFile().delete();

            Log.message("Application update applied successfully.");
            Application.getContext().getApplication().stop();
            return true;
        } catch (IOException e) {
            Log.error("Application update failed: %s", e.getMessage());
        }
        return false;
    }

    public boolean load() {
        Path appJarPath = this.getJarFilePath();
        if (!this.isJarApplication() || appJarPath == null) {
            Log.message("Application update check skipped: not a jar application.");
            return true;
        }

        String currentVersion = this.checkSum(appJarPath.toString());
        String remoteVersion = this.fetchRemoteChecksum();
        if (currentVersion == null || remoteVersion == null) {
            Log.error("Application update check failed: unable to compute checksums.");
            return false;
        }

        if (currentVersion.equals(remoteVersion)) {
            Log.message("Application is up to date.");
            return true;
        }

        Log.message("Application update available: %s -> %s", currentVersion, remoteVersion);
        Path tmpUpdatePath = this.downloadUpdate();
        if (tmpUpdatePath == null) {
            return false;
        }

        return this.executeUpdate(appJarPath, tmpUpdatePath);
    }

}
