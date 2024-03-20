package es.jfp.localclientproject.repositorys;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerRepository {

    private static ServerRepository instance;

    private ServerRepository() {}

    public static ServerRepository getInstance() {
        synchronized (ServerRepository.class) {
            if (instance==null) {
                instance = new ServerRepository();
            }
            return instance;
        }
    }

    public Map<String, List<String[]>> getDirectoryMap() {
        Map<String, List<String[]>> directoryMap = null;
        try {
            Socket socket = new Socket("192.168.0.19", 4545);
            OutputStream os = new BufferedOutputStream(socket.getOutputStream());

            os.write(0);
            os.flush();

            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            directoryMap = (Map<String, List<String[]>>) is.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return directoryMap;
    }

}
