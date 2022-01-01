package mathax.client.utils.misc;

import mathax.client.utils.Version;

import static mathax.client.MatHax.mc;

public class WindowUtils {
    public static class MatHax {
        public static void set() {
            setIcon();
            setTitle();
        }

        public static void setIcon() {
            mc.getWindow().setIcon(WindowUtils.class.getResourceAsStream("/assets/mathax/textures/icons/icon64.png"), WindowUtils.class.getResourceAsStream("/assets/mathax/textures/icons/icon128.png"));
        }

        public static void setTitleLoading() {
            mc.getWindow().setTitle("MatHax " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft() + " is being loaded...");
        }

        public static void setTitleLoaded() {
            mc.getWindow().setTitle("MatHax " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft() + " loaded!");
        }

        public static void setTitle() {
            mc.getWindow().setTitle("MatHax " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft());
        }
    }

    public static class Meteor {
        public static void set() {
            setIcon();
            setTitle();
        }

        public static void setIcon() {
            mc.getWindow().setIcon(WindowUtils.class.getResourceAsStream("/assets/mathax/textures/icons/meteor64.png"), WindowUtils.class.getResourceAsStream("/assets/mathax/textures/icons/meteor128.png"));
        }

        public static void setTitle() {
            mc.getWindow().setTitle("Meteor Client " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft());
        }
    }
}
