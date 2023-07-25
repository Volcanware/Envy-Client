package envy.client.utils.misc;

import envy.client.Envy;
import envy.client.utils.Version;

public class WindowUtils {
    public static class MatHax {
        public static void set() {
            setIcon();
            setTitle();
        }

        public static void setIcon() {
            Envy.mc.getWindow().setIcon(WindowUtils.class.getResourceAsStream("/assets/envy/textures/icons/icon64.png"), WindowUtils.class.getResourceAsStream("/assets/envy/textures/icons/icon128.png"));
        }

        public static void setTitleLoading() {
            Envy.mc.getWindow().setTitle("Envy Client " + Version.getStylized() + " - " + Envy.mc.getVersionType() + " " + Version.getMinecraft() + " is being loaded...");
        }

        public static void setTitleLoaded() {
            Envy.mc.getWindow().setTitle("Envy Client " + Version.getStylized() + " - " + Envy.mc.getVersionType() + " " + Version.getMinecraft() + " loaded!");
        }

        public static void setTitle() {
            Envy.mc.getWindow().setTitle("Envy Client " + Version.getStylized() + " - " + Envy.mc.getVersionType() + " " + Version.getMinecraft());
        }
    }

    public static class Meteor {
        public static void set() {
            setIcon();
            setTitle();
        }

        public static void setIcon() {
            Envy.mc.getWindow().setIcon(WindowUtils.class.getResourceAsStream("/assets/envy/textures/icons/meteor64.png"), WindowUtils.class.getResourceAsStream("/assets/envy/textures/icons/meteor128.png"));
        }

        public static void setTitle() {
            Envy.mc.getWindow().setTitle("Meteor Client " + Version.getStylized() + " - " + Envy.mc.getVersionType() + " " + Version.getMinecraft());
        }
    }
}
