package mathax.legacy.installer;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
        ImageIcon icon = createImageIcon();

        // Colors & Fonts
        // TODO: Load Comfortaa font and make it work. :)
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
            "\nHow to install:\nPut this .jar file to your mods folder and run Fabric for Minecraft version specified in the jar name.\n\n",
            "MatHax Legacy",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.ERROR_MESSAGE,
            icon,
            new String[]{"Download Fabric", "Open mods folder", "MatHax Website", "MatHax Discord"},
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
    protected static ImageIcon createImageIcon() {
        URL imgURL = Installer.class.getResource("/assets/mathaxlegacy/textures/icons/icon64.png");
        if (imgURL != null) {
            return new ImageIcon(imgURL, "MatHax Legacy");
        } else {
            System.err.println("Couldn't find icon: " + "/assets/mathaxlegacy/textures/icons/icon64.png");
            return null;
        }
    }
}
