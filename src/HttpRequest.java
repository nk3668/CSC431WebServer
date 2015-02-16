import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public final class HttpRequest implements Runnable {

	final static String CRLF = "\r\n";
	Socket socket;
	
	// Constructor
	public HttpRequest (Socket socket) throws Exception {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void processRequest() throws Exception {
		// Get a reference to the socket's input and output streams
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		
		// Set up input Stream filters
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		// Get the request line of the HTTP request message
		String requestLine = br.readLine();
		
		// Display the request line
		System.out.println();
		System.out.println(requestLine);
		
		// Get and display the header lines.
		String headerLine = null;
		while((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}
		
		// Close Streams and socket.
		os.close();
		br.close();
		socket.close();
	}

}
