import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

/*
* Project 1: Web Server
* Programmer: LaVonne Diller and Nate Kuhn
* Course: CSC 431
* Section: 1
* Instructor: S. Lee
*/

/**
 * The class that handle the HTTP Request
 */
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
		
		// Extract the filename from the request line.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip the method, which should be "GET"
		String fileName = tokens.nextToken();
		
		//Prepend a "." so that file request is within the current directory.
		fileName = "." + fileName;
		
		// Open the request file.
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}
		
		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = "HTTP /1.0 200 OK" + CRLF;
			contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
		} else {
			statusLine = "HTTP /1.0 404 Not Found" + CRLF;
			contentTypeLine = "";
			entityBody = "<!DOCTYPE html><HTML><HEAD><TITLE>Not Found</TITLE></HEAD><BODY>Not Found</BODY></HTML>";
		}
		
		// Sends the status line, content type line, and the entity body to the client.
		os.writeBytes(statusLine);
		os.writeBytes(contentTypeLine);
		os.writeBytes(CRLF);
		
		// Sends the Entity Body if exists
		if (fileExists) {
			sendBytes(fis, os);
			fis.close();
		} else {
			os.writeBytes(entityBody);
		}
		
		//os.writeBytes(CRLF);
		
		// Close Streams and socket.
		os.close();
		br.close();
		socket.close();
	}

	/**
	 * Sends bytes that is the HTTP Response message header & entity body  
	 * @param fis FileInputStream
	 * @param os DataOutputStream
	 * @throws Exception
	 */
	private static void sendBytes(FileInputStream fis, DataOutputStream os) throws Exception {
		// Construct a 1K buffer to hold bytes on their way to the socket.
		 byte[] buffer = new byte[1024];
		 int bytes = 0;
		 
		 // Copy requested file into the socket's output stream.
		 while((bytes = fis.read(buffer)) != -1 ) {
			 os.write(buffer, 0, bytes);
		 }
	}

	/**
	 * Searches filename for the file type requested 
	 * and returns the contentType for the HTTP response message 
	 * @param fileName name of file requested
	 * @return contentType
	 */
	private String contentType(String fileName) {
		if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		else if(fileName.endsWith(".gif")) {
			return "image/gif";
		}
		else if(fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		return "application/octet-stream";
	}

}
