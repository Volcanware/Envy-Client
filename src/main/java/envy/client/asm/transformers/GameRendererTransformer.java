package envy.client.asm.transformers;

import envy.client.asm.AsmTransformer;
import envy.client.asm.Descriptor;
import envy.client.asm.FieldInfo;
import envy.client.asm.MethodInfo;
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
        MethodNode method = getMethod(klass, getFovMethod);
        if (method == null) throw new RuntimeException("[Envy] Could not find method GameRenderer.getFov()!");

        int injectionCount = 0;

        for (AbstractInsnNode insn : method.instructions) {
            if (insn instanceof LdcInsnNode in && in.cst instanceof Double && (double) in.cst == 90) {
                InsnList insns = new InsnList();
                generateEventCall(insns, new LdcInsnNode(in.cst));

                method.instructions.insert(insn, insns);
                method.instructions.remove(insn);
                injectionCount++;
            } else if ((insn instanceof MethodInsnNode in1 && in1.name.equals("intValue") && insn.getNext() instanceof InsnNode _in && _in.getOpcode() == Opcodes.I2D) || (insn instanceof MethodInsnNode in2 && in2.owner.equals(klass.name) && in2.name.startsWith("redirect") && in2.name.endsWith("getFov"))) {
                InsnList insns = new InsnList();

                insns.add(new VarInsnNode(Opcodes.DSTORE, method.maxLocals));
                generateEventCall(insns, new VarInsnNode(Opcodes.DLOAD, method.maxLocals));

                method.instructions.insert(insn.getNext(), insns);
                injectionCount++;
            }
        }

        if (injectionCount < 2) throw new RuntimeException("[Envy] Failed to modify GameRenderer.getFov()!");
    }

    private void generateEventCall(InsnList insns, AbstractInsnNode loadPreviousFov) {
        insns.add(new FieldInsnNode(Opcodes.GETSTATIC, "envy/client/Envy", "EVENT_BUS", "Lmathax/client/eventbus/IEventBus;"));
        insns.add(loadPreviousFov);
        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "envy/client/events/render/GetFovEvent", "get", "(D)Lmathax/client/events/render/GetFovEvent;"));
        insns.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "envy/client/eventbus/IEventBus", "post", "(Ljava/lang/Object;)Ljava/lang/Object;"));
        insns.add(new TypeInsnNode(Opcodes.CHECKCAST, "envy/client/events/render/GetFovEvent"));
        insns.add(new FieldInsnNode(Opcodes.GETFIELD, "envy/client/events/render/GetFovEvent", "fov", "D"));
    }
}
