package engine.network.server;

import engine.network.NetObject;

@FunctionalInterface
public interface ClientReceive {
    void run(ClientConnection client, NetObject obj);
}
