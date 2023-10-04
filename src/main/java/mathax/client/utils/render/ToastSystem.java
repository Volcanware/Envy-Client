package mathax.client.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mathax.client.MatHax;
import mathax.client.systems.config.Config;
import mathax.client.utils.render.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static mathax.client.MatHax.mc;

public class ToastSystem implements Toast {
    public static final int TITLE_COLOR = Color.fromRGBA(MatHax.INSTANCE.MATHAX_COLOR.r, MatHax.INSTANCE.MATHAX_COLOR.g, MatHax.INSTANCE.MATHAX_COLOR.b, 255);
    public static final int TEXT_COLOR = Color.fromRGBA(255, 255, 255, 255);

    private ItemStack icon;
    private Text title, text;
    private boolean justUpdated = true, playedSound;
    private final int titleColor, textColor;
    private long start, duration;

    public ToastSystem(@Nullable Item item, @Nullable Integer titleColor, @NotNull String title, @Nullable Integer textColor, @Nullable String text, long duration) {
        this.icon = item != null ? item.getDefaultStack() : null;
        this.titleColor = titleColor != null ? titleColor : TITLE_COLOR;
        this.title = Text.literal(title).setStyle(Style.EMPTY.withColor(new TextColor(this.titleColor)));
        this.textColor = textColor != null ? textColor : TEXT_COLOR;
        this.text = text != null ? Text.literal(text).setStyle(Style.EMPTY.withColor(new TextColor(this.textColor))) : null;
        this.duration = duration;
    }

    public ToastSystem(@Nullable Item item, @Nullable Integer titleColor, @NotNull String title, @Nullable Integer textColor, @Nullable String text) {
        this.icon = item != null ? item.getDefaultStack() : null;
        this.titleColor = titleColor != null ? titleColor : TITLE_COLOR;
        this.title = Text.literal(title).setStyle(Style.EMPTY.withColor(new TextColor(this.titleColor)));
        this.textColor = textColor != null ? textColor : TEXT_COLOR;
        this.text = text != null ? Text.literal(text).setStyle(Style.EMPTY.withColor(new TextColor(this.textColor))) : null;
        this.duration = 6000;
    }

    public Visibility draw(DrawContext context, ToastManager toastManager, long currentTime) {
        if (justUpdated) {
            start = currentTime;
            justUpdated = false;
        }

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        context.drawTexture(TEXTURE, 0, 0, 0, 0, getWidth(), getHeight());

        int x = icon != null ? 28 : 12;
        int titleY = 12;

        if (text != null) {
            context.drawText(mc.textRenderer, text, x, 18, textColor, false);
            titleY = 7;
        }

        context.drawText(mc.textRenderer, title, x, titleY, titleColor, false);

        if (icon != null) context.drawItem(icon, 8, 8);

        if (!playedSound && Config.get().toastSound.get()) {
            mc.getSoundManager().play(getSound());
            playedSound = true;
        }

        return currentTime - start >= duration ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    public void setIcon(@Nullable Item item) {
        this.icon = item != null ? item.getDefaultStack() : null;
        justUpdated = true;
    }

    public void setTitle(@NotNull String title) {
        this.title = Text.literal(title).setStyle(Style.EMPTY.withColor(new TextColor(titleColor)));
        justUpdated = true;
    }

    public void setText(@Nullable String text) {
        this.text = text != null ? Text.literal(text).setStyle(Style.EMPTY.withColor(new TextColor(textColor))) : null;
        justUpdated = true;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        justUpdated = true;
    }

    public SoundInstance getSound() {
        return PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), 1.2f, 1);
    }
}
