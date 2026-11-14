package networking;

import android.content.Intent;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

public final class SessionManager {
    public static void start(Runnable onExit){
        new Thread(() -> {
            try (InputStream in = ConnectionManager.pcSocket.getInputStream()) {
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
}
