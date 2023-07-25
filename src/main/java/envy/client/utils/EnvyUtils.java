package envy.client.utils;

import envy.client.Envy;

public class EnvyUtils {

    public static void pos() {
        Envy.mc.player.getPos();
    }
    public static void velocity() {
        Envy.mc.player.getVelocity();
    }
    public static void jump() {
        Envy.mc.player.jump();
    }
    public static void fall() {
        Envy.mc.player.setVelocity(Envy.mc.player.getVelocity().x, -0.5, Envy.mc.player.getVelocity().z);
    }
    public static void sprint() {
        Envy.mc.player.setSprinting(true);
    }
}
