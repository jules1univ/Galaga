package engine.network;

import java.io.*;
import java.util.Optional;

public final class NetBuffer {
    private final ByteArrayOutputStream baos;
    private final DataOutputStream out;
    private final DataInputStream in;

    public NetBuffer() {
        this.baos = new ByteArrayOutputStream();
        this.out = new DataOutputStream(baos);
        this.in = null;
    }

    public NetBuffer(byte[] data) {
        this.baos = null;
        this.out = null;
        this.in = new DataInputStream(new ByteArrayInputStream(data));
    }

    public boolean write(int v)  {
        try {
            this.out.writeInt(v);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean write(float v)  {
        try {
            this.out.writeFloat(v);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean write(char v)  {
        try {
            this.out.writeChar(v);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean write(String v)
    {
        try {
            this.out.writeUTF(v);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean write(NetObject obj)  {
        try {
            obj.write(this);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<NetObject> read(NetObject obj)  {
        try {
            obj.read(this);
            return Optional.of(obj);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Float> readFloat()  {
        try {
            return Optional.of(this.in.readFloat());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<Integer> readInt()  {
        try {
            return Optional.of(this.in.readInt());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<Character> readChar()  {
        try {
            return Optional.of(this.in.readChar());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<String> readString()  {
        try {
            return Optional.of(this.in.readUTF());
        } catch (IOException e) {
            return Optional.empty();
        }
    }


    public Optional<byte[]> toBytes()  {
        try {
            this.out.flush();
            return Optional.of(this.baos.toByteArray());
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
