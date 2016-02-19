import java.io.* ;
import java.net.* ;
import java.util.* ;

final class HttpRequest implements Runnable
{
        final static String CRLF = "\r\n";
        Socket socket;


        // Constructor
        public HttpRequest(Socket socket) throws Exception
        {
                this.socket = socket;
        }

        // Implement the run() method of the Runnable interface.
        public void run()
        {
                try {
                        processRequest();
                }
                catch (Exception e) {
                        System.out.println(e);
                }
        }

        private void processRequest() throws Exception
        {

                // Get a reference to the socket's input and output streams.
                InputStream is = socket.getInputStream();
                DataOutputStream os =  new DataOutputStream(socket.getOutputStream());

                // Set up input stream filters.
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                // Get the request line of the HTTP request message.
                String requestLine = br.readLine();

                // Display the request line.
                System.out.println();
                System.out.println(requestLine);

                // Get and display the header lines.
                String headerLine = null;
                while ((headerLine = br.readLine()).length() != 0) {
                        System.out.println(headerLine);
                }

                StringTokenizer tokens = new StringTokenizer(requestLine);
                String requestType = tokens.nextToken();  // Check if the Request is a "GET" or "HEAD"
                String fileName = tokens.nextToken(); // Extract the filename from the request line.
                
                if ((requestType.equals("GET")) || (requestType.equals("HEAD")) ){
                
                fileName = new URI(fileName).normalize().getPath(); // Normalize the requested path
				
				if (fileName.startsWith("../")){
					fileName = ""; // set filename to blank if requesting for a file not inside webroot
				}
				if (fileName.startsWith("/")){
				fileName = "." + fileName; // fix for when requesting files with /
				}
							                
                // Open the requested file.
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
                String contentLengthLine = null;
                String terminateLine = null;
                String entityBody = null;
                int byteCount = 0;
                if (fileExists) {
                        statusLine = "HTTP/1.0 200 OK"+CRLF;
                        contentTypeLine = "Content-Type: " + contentType( fileName ) + CRLF;
                        contentLengthLine = "Content-Length: " + fis.available()+CRLF;

                } else {
                        statusLine = "HTTP/1.0 404 Not Found"+CRLF;
                        contentTypeLine = "Content-Type: text/html"+CRLF;

                        entityBody = "<HTML>" +
                                "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                                "<BODY>Not Found</BODY></HTML>";
                        contentLengthLine = "Content-Length: "+ entityBody.getBytes("US-ASCII").length + CRLF;

                }

                // Send the status line.
                os.writeBytes(statusLine);

                // Send the headers.
                os.writeBytes(contentTypeLine + contentLengthLine);



                // Send a blank line to indicate the end of the header lines.
                os.writeBytes(CRLF);

                // Send the entity body.
                if (requestType.equals("GET")){
                if (fileExists) {
                        sendBytes(fis, os);
                        fis.close();
                } else {
                        os.writeBytes(entityBody);
                }
                }

				} //exit request type validation
                
                // Close streams and socket.
                os.close();
                br.close();
                is.close();
                socket.close();
                

        }

        private static void sendBytes(FileInputStream fis, OutputStream os)
        throws Exception
        {
           // Construct a 1K buffer to hold bytes on their way to the socket.
           byte[] buffer = new byte[1024];
           int bytes = 0;

           // Copy requested file into the socket's output stream.
           while((bytes = fis.read(buffer)) != -1 ) {
              os.write(buffer, 0, bytes);
           }


        }

        private static String contentType(String fileName)
        {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
                return "text/html";
        }
        if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                return "image/jpeg";
        }
        if(fileName.endsWith(".gif")) {
                return "image/gif";
        }
        if(fileName.endsWith(".txt")){
                    return "text/plain";
        }

        return "application/octet-stream";

        }

}//end class
