package handling.world;



import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Vector;

import server.Start;



public class ChatServer extends Thread{
	protected ServerSocket socketServer;
    protected int port;
    protected boolean listening;
    protected Vector<ChatServerConnection> clientConnections;
    
    public ChatServer(int serverPort) {
        this.port = serverPort;
        this.clientConnections = new Vector<ChatServerConnection>();
        this.listening = false;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public int getClientCount() {
        return this.clientConnections.size();
    }
    
    protected void debug(String msg) {
        Start.debug("ChatServer (" + this.port + ")", msg);
    }
    
	public void run() {
        try {
            this.socketServer = new ServerSocket(this.port);
            this.listening = true;
            //System.out.println("Wizard Blade Server- listening");
            debug("listening");
            
            while (this.listening) {
                Socket socket = this.socketServer.accept();
                System.out.println("WBO client connection from " + socket.getRemoteSocketAddress());
                debug("WBO client connection from " + socket.getRemoteSocketAddress());
                ChatServerConnection socketConnection = new ChatServerConnection(socket, this);
                socketConnection.start();
                this.clientConnections.add(socketConnection);
            }
        }
        catch (Exception e) {
        	debug(e.getMessage());
        }
    }
	
	public void writeToAll(String msg) {
        try {
            for (int i = 0; i < this.clientConnections.size(); i++) {
            	ChatServerConnection client = this.clientConnections.get(i);
                client.write(msg);
            }
            debug("broadcast message '" + msg + "' was sent");
        }
        catch (Exception e) {
        	debug("Exception (writeToAll): " + e.getMessage());
        }
    }
	
	public boolean remove(SocketAddress remoteAddress) {
        try {
            for (int i = 0; i < this.clientConnections.size(); i++) {
            	ChatServerConnection client = this.clientConnections.get(i);

                if(client.getRemoteAddress().equals(remoteAddress)) {
                    this.clientConnections.remove(i);
                    //System.out.println("client " + remoteAddress + " was removed");
                    debug("client " + remoteAddress + " was removed");
                    writeToAll(remoteAddress + " has disconnected.");

                    return true;
                }
            }
        }
        catch (Exception e) {
        	debug("Exception (remove): " + e.getMessage());
        }
        
        return false;
    }
	
	protected void finalize() {	 
        try {
            this.socketServer.close();
            this.listening = false;
            debug("stopped");
        }
        catch (Exception e) {
        	debug("Exception (finalize): " + e.getMessage());
        }
    }
}
