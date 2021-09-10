package mathax.legacy.client.utils.misc;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class SplashTextResourceSupplier extends SinglePreparationResourceReloader<List<String>> {
    private static final Identifier RESOURCE_ID = new Identifier("mathaxlegacy", "texts/splashes.txt");
    private static final Random RANDOM = new Random();
    private final List<String> splashTexts = Lists.newArrayList();
    private final Session session;

    public SplashTextResourceSupplier(Session session) {
        this.session = session;
    }

    protected List<String> prepare(ResourceManager resourceManager, Profiler profiler) {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(RESOURCE_ID);

            List list;
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

                try {
                    list = (List)bufferedReader.lines().map(String::trim).filter((splashText) -> {
                        return splashText.hashCode() != 125780783;
                    }).collect(Collectors.toList());
                } catch (Throwable throwable) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }

                    throw throwable;
                }

                bufferedReader.close();
            } catch (Throwable throwable) {
                if (resource != null) {
                    try {
                        resource.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (resource != null) {
                resource.close();
            }

            return list;
        } catch (IOException var11) {
            return Collections.emptyList();
        }
    }

    protected void apply(List<String> list, ResourceManager resourceManager, Profiler profiler) {
        splashTexts.clear();
        splashTexts.addAll(list);
    }

    @Nullable
    public String get() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
            return "Merry X-mas!";
        } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
            return "Happy new year!";
        } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
            return "OOoooOOOoooo! Spooky!";
        } else if (splashTexts.isEmpty()) {
            return null;
        } else if (session != null && RANDOM.nextInt(splashTexts.size()) == 42) {
            String username = session.getUsername();
            return username.toUpperCase(Locale.ROOT) + " IS YOU";
        } else {
            return splashTexts.get(RANDOM.nextInt(splashTexts.size()));
        }
    }
}
