package mathax.legacy.client.systems.modules.chat;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.game.ReceiveMessageEvent;
import mathax.legacy.client.events.game.SendMessageEvent;
import mathax.legacy.client.mixin.ChatHudAccessor;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.base91.Base91;
import mathax.legacy.client.utils.render.color.SettingColor;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/*/-----------------/*/
/*/ Made by NobreHD /*/
/*/-----------------/*/

public class ChatEncryption extends Module {
    private static final String password = "MatHaxEncryption";

    private static SecretKeySpec secretKey;

    private static byte[] key;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> encryptAll = sgGeneral.add(new BoolSetting.Builder()
        .name("encrypt-all")
        .description("Encrypts all sent messages.")
        .defaultValue(false)
        .build()
    );

    private final Setting<String> prefix = sgGeneral.add(new StringSetting.Builder()
        .name("prefix")
        .description("The prefix determining which messages will get encrypted.")
        .defaultValue(";")
        .visible(() -> !encryptAll.get())
        .build()
    );

    private final Setting<String> suffix = sgGeneral.add(new StringSetting.Builder()
        .name("suffix")
        .description("The prefix determining how will encrypted messages end.")
        .defaultValue("<3")
        .build()
    );

    private final Setting<Boolean> customKey = sgGeneral.add(new BoolSetting.Builder()
        .name("custom-key")
        .description("Allow you to use a custom key.")
        .defaultValue(true)
        .build()
    );

    private final Setting<String> groupKey = sgGeneral.add(new StringSetting.Builder()
        .name("key")
        .description("The key to use.")
        .defaultValue("MatHaxEncryption")
        .visible(customKey::get)
        .build()
    );

    private final Setting<SettingColor> decryptedColor = sgGeneral.add(new ColorSetting.Builder()
        .name("decrypted-color")
        .description("The color of decrypted messages.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    public ChatEncryption(){
        super(Categories.Client, Items.BARRIER, "chat-encryption", "Encrypts your chat messages.");
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getVisibleMessages().removeIf((message) -> message.getId() == event.id && event.id != 0);
        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getMessages().removeIf((message) -> message.getId() == event.id && event.id != 0);

        Text message = event.getMessage();
        if (message.getString().endsWith(suffix.get()) && !suffix.get().isEmpty()) {
            String[] msg = message.getString().split(" ",2);
            msg[1] = decrypt(msg[1], customKey.get() ? groupKey.get() : password);
            message = new LiteralText(msg[0] + " " + msg[1]).setStyle(message.getStyle().withColor(getIntFromColor(decryptedColor.get().r, decryptedColor.get().g, decryptedColor.get().b)));
        }

        event.setMessage(message);
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        String message = event.message;
        if (suffix.get().isEmpty()) {
            error("Suffix is empty, not sending...");
            event.setCancelled(true);
            event.message = null;
        } else if (encryptAll.get() || message.startsWith(prefix.get())) {
            if (!encryptAll.get()) message = message.substring(prefix.get().length());

            message = encrypt(message, (customKey.get() ? groupKey.get() : password));

            if (message.length() > 256) {
                error("Message is too long, not sending.");
                event.message = null;
                event.setCancelled(true);
            }
        }

        event.message = message;
    }

    public static void setKey(String myKey) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = Arrays.copyOf(sha.digest(myKey.getBytes(StandardCharsets.UTF_8)), 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }
    }

    public String encrypt(String strToEncrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base91.encodeToString(cipher.doFinal(compress(strToEncrypt.getBytes(StandardCharsets.UTF_8)))) + suffix.get();
        } catch (Exception exception) {
            error("Error while encrypting: " + exception);
        }

        return null;
    }

    public String decrypt(String strToDecrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(decompress(cipher.doFinal(Base91.decode(strToDecrypt.substring(0, strToDecrypt.length() - suffix.get().length())))), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            error("Error while decrypting: " + exception);
        }

        return strToDecrypt;
    }

    public static byte[] compress(byte[] in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DeflaterOutputStream defl = new DeflaterOutputStream(out);
            defl.write(in);
            defl.flush();
            defl.close();

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(150);
            return null;
        }
    }

    public static byte[] decompress(byte[] in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InflaterOutputStream infl = new InflaterOutputStream(out);
            infl.write(in);
            infl.flush();
            infl.close();

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(150);
            return null;
        }
    }

    public int getIntFromColor(int r, int g, int b) {
        r = (r << 16) & 0x00FF0000;
        g = (g << 8) & 0x0000FF00;
        b = b & 0x000000FF;
        return 0xFF000000 | r | g | b;
    }
}
