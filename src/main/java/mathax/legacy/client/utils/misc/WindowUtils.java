package mathax.legacy.client.utils.misc;

import mathax.legacy.client.utils.Version;

import static mathax.legacy.client.MatHaxLegacy.mc;

public class WindowUtils {
    public static class MatHax {
        public static void set() {
            setIcon();
            setTitle();
        }

        public static void setIcon() {
            mc.getWindow().setIcon(WindowUtils.class.getResourceAsStream("/assets/mathaxlegacy/textures/icons/icon64.png"), WindowUtils.class.getResourceAsStream("/assets/mathaxlegacy/textures/icons/icon128.png"));
        }

        public static void setTitleLoading() {
            mc.getWindow().setTitle("MatHax Legacy " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft() + " is being loaded...");
        }

        public static void setTitleLoaded() {
            mc.getWindow().setTitle("MatHax Legacy " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft() + " loaded!");
        }

        public static void setTitle() {
            mc.getWindow().setTitle("MatHax Legacy " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft());
        }
    }

    public static class Meteor {
        public static void set() {
            setIcon();
            setTitle();
        }

        public static void setIcon() {
            mc.getWindow().setIcon(WindowUtils.class.getResourceAsStream("/assets/mathaxlegacy/textures/icons/meteor64.png"), WindowUtils.class.getResourceAsStream("/assets/mathaxlegacy/textures/icons/meteor128.png"));
        }

        public static void setTitle() {
            mc.getWindow().setTitle("Meteor Client " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft());
        }
    }
}
