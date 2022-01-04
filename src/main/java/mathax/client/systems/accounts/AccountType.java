package mathax.client.systems.accounts;

public enum AccountType {
    Cracked("Cracked"),
    Premium("Premium"),
    Microsoft("Microsoft"),
    The_Altening("The Altening");

    private final String title;

    AccountType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
