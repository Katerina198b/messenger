package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * TODO: реализовать здесь свой протокол
 */
public class BinaryProtocol implements Protocol {

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        try (ByteArrayInputStream bos = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bos)) {

            Message msg = (Message)in.readObject();
            return msg;
        } catch (ClassNotFoundException| IOException e) {
            throw new ProtocolException("", e);
        }

    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(msg);
            byte[] objData = bos.toByteArray();
            //int size = objData.length;

            //ByteBuffer buf = ByteBuffer.allocate(size + 4);
            //buf.putInt(size);
            //buf.put(objData);

            //return buf.array();
            return objData;

        } catch (IOException e) {
            throw new ProtocolException("Failed to encode message", e);
        }
    }
}
