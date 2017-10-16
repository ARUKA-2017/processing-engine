package akura.utility;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;


import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SocketEmitter {
    Socket socket = null;

    public static String START_METHOD = "START_METHOD";
    public static String END_METHOD = "END_METHOD";
    public static String LOG = "LOG";

    public SocketEmitter(String uri) {
        try {
            socket = IO.socket("http://localhost:4568/"+uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, objects -> {
            System.out.println("--------socekt conntected-------------");
        });

        socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = (Transport)args[0];
                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                        Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
                        headers.put("Origin", Arrays.asList("*"));
                    }
                });
            }
        });
        socket.connect();

    }

    public void  emit(String event,String args){
        socket.emit(event, args);
    }

}
