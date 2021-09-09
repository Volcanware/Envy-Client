package mathax.legacy.client.asm;

import mathax.legacy.client.asm.transformers.CanvasWorldRendererTransformer;
import mathax.legacy.client.asm.transformers.GameRendererTransformer;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.transformers.MixinClassWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** When mixins are just not good enough **/
public class Asm {
    private final Map<String, AsmTransformer> transformers = new HashMap<>();
    private final boolean export;

    public Asm() {
        add(new GameRendererTransformer());
        add(new CanvasWorldRendererTransformer());

        export = System.getProperty("mathaxlegacy.asm.export") != null;
    }

    private void add(AsmTransformer transformer) {
        transformers.put(transformer.targetName, transformer);
    }

    public byte[] transform(String name, byte[] bytes) {
        AsmTransformer transformer = transformers.get(name);

        if (transformer != null) {
            ClassNode klass = new ClassNode();
            ClassReader reader = new ClassReader(bytes);
            reader.accept(klass, ClassReader.EXPAND_FRAMES);

            transformer.transform(klass);

            ClassWriter writer = new MixinClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            klass.accept(writer);
            bytes = writer.toByteArray();

            if (export) {
                try {
                    Path path = Path.of(FabricLoader.getInstance().getGameDir().toString(), ".MatHax.Legacy.asm.out", name.replace('.', '/') + ".class");
                    new File(path.toUri()).getParentFile().mkdirs();
                    Files.write(path, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return  bytes;
    }
}
