package mathax.legacy.client.renderer.text;

public enum BetterChatFonts {
    Full_Width,
    Small_CAPS,
    None;

    @Override
    public String toString() {
        return super.toString().replace("_", " ");
    }
}
