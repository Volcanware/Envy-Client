package mathax.client.systems.accounts;

public enum AccountType {
    Cracked,
    Premium,
    Microsoft,
    The_Altening;

    @Override
    public String toString() {
        return super.toString().replace("_", " ");
    }
}
