package networking;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public final class SessionManager {
    static volatile Socket socket = null;
    public static void start(Runnable onExit){
        new Thread(() -> {
            try (InputStream in = socket.getInputStream()) {
                while (in.read() != -1) {
                    // ...
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                System.out.println("Connection closed.");
                onExit.run();
            }
        }).start();
    }

    public static void closeConnection(){
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
