package engine.network.server;

@FunctionalInterface
public interface ClientDisconnect {
    void run(ClientConnection client);
}