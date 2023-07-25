package envy.client.systems.modules.chat;

import envy.client.eventbus.EventHandler;
import envy.client.events.game.SendMessageEvent;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.settings.StringListSetting;
import envy.client.settings.StringSetting;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.Items;

import java.util.List;

/*/-----------------------------------------------------------------------------------------------------------------------------/*/
/*/ Used from Meteor Tweaks                                                                                                     /*/
/*/ https://github.com/Declipsonator/Meteor-Tweaks/blob/main/src/main/java/me/declipsonator/meteortweaks/modules/GroupChat.java /*/
/*/-----------------------------------------------------------------------------------------------------------------------------/*/

public class GroupChat extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<List<String>> players = sgGeneral.add(new StringListSetting.Builder()
        .name("players")
        .description("Determines which players to message.")
        .build()
    );

    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
        .name("command")
        .description("How the message command is set up on the server.")
        .defaultValue("/msg %player% %message%")
        .build()
    );

    public GroupChat() {
        super(Categories.Misc, Items.PAPER, "group-chat", "Talks with people in groups privately using /msg.");
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        for(String playerString: players.get()) {
            for(PlayerListEntry onlinePlayer: mc.getNetworkHandler().getPlayerList()) {
                if (onlinePlayer.getProfile().getName().equalsIgnoreCase(playerString)) {
                    mc.player.sendChatMessage(command.get().replace("%player%", onlinePlayer.getProfile().getName()).replace("%message%", event.message));
                    break;
                }
            }
        }

        event.cancel();
    }
}
