package installer.mathax.client.legacy;

import installer.mathax.client.legacy.version.Version;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class Installer {
    public static final Color MATHAX_COLOR = new Color(230, 75, 100);
    public static final Color MATHAX_BACKGROUND_COLOR = new Color(30, 30, 45);

    //TODO: MAKE THIS SHIT AN INSTALLER
    public static void main(String[] args) throws IOException, URISyntaxException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Icon
        ImageIcon icon = createImageIcon("/assets/mathaxlegacy/textures/icons/window/icon64.png", "MatHax Legacy");

        // Colors & Fonts
        UIManager.put("OptionPane.background", MATHAX_BACKGROUND_COLOR);
        UIManager.put("Panel.background", MATHAX_BACKGROUND_COLOR);
        UIManager.put("Button.background", MATHAX_COLOR);
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("Comfortaa", Font.BOLD, 16)));
        UIManager.put("Panel.messageFont", new FontUIResource(new Font("Comfortaa", Font.BOLD, 16)));
        UIManager.put("Button.font", new FontUIResource(new Font("Comfortaa", Font.BOLD, 16)));
        UIManager.put("OptionPane.messageForeground", MATHAX_COLOR);
        UIManager.put("Panel.messageForeground", MATHAX_COLOR);
        UIManager.put("Button.foreground", MATHAX_BACKGROUND_COLOR);

        // Options
        int option = JOptionPane.showOptionDialog(
            null,
            "How to install:\nPut this .jar file to your mods folder and run Fabric for Minecraft version " + Version.getMinecraft() + ".\n\n",
            "MatHax Legacy v" + Version.get() + " " + Version.getDevStylized() + " | Fabric " + Version.getMinecraft(),
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.ERROR_MESSAGE,
            icon,
            new String[] { "Download Fabric", "Open mods folder", "MatHax Website", "MatHax Discord" },
            null
        );

        switch (option) {
            case 0: openUrl("https://fabricmc.net/use/");
            case 1:
                String os = System.getProperty("os.name").toLowerCase();

                try {
                    if (os.contains("win")) {
                        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                            String path = System.getenv("AppData") + "/.minecraft/mods";
                            new File(path).mkdirs();
                            Desktop.getDesktop().open(new File(path));
                        }
                    } else if (os.contains("mac")) {
                        String path = System.getProperty("user.home") + "/Library/Application Support/minecraft/mods";
                        new File(path).mkdirs();
                        ProcessBuilder pb = new ProcessBuilder("open", path);
                        Process process = pb.start();
                    } else if (os.contains("nix") || os.contains("nux")) {
                        String path = System.getProperty("user.home") + "/.minecraft";
                        new File(path).mkdirs();
                        Runtime.getRuntime().exec("xdg-open \"" + path + "\"");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            case 2: openUrl("https://mathaxclient.xyz");
            case 3: openUrl("https://mathaxclient.xyz/Discord");
        }
    }

    // URL
    public static void openUrl(String url) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                }
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec("xdg-open " + url);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    // ICON
    protected static ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = Installer.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find icon: " + path);
            return null;
        }
    }
}
