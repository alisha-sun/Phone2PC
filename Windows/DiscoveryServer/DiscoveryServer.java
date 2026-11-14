import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class DiscoveryServer{
	private static final int PORT = 5001;
	private static final byte[] DATA = "PHONE2PC_DISCOVER".getBytes(StandardCharsets.UTF_8);
	private static final String PATH = "../PhoneConnection/main.exe";
	
	public static void main(String[] args) {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		ProcessBuilder builder = new ProcessBuilder(PATH);
		
		try (DatagramSocket socket = new DatagramSocket(PORT)){
			while (true) {
				System.out.println("Listening...");
				try {
					socket.receive(packet); 
					if (packetEquals(DATA, packet)) {
						builder.command(PATH, packet.getAddress().getHostAddress());
						Process process = builder.start();
						System.out.println("Started: " + process.pid());
						System.out.println("Address: " + packet.getAddress().getHostAddress());
					}
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
	
	private static boolean packetEquals(byte[] expected, DatagramPacket packet) {
	    if (expected.length != packet.getLength()) return false;

	    byte[] data = packet.getData();
	    int offset = packet.getOffset();

	    for (int i = 0; i < expected.length; i++) {
	        if (expected[i] != data[offset + i]) return false;
	    }

	    return true;
	}
}