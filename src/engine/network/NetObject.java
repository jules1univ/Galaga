package engine.network;

public interface NetObject {

    void read(NetBuffer buff);

    void write(NetBuffer buff);

    void interpolate(NetObject other, float factor);
}
