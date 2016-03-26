package handling.world;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

import server.Start;

public class ChatServerConnection extends Thread{
	protected ChatServer server;
	protected Socket socket;
    protected BufferedReader socketIn;
    protected PrintWriter socketOut;
    
    public ChatServerConnection(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }
    
    public SocketAddress getRemoteAddress() {
        return this.socket.getRemoteSocketAddress();
    }
    
    /**
     * Roots a debug message to the main application.
     * 
     * @param msg The debug message to be sent to the main application
     */
    protected void debug(String msg) {
        Start.debug("ChatServerConnection (" + this.socket.getRemoteSocketAddress() + ")", msg);
    }
    
    public void run() {
        try {
            this.socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.socketOut = new PrintWriter(this.socket.getOutputStream(), true);
            this.server.writeToAll(this.getRemoteAddress() + " has connected.");					//Tell everyone who is connected
            String line = this.socketIn.readLine();

            while (line != null) {
            	debug("client says '" + line + "'");
                
                // If it's a quit command, we remove the client from the server and exit
                if (line.compareToIgnoreCase("\\quit") == 0) {
                    if (this.server.remove(this.getRemoteAddress())) {
                        this.finalize();
                        return;
                    }
                }
                this.server.writeToAll(this.getRemoteAddress() + ": " + line);
                line = this.socketIn.readLine();
            }
        }
        catch (Exception e) {
            debug("Exception (run): " + e.getMessage());
        }
    }
    
    public void write(String msg) {
        try {
            this.socketOut.write(msg + "\u0000");
            this.socketOut.flush();
        }
        catch (Exception e) {
        	debug("Exception (write): " + e.getMessage());
        }
    }
    
    protected void finalize() {	 
        try {
            this.socketIn.close();
            this.socketOut.close();
            this.socket.close();
            debug("connection closed");
        }
        catch (Exception e) {
        	debug("Exception (finalize): " + e.getMessage());
        }
    }
    
}
