import java.net.ServerSocket;
import java.net.Socket;


public final class WebServer {

	public static void main(String[] args) throws Exception {
		ServerSocket server = new ServerSocket(8080);
		// Establish the listen socket.
		Socket sock = new Socket();

		// Process HTTP service requests in an infinite loop.
		while(true) {
			// Wait for an incoming request
			sock = server.accept();
			
			// Construct an object to process the HTTP request message.
			HttpRequest request = new HttpRequest(sock);
			
			// Create a new thread to process the request.
			Thread thread = new Thread(request);
			thread.start();
			
		}

	}

}
