package mathax.client.legacy.systems.commands.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mathax.client.legacy.systems.commands.Command;
import mathax.client.legacy.systems.enemies.Enemies;
import mathax.client.legacy.systems.enemies.Enemy;
import mathax.client.legacy.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.command.CommandSource.suggestMatching;

public class EnemiesCommand extends Command {

    public EnemiesCommand() {
        super("enemies", "Manages enemies.", "enemy");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {

        builder.then(literal("add").then(argument("enemy", EnemyArgumentType.enemy())
                        .executes(context -> {
                            Enemy enemy = EnemyArgumentType.getEnemy(context, "enemy");

                            if (Enemies.get().add(enemy)) info("Added (highlight)%s (default)to enemies.", enemy.name);
                            else error("That person is already your enemy.");

                            return SINGLE_SUCCESS;
                        })
                )
        );

        builder.then(literal("remove").then(argument("enemy", EnemyArgumentType.enemy())
                        .executes(context -> {
                            Enemy enemy = EnemyArgumentType.getEnemy(context, "enemy");

                            if (Enemies.get().remove(enemy)) info("Removed (highlight)%s (default)from enemies.", enemy.name);
                            else error("That person is not your enemy.");

                            return SINGLE_SUCCESS;
                        })
                )
        );

        builder.then(literal("list").executes(context -> {
                    info("--- Enemies ((highlight)%s(default)) ---", Enemies.get().count());
                    Enemies.get().forEach(enemy-> ChatUtils.info("(highlight)" + enemy.name));
                    return SINGLE_SUCCESS;
                })
        );
    }

    private static class EnemyArgumentType implements ArgumentType<Enemy> {

        public static EnemyArgumentType enemy() {
            return new EnemyArgumentType();
        }

        @Override
        public Enemy parse(StringReader reader) throws CommandSyntaxException {
            return new Enemy(reader.readString());
        }

        public static Enemy getEnemy(CommandContext<?> context, String name) {
            return context.getArgument(name, Enemy.class);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return suggestMatching(mc.getNetworkHandler().getPlayerList().stream()
                    .map(entry -> entry.getProfile().getName()).collect(Collectors.toList()), builder);
        }

        @Override
        public Collection<String> getExamples() {
            return Arrays.asList("Enemy");
        }
    }
}
