package handling.world;


import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Vector;

import server.Start;

public class CharacterServer extends Thread{
	protected ServerSocket socketServer;
	protected Socket socket;
    protected int port;
    protected boolean listening;
    protected Vector<CharacterServerConnection> clientConnections;
    
    public CharacterServer(int serverPort){
    	this.port = serverPort;
    	this.clientConnections = new Vector<CharacterServerConnection>();
    	this.listening = false;
    }
    
    public int getPort(){
    	return this.port;
    }
    
    protected void debug(String msg){
    	Start.debug("CharacterServer (" + this.port + ")", msg);
    }
    
    public void run(){
    	synchronized(this){
	    	try{
	    		this.socketServer = new ServerSocket(this.port);
	            this.listening = true;
	            debug("listening");
	            
	            while(this.listening){
	            	Socket socket = this.socketServer.accept();
	            	debug("WBO client connection from " + socket.getRemoteSocketAddress());
	            	CharacterServerConnection socketConnection = new CharacterServerConnection(socket, this);
	                socketConnection.start();
	                this.clientConnections.add(socketConnection);
	            }
	    	}catch(Exception e){
	    		debug(e.getMessage());
	    	}
    	}
    }
    
    /**
     * Send to every client player's position X and Y axis,
     * @param remoteAddress: Get TCP port form client
     * @param x: value of Player's x axis
     * @param y: value of Player's y axis
     */
    public void writeToAllExceptThisTCP(double mapNumber, SocketAddress remoteAddress, double x, double y, double status, double type, double face){
    	try{
    		for (int i = 0; i < this.clientConnections.size(); i++) {
    			double count = i;
            	CharacterServerConnection client = this.clientConnections.get(i);
            	if(client.getRemoteAddress().equals(remoteAddress)){
            		//skip this, do not send to itself
            	}else{
            		System.out.println("Hello?: " + remoteAddress);
            		double size = this.clientConnections.size();
            		client.writeInfo(mapNumber, count, size, x, y, status, type, face);
            	}
            }
            //debug("Player's Position sended: |Connections: " + this.clientConnections.size() + " |x: " + x + " |y: " + y + " |status: " + status + " |type: " + type + " |face: " + face);
    	}catch(Exception e){
    		debug("Exception (writeToAllExceptThisTCP): " + e.getMessage());
    	}
    }
    
    public boolean remove(SocketAddress remoteAddress) {
        try {
            for (int i = 0; i < this.clientConnections.size(); i++) {
            	CharacterServerConnection client = this.clientConnections.get(i);

                if(client.getRemoteAddress().equals(remoteAddress)) {
                    this.clientConnections.remove(i);
                    //System.out.println("client " + remoteAddress + " was removed");

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
