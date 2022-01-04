package mathax.client.systems.proxies;

public enum ProxyType {
    Socks4("Socks4"),
    Socks5("Socks5");

    private final String title;

    ProxyType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
