package handling.login;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;
import java.util.Vector;

import server.Start;
import database.DatabaseConnection;


public class LoginServer extends Thread{
	protected ServerSocket socketServer;
    protected int port;
    protected boolean listening;
    protected Vector<LoginServerConnection> clientConnections;
	
	public LoginServer(int serverPort){
		this.port = serverPort;
    	this.clientConnections = new Vector<LoginServerConnection>();
    	this.listening = false;
	}
	
	public int getPort() {
        return this.port;
    }
    
	public int getClientCount() {
        return this.clientConnections.size();
    }
    
	protected void debug(String msg) {
		Start.debug("LoginServer (" + this.port + ")", msg);
    }
	
	public void run(){
    	try{
    		this.socketServer = new ServerSocket(this.port);
            this.listening = true;
            debug("listening");
            
            while(this.listening){
            	Socket socket = this.socketServer.accept();
            	debug("WBO client connection from " + socket.getRemoteSocketAddress());
            	LoginServerConnection socketConnection = new LoginServerConnection(socket, this);
                socketConnection.start();
                this.clientConnections.add(socketConnection);
            }
    	}catch(Exception e){
    		debug(e.getMessage());
    	}
    }
	
	public void checkStatusAndWriteToClient(SocketAddress remoteAddress, int status){
		try{
    		for (int i = 0; i < this.clientConnections.size(); i++) {
    			LoginServerConnection client = this.clientConnections.get(i);
            	if(client.getRemoteAddress().equals(remoteAddress)){
            		
            		//Connection dbConn = Start.db.checkAccountInfo(remoteAddress, username, password);
            		client.sendAvaAccountAndPassword(status);//0: offline, 1: online
            	}
            }
            //debug("Status: " + status, "Username: " + account,"Password: " + password);
    	}catch(Exception e){
    		debug("Exception (checkStatusAndWriteToClient): " + e.getMessage());
    	}
	}
	
	public boolean remove(SocketAddress remoteAddress) {
        try {
            for (int i = 0; i < this.clientConnections.size(); i++) {
            	LoginServerConnection client = this.clientConnections.get(i);

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
