package galaga.net.client;

import engine.network.NetObject;
import engine.network.NetworkManager;
import engine.network.client.Client;

public class GalagaClient extends Client {

    public GalagaClient(NetworkManager netm) {
        super(netm);
    }

    @Override
    protected void onReceive(NetObject obj) {

    }

    @Override
    protected void onConnect() {
    }

    @Override
    protected void onDisconnect() {
    }

}
