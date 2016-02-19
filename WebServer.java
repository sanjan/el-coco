import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer
{
        public static void main(String argv[]) throws Exception
        {
                // Set the port number.
                int port = 8000;
                // Establish the listen socket.
                 // ?
                  ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("WebServer Listening on port: "+ port );
                // Process HTTP service requests in an infinite loop.
                while (true) {
                        // Listen for a TCP connection request.
                        //?
                        Socket socket = serverSocket.accept();
                        InetAddress client = socket.getInetAddress();
                        System.out.println("New connection request from: "+ client.getHostName());
                        // Construct an object to process the HTTP request message.
                        HttpRequest request = new HttpRequest(socket);

                        // Create a new thread to process the request.
                        Thread thread = new Thread(request);

                        // Start the thread.

                        thread.start();
                        System.out.println();

                }
        }
}

