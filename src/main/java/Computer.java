import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Computer {
    private String path;

    public Computer() {
        path = System.getenv("APPDATA") + "/discord/Cache";
        if (!canFindDiscordData()) {
            path = System.getenv("APPDATA") + "/discordptb/Cache";
            if (!canFindDiscordData()) {
                path = System.getenv("APPDATA") + "/discordcanary/Cache";
                if (!canFindDiscordData()) {
                    path = "";
                    JOptionPane.showMessageDialog(null, "Couldn't find any Discord installation, please select your Discord path manually!");

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        path = fileChooser.getSelectedFile() + "/Cache";
                    }
                }
            }
        }
    }

    public boolean canFindDiscordData() {
        File file = getCacheFolder();
        return file.exists() && file.isDirectory() && file.canRead();
    }

    public File getCacheFolder() {
        return new File(path);
    }

    public void extractFiles(GUI gui, File folderDestination) {
        new Thread(() -> {
            File folderCacheFiles = getCacheFolder();
            File[] files = folderCacheFiles.listFiles();
            boolean error = false;

            if (files != null) {
                int n = 0;
                gui.setBarValue(0);
                gui.setBarMax(files.length);

                for (int i=0; i<files.length; i++) {
                    File cacheFile = files[i];
                    if (cacheFile.getName().startsWith("f_")) {
                        try {
                            String ext = FileExtDetector.getInstance().detectExt(cacheFile);
                            Files.copy(cacheFile.toPath(), new File(folderDestination.getAbsoluteFile() + "/" + cacheFile.getName() + ext).toPath(), StandardCopyOption.REPLACE_EXISTING);
                            n++;
                        } catch (IOException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(gui, "Error while copying the file \"" + cacheFile.getName() + "\"!", "Error!", JOptionPane.ERROR_MESSAGE);
                            error = true;
                        }
                    }

                    gui.setBarValue(i+1);
                }

                gui.setLocked(false);

                if (n > 0) {
                    gui.setStatus(true, n + " files have been successfully extracted!");
                    try {
                        Desktop.getDesktop().open(folderDestination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (error) gui.setStatus(false, "Error while copying!");
                    else {
                        gui.setBarMax(0);
                        gui.setBarValue(1);
                        gui.setStatus(false, "Cache is already empty!");
                    }
                }
            }
        }).start();
    }

    public void clearCache(GUI gui) {
        new Thread(() -> {
            File folderCacheFiles = getCacheFolder();
            File[] files = folderCacheFiles.listFiles();
            boolean error = false;

            if (files != null) {
                int n = 0;
                gui.setBarValue(0);
                gui.setBarMax(files.length);

                for (int i=0; i<files.length; i++) {
                    File cacheFile = files[i];
                    if (!cacheFile.getName().startsWith("f_")) {
                        if (cacheFile.delete()) {
                            n++;
                        } else {
                            gui.setStatus(false, "Error while removing data! Discord might still be running! (You need to close Discord completely in order to clear the cache)");
                            gui.setLocked(false);
                            return;
                        }
                    }

                    gui.setBarValue(n+1);
                }

                for (int i=0; i<files.length; i++) {
                    File cacheFile = files[i];
                    if (cacheFile.getName().startsWith("f_")) {
                        if (cacheFile.delete()) {
                            n++;
                        } else {
                            error = true;
                            JOptionPane.showMessageDialog(gui, "Error while copying file \"" + cacheFile.getName() + "\"!", "Error!", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    gui.setBarValue(i+1);
                }

                gui.setLocked(false);

                if (n > 0) {
                    gui.setStatus(true, n + " files have been successfully removed from the cache");
                } else {
                    if (error) gui.setStatus(false, "Error while removing data!");
                    else {
                        gui.setBarMax(0);
                        gui.setBarValue(1);
                        gui.setStatus(false, "Cache is already empty!");
                    }
                }
            }
        }).start();
    }
}
