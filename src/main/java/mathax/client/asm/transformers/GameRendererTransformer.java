package mathax.client.asm.transformers;

import mathax.client.asm.AsmTransformer;
import mathax.client.asm.Descriptor;
import mathax.client.asm.FieldInfo;
import mathax.client.asm.MethodInfo;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GameRendererTransformer extends AsmTransformer {
    private final MethodInfo getFovMethod;
    private final FieldInfo fovField;

    public GameRendererTransformer() {
        super(mapClassName("net/minecraft/class_757"));

        getFovMethod = new MethodInfo("net/minecraft/class_4184", null, new Descriptor("Lnet/minecraft/class_4184;", "F", "Z", "D"), true);
        fovField = new FieldInfo("net/minecraft/class_315", "field_1826", new Descriptor("D"), true);
    }

    @Override
    public void transform(ClassNode klass) {
        // Modify GameRenderer.getFov()
        MethodNode method = getMethod(klass, getFovMethod);
        if (method == null) throw new RuntimeException("[MatHax] Could not find method GameRenderer.getFov()!");

        int injectionCount = 0;

        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof LdcInsnNode in && in.cst instanceof Double && (double) in.cst == 90) {
                InsnList insns = new InsnList();
                generateEventCall(insns, new LdcInsnNode(in.cst));

                method.instructions.insert(insn, insns);
                method.instructions.remove(insn);
                injectionCount++;
            } else if (insn instanceof FieldInsnNode in && fovField.equals(in)) {
                InsnList insns = new InsnList();

                insns.add(new VarInsnNode(Opcodes.DSTORE, method.maxLocals));
                generateEventCall(insns, new VarInsnNode(Opcodes.DLOAD, method.maxLocals));

                method.instructions.insert(insn, insns);
                injectionCount++;
            }
        }

        if (injectionCount < 2) throw new RuntimeException("[MatHax] Failed to modify GameRenderer.getFov()!");
    }

    private void generateEventCall(InsnList insns, AbstractInsnNode loadPreviousFov) {
        insns.add(new FieldInsnNode(Opcodes.GETSTATIC, "mathax/client/MatHax", "EVENT_BUS", "Lmathax/client/eventbus/IEventBus;"));
        insns.add(loadPreviousFov);
        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mathax/client/events/render/GetFovEvent", "get", "(D)Lmathax/client/events/render/GetFovEvent;"));
        insns.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "mathax/client/eventbus/IEventBus", "post", "(Ljava/lang/Object;)Ljava/lang/Object;"));
        insns.add(new TypeInsnNode(Opcodes.CHECKCAST, "mathax/client/events/render/GetFovEvent"));
        insns.add(new FieldInsnNode(Opcodes.GETFIELD, "mathax/client/events/render/GetFovEvent", "fov", "D"));
    }
}
