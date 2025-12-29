package galaga.net.client;

import engine.network.NetObject;
import engine.network.client.Client;
import engine.network.objects.form.NetForm;
import galaga.net.GalagaNetState;

public class GalagaClient extends Client {

    private NetClientState state;    

    public GalagaClient() {
        super();
    }

    public NetClientState getState() {
        return this.state;
    }

    private void handleForm(NetForm<GalagaNetState> form) {
           }

    @SuppressWarnings("unchecked")
    @Override
    protected void onReceive(NetObject obj) {
        if (obj instanceof NetForm form) {
            if(form.getState().getDeclaringClass() != GalagaNetState.class) {
                return;
            }
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
