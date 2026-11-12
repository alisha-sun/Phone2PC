import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class DiscoveryServer{
	private static final int PORT = 5001;
	
	public static void main(String[] args) {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		try (DatagramSocket socket = new DatagramSocket(PORT)){
			while (true) {
				System.out.println("Listening...");
				try {
					socket.receive(packet); 
					
					String message = new String(
							packet.getData(), 
							packet.getOffset(), 
							packet.getLength(), 
							StandardCharsets.UTF_8 );
			
					System.out.println("Received: " + message);
					System.out.println("From: " + packet.getAddress().getHostAddress());
				}
				catch (IOException e) {
					e.printStackTrace(); 
				}
			}
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
	}
}