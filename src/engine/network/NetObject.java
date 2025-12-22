package engine.network;

public interface NetObject {
    int getId();

    void read(NetBuffer buff);

    void write(NetBuffer buff);
}
