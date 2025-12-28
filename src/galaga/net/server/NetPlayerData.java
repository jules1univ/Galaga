package galaga.net.server;

import engine.network.NetObject;
import galaga.net.objects.request.NetRequest;
import galaga.net.objects.request.NetRequestType;

public class NetPlayerData {
 
    public NetPlayerData() {
    }

    public NetObject onJoinRequest(NetRequest req) {
        if(req.getType() == NetRequestType.JOIN) {
            return NetRequest.create(NetRequestType.REQUEST, "username");
        }

        return null;
    }
}
