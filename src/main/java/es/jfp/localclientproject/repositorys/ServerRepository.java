package es.jfp.localclientproject.repositorys;

import es.jfp.localclientproject.controllers.MainController;
import es.jfp.localclientproject.elements.ProgressWidget;
import es.jfp.localclientproject.models.MainModel;
import javafx.application.Platform;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ServerRepository {

    private static ServerRepository instance;
    private Socket socket;
    private ServerRepository() {}

    public static ServerRepository getInstance() {
        synchronized (ServerRepository.class) {
            if (instance==null) {
                instance = new ServerRepository();
            }
            return instance;
        }
    }

    public void setServerSocket(InetAddress ipv4, int port) throws IOException {
        this.socket = new Socket();
        socket.connect(new InetSocketAddress(ipv4, port), 3000);
        System.out.println(socket);
    }

    public Map<String, List<String[]>> getDirectoryMap(boolean init) {
        Map<String, List<String[]>> directoryMap = null;
        try {

            if (init) {
                OutputStream os = new BufferedOutputStream(socket.getOutputStream());
                os.write(11);
                os.flush();
            }

            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            directoryMap = (Map<String, List<String[]>>) is.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return directoryMap;
    }


    public boolean loginUser(String username, String hashPassword) {
        System.out.println("login username " + username);
        try {
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());

            os.write(02);
            os.writeUTF(username);
            os.writeUTF(hashPassword);

            return is.readBoolean();

        } catch (IOException e) {
            return false;
        }
    }

    public boolean registerUser(String username, String hashPassword) {
        System.out.println("register username " + username);
        try {
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());

            os.write(01);
            os.writeUTF(username);
            os.writeUTF(hashPassword);

            return is.readBoolean();

        } catch (IOException e) {
            return false;
        }
    }

    public void uploadFile(File file, String relativePath) {

        System.out.println("Client start upload to: " + file);

        Thread thread = new Thread(() -> {
            ProgressWidget progressWidget = new ProgressWidget("Upload " + file.getName());

            MainModel.getInstance().insertOnProcessToolbar(progressWidget);
            try (InputStream is = Files.newInputStream(file.toPath())) {

                OutputStream os = new BufferedOutputStream(socket.getOutputStream());

                String path = relativePath + file.getName();

                os.write(21);
                os.flush();

                os.write(path.getBytes().length);
                os.flush();

                os.write(path.getBytes());
                os.flush();

                long bytesSent = 0;
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                    os.flush();
                    bytesSent += bytesRead;

                    int progress = (int) (((float) bytesSent / file.length()) * 100);

                    Platform.runLater(() -> progressWidget.setBarProgress(progress));
                }
                os.write(-1);
                os.flush();
                System.out.println("Final");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }



    public void downloadFile(String destinationPath, String path) {

        new Thread(() -> {
            try (OutputStream fos = Files.newOutputStream(Path.of(destinationPath))) {

                OutputStream os = new BufferedOutputStream(socket.getOutputStream());
                InputStream is = new BufferedInputStream(socket.getInputStream());

                os.write(22);
                os.flush();

                os.write(path.getBytes().length);
                os.flush();

                os.write(path.getBytes());
                os.flush();

                long bytesSent = 0;
                byte[] buffer = new byte[2048];
                int bytes;
                while ((bytes=is.read(buffer))!=-1) {
                    if (bytes == 1 && buffer[0] == -1) {
                        break;
                    }
                    fos.write(buffer, 0, bytes);
                    fos.flush();
                }
                System.out.println("Final");
                fos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void deleteFile(String pathToDelete) {
        try {

            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            os.write(24);
            os.flush();
            os.writeUTF(pathToDelete);
            os.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createNewFolder(String folderPath) {
        try {

            DataOutputStream os = new DataOutputStream(socket.getOutputStream());

            os.write(12);
            os.flush();

            os.writeUTF(folderPath);
            os.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean socketIsRunning() {
        return this.socket.isConnected();
    }

}
