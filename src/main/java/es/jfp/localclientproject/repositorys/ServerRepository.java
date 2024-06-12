package es.jfp.localclientproject.repositorys;

import es.jfp.SerialMap;
import es.jfp.localclientproject.elements.ProgressWidget;
import es.jfp.localclientproject.models.MainModel;
import javafx.application.Platform;
import org.controlsfx.dialog.ExceptionDialog;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public synchronized boolean ping() {
        try {
            OutputStream os = new BufferedOutputStream(socket.getOutputStream());
            os.write(30);
            os.flush();
            return true;
        } catch (IOException e) {
            ExceptionDialog ed = new ExceptionDialog(e);
            ed.showAndWait();
            return false;
        }
    }

    public synchronized SerialMap getDirectoryMap() {
        try {

            OutputStream os = new BufferedOutputStream(socket.getOutputStream());
            os.write(11);
            os.flush();

            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

            return (SerialMap) is.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            ExceptionDialog ed = new ExceptionDialog(e);
            ed.showAndWait();
        }
        return null;
    }


    public synchronized boolean loginUser(String username, String hashPassword) {
        System.out.println("login username " + username);
        try {
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());

            os.write(02);
            os.flush();
            os.writeUTF(username);
            os.flush();
            os.writeUTF(hashPassword);
            os.flush();

            return is.readBoolean();

        } catch (IOException e) {
            ExceptionDialog ed = new ExceptionDialog(e);
            ed.showAndWait();
            return false;
        }
    }

    public synchronized boolean registerUser(String username, String hashPassword) {
        System.out.println("register username " + username);
        try {
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());

            os.write(01);
            os.writeUTF(username);
            os.writeUTF(hashPassword);

            return is.readBoolean();

        } catch (IOException e) {
            ExceptionDialog ed = new ExceptionDialog(e);
            ed.showAndWait();
            return false;
        }
    }

    public synchronized void closeSession() {
        try {
            OutputStream os = socket.getOutputStream();

            os.write(03);

        } catch (IOException e) {
            ExceptionDialog ed = new ExceptionDialog(e);
            ed.showAndWait();
            e.printStackTrace();
        }
    }

    public void uploadFile(File file, String relativePath) {

        System.out.println("Client start upload to: " + file);
        ProgressWidget progressWidget = new ProgressWidget("Upload " + file.getName());

        Thread uploadThread = new Thread(() -> {

            MainModel.getInstance().insertOnProcessToolbar(progressWidget);
            synchronized (this) {
                try (InputStream is = Files.newInputStream(file.toPath())) {

                    System.out.println("UPLOAD SIZE: " + file.length());

                    OutputStream os = new BufferedOutputStream(socket.getOutputStream());

                    os.write(21);
                    os.flush();

                    os.write(relativePath.getBytes().length);
                    os.flush();

                    os.write(relativePath.getBytes());
                    os.flush();

                    long bytesSent = 0;
                    byte[] buffer = new byte[2048];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                        os.flush();
                        bytesSent += bytesRead;

                        int progress = (int) (((float) bytesSent / file.length()) * 100);
                        System.out.println("Progress => " + progress);
                        Platform.runLater(() -> progressWidget.setBarProgress(progress));
                    }
                    os.write(-1);
                    os.flush();
                    System.out.println("END upload");

                } catch (IOException e) {
                    ExceptionDialog ed = new ExceptionDialog(e);
                    ed.showAndWait();
                }
            }

        });

        progressWidget.setProcessThread(uploadThread);
    }

    public void downloadFile(String destinationPath, String path, long fileSize) {

        ProgressWidget progressWidget = new ProgressWidget("Download " + Path.of(path).getFileName());

        Thread downloadThread = new Thread(() -> {
            MainModel.getInstance().insertOnProcessToolbar(progressWidget);
            synchronized (this) {
                try (OutputStream fos = Files.newOutputStream(Path.of(destinationPath))) {

                    System.out.println("UPLOAD SIZE: " + fileSize);

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
                        if (bytes == 1 && buffer[0] == -1 || bytes < buffer.length) {
                            break;
                        }
                        fos.write(buffer, 0, bytes);
                        fos.flush();
                        bytesSent += bytes;
                        int progress = (int) (((float) bytesSent / fileSize) * 100);
                        System.out.println("Progress => " + progress);
                        Platform.runLater(() -> progressWidget.setBarProgress(progress));
                    }
                    System.out.println("FINAL download");
                    fos.flush();

                    Platform.runLater(() -> progressWidget.setBarProgress(100));

                } catch (IOException e) {
                    ExceptionDialog ed = new ExceptionDialog(e);
                    ed.showAndWait();
                    e.printStackTrace();
                }
            }
        });

        progressWidget.setProcessThread(downloadThread);
    }

    public synchronized void downloadFolder(String destinationPath, String path) {


    }

    public synchronized boolean deleteFile(String pathToDelete) {
        try {

            synchronized (this) {
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());

                os.write(24);
                os.flush();
                os.writeUTF(pathToDelete);
                os.flush();

                return true;
            }

        } catch (IOException e) {
            ExceptionDialog ed = new ExceptionDialog(e);
            ed.showAndWait();
            return false;
        }
    }

    public synchronized boolean deleteFolder(String pathToDelete) {
        try {

            synchronized (this) {
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());

                os.write(14);
                os.flush();
                os.writeUTF(pathToDelete);
                os.flush();

                return true;
            }

        } catch (IOException e) {
            ExceptionDialog ed = new ExceptionDialog(e);
            ed.showAndWait();
            return false;
        }
    }

    public synchronized boolean createNewFolder(String folderPath) {
        try {

            synchronized (this) {
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());

                os.write(12);
                os.flush();

                os.writeUTF(folderPath);
                os.flush();

                //MainModel.getInstance().requestControllerUpdateDirectory();
                return true;
            }

        } catch (IOException e) {
            ExceptionDialog ed = new ExceptionDialog(e);
            ed.showAndWait();
            return false;
        }
    }

    public void closeConnection() {
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized boolean socketIsRunning() {
        return this.socket.isConnected();
    }

}
