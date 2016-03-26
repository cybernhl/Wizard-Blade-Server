package handling.login;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;

import server.Start;

public class LoginServerConnection extends Thread{
	protected LoginServer server;
	protected Socket socket;
    private DataInputStream socketIn;
    private DataOutputStream socketOut;
    
    public LoginServerConnection(Socket socket, LoginServer server) {
        this.socket = socket;
        this.server = server;
    }
    
    public SocketAddress getRemoteAddress() {
        return this.socket.getRemoteSocketAddress();
    }
    
    protected void debug(String msg) {
        Start.debug("LoginServerConnection (" + this.socket.getRemoteSocketAddress() + ")", msg);
    }
    
    public void run(){
    	try {
    		this.socketIn = new DataInputStream(this.socket.getInputStream());
            this.socketOut = new DataOutputStream(this.socket.getOutputStream());
            String username = this.socketIn.readUTF();
            String password = this.socketIn.readUTF();
            String lastString = this.socketIn.readUTF();
            
            while (true) {
            	//if button Logout click!!! (In Game)
            	if(lastString.compareToIgnoreCase("\\logout") == 0){
            		Connection dbConn = Start.db.logoutRequest(server, this.getRemoteAddress(), username, password);
            	}
            	//if button Exit click!!! (Login Screen)
            	else if(lastString.compareToIgnoreCase("\\quit") == 0){
            		if (this.server.remove(this.getRemoteAddress())) {
                        this.finalize();
                        return;
                    }
            	}
            	//else run apply login request (loading information and prepared login to game)
            	else{
	            	Connection dbConn = Start.db.checkAccountInfo(server, this.getRemoteAddress(), username, password);
            	}
            	
            	username = this.socketIn.readUTF();
                password = this.socketIn.readUTF();
                lastString = this.socketIn.readUTF();
            }
        }
        catch (Exception e) {
            debug("Exception (run): " + e.getMessage());
        }
    }
    
    public void sendAvaAccountAndPassword(int status){
    	try {
    		this.socketOut.writeInt(status);
            this.socketOut.flush();
        } catch (Exception e) {
        	debug("Exception (sendAvaAccountAndPassword): " + e.getMessage());
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
