package mathax.client.utils.misc;

import mathax.client.utils.Version;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import static mathax.client.MatHax.mc;

public class WindowUtils {
    public static class MatHax {
        public static void set() {
            setIcon();
            setTitle();
        }

        public static void setIcon() {
            Identifier smallIcon = new Identifier("mathax", "icons/64.png");
            Identifier bigIcon = new Identifier("mathax", "icons/128.png");
            setIcon(smallIcon, bigIcon);
        }

        public static void setIcon(Identifier icon1, Identifier icon2) {
            mc.getWindow().setIcon(MinecraftClient.getInstance().getResourceManager().getResource(icon1).get().getPack().open(ResourceType.CLIENT_RESOURCES, icon1), MinecraftClient.getInstance().getResourceManager().getResource(icon2).get().getPack().open(ResourceType.CLIENT_RESOURCES, icon2));
        }

        public static void setTitleLoading() {
            mc.getWindow().setTitle("Envy Client " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft() + " is being loaded...");
        }

        public static void setTitleLoaded() {
            mc.getWindow().setTitle("Envy Client " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft() + " loaded!");
        }

        public static void setTitle() {
            mc.getWindow().setTitle("Envy Client " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft());
        }
    }

    public static class Meteor {
        public static void set() {
            setIcon();
            setTitle();
        }

        public static void setIcon() {
            Identifier smallIcon = new Identifier("mathax", "textures/icons/meteor64.png");
            Identifier bigIcon = new Identifier("mathax", "textures/icons/meteor128.png");
            mc.getWindow().setIcon(
                MinecraftClient.getInstance().getResourceManager().getResource(smallIcon).get().getPack().open(ResourceType.CLIENT_RESOURCES, smallIcon),
                MinecraftClient.getInstance().getResourceManager().getResource(bigIcon).get().getPack().open(ResourceType.CLIENT_RESOURCES, bigIcon)
            );
        }

        public static void setTitle() {
            mc.getWindow().setTitle("Meteor Client " + Version.getStylized() + " - " + mc.getVersionType() + " " + Version.getMinecraft());
        }
    }
}
