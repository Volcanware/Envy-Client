package envy.client.systems.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import envy.client.settings.Setting;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class SettingValueArgumentType implements ArgumentType<String> {
    public static SettingValueArgumentType value() {
        return new SettingValueArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String text = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return text;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Setting<?> setting;

        try {
            setting = SettingArgumentType.getSetting(context);
        } catch (CommandSyntaxException ignored) {
            return null;
        }

        Iterable<Identifier> identifiers = setting.getIdentifierSuggestions();
        if (identifiers != null) return CommandSource.suggestIdentifiers(identifiers, builder);

        return CommandSource.suggestMatching(setting.getSuggestions(), builder);
    }
}
