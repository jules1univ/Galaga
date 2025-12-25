package engine.network.server;

@FunctionalInterface
public interface ClientConnect {
    void run(ClientConnection client);
}
