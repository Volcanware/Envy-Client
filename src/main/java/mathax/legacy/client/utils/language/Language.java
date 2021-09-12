package mathax.legacy.client.utils.language;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class Language {
    public static Text getButton(String name) {
        return new TranslatableText("button.mathaxlegacy." + name.toLowerCase());
    }

    public static String getTextString(String name) {
        return new TranslatableText("text.mathaxlegacy." + name.toLowerCase()).getString();
    }

    public static String getCategoryTitleString(String name) {
        return new TranslatableText("category.title.mathaxlegacy." + name.toLowerCase()).getString();
    }

    public static String getModuleTitleString(String name) {
        return new TranslatableText("module.title.mathaxlegacy." + name.toLowerCase()).getString();
    }

    public static String getModuleDescriptionString(String name) {
        return new TranslatableText("module.description.mathaxlegacy." + name.toLowerCase()).getString();
    }
}
