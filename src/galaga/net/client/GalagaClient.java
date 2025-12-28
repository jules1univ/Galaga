package galaga.net.client;

import engine.network.NetObject;
import engine.network.client.Client;
import galaga.net.objects.request.NetRequest;
import galaga.net.objects.request.NetRequestType;

public class GalagaClient extends Client {

    private NetClientState state;    

    public GalagaClient() {
        super();
    }

    public NetClientState getState() {
        return this.state;
    }

    private void handleRequest(NetRequest req) {
        if (req.getType() == NetRequestType.REQUEST) {
            switch (req.getField()) {
                case "username" -> {
                    this.state = NetClientState.WAIT_USERNAME;
                }
                default -> {}
            }
        }
    }

    @Override
    protected void onReceive(NetObject obj) {
        if (obj instanceof NetRequest req) {
            this.handleRequest(req);
        }
    }

    @Override
    protected void onConnect() {
        this.state = NetClientState.CONNECTED;
        this.send(NetRequest.create(NetRequestType.JOIN));
    }

    @Override
    protected void onDisconnect() {
        this.state = NetClientState.DISCONNECTED;
    }

}
