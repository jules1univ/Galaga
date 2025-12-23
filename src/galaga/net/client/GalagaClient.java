package galaga.net.client;

import engine.network.NetObject;
import engine.network.NetworkManager;
import engine.network.client.Client;
import engine.utils.Position;
import engine.utils.logger.Log;

public class GalagaClient extends Client {

    public GalagaClient(NetworkManager netm) {
        super(netm);
    }

    @Override
    protected void onReceive(NetObject obj) {
        if (obj instanceof Position position) {
            Log.message("Received position: " + position);

            this.send(position.negate());
        }

    }

    @Override
    protected void onConnect() {
    }

    @Override
    protected void onDisconnect() {
    }

}
