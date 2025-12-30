package galaga.net.client;

import java.util.Map;

import engine.network.NetObject;
import engine.network.client.Client;
import engine.network.objects.form.NetForm;
import engine.network.objects.form.NetFormAction;
import galaga.net.shared.NetPlayerData;

public class GalagaClient extends Client {

    private NetClientState state;
    private final NetPlayerData playerData;

    public GalagaClient(NetPlayerData playerData) {
        super();
        this.playerData = playerData;
    }

    public NetClientState getState() {
        return this.state;
    }

    private void handleForm(NetForm form) {
        if(form.getAction() == NetFormAction.REQ_READ) {
            
            if(form.isResourceId(NetPlayerData.class)) {
                this.send(NetForm.response(NetPlayerData.class, Map.of(
                    "data", this.playerData
                )));
            }
            
            return;
        }
        
    }

    @Override
    protected void onReceive(NetObject obj) {
        if (obj instanceof NetForm form) {
            this.handleForm(form);
        }
    }

    @Override
    protected void onConnect() {
        this.state = NetClientState.CONNECTED;
    }

    @Override
    protected void onDisconnect() {
        this.state = NetClientState.DISCONNECTED;
    }

}
