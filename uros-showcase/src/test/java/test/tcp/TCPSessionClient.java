package test.tcp;

import com.bamboocloud.uros.client.UrosTcpClient;
import com.bamboocloud.uros.common.UrosFilter;
import com.bamboocloud.uros.io.ByteBufferStream;
import com.bamboocloud.uros.io.ObjectIntMap;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class TCPSessionClient {
    static class MyClientFilter implements UrosFilter {
        private final ObjectIntMap sidMap = new ObjectIntMap();
        @Override
        public ByteBuffer inputFilter(ByteBuffer istream, Object context) {
            int len = istream.limit() - 7;
            if (len > 0 &&
                istream.get() == 's' &&
                istream.get() == 'i' &&
                istream.get() == 'd') {
                int sid = ((int)istream.get()) << 24 |
                          ((int)istream.get()) << 16 |
                          ((int)istream.get()) << 8  |
                           (int)istream.get();
                sidMap.put(context, sid);
                return istream.slice();
            }
            istream.rewind();
            return istream;
        }

        @Override
        public ByteBuffer outputFilter(ByteBuffer ostream, Object context) {
            if (sidMap.containsKey(context)) {
                int sid = sidMap.get(context);
                ByteBuffer buf = ByteBufferStream.allocate(ostream.remaining() + 7);
                buf.put((byte)'s');
                buf.put((byte)'i');
                buf.put((byte)'d');
                buf.put((byte)(sid >> 24 & 0xff));
                buf.put((byte)(sid >> 16 & 0xff));
                buf.put((byte)(sid >> 8 & 0xff));
                buf.put((byte)(sid & 0xff));
                buf.put(ostream);
                ByteBufferStream.free(ostream);
                return buf;
            }
            return ostream;
        }
    }

    public interface Stub {
        int inc();
    }
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("TCPSessionClient START");
        UrosTcpClient client = new UrosTcpClient("tcp://localhost:4321");
        client.setFilter(new MyClientFilter());
        Stub stub = client.useService(Stub.class);
        System.out.println(stub.inc());
        System.out.println(stub.inc());
        System.out.println(stub.inc());
        System.out.println(stub.inc());
        System.out.println("TCPSessionClient STOP");
    }
}
