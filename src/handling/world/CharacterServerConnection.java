package handling.world;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

import server.Start;

public class CharacterServerConnection extends Thread{
	protected CharacterServer server;
	protected Socket socket;
    private DataInputStream socketIn;
    private DataOutputStream socketOut;
    
    public CharacterServerConnection(Socket socket, CharacterServer server) {
        this.socket = socket;
        this.server = server;
    }
    
    public SocketAddress getRemoteAddress() {
        return this.socket.getRemoteSocketAddress();
    }
    
    protected void debug(String msg) {
        Start.debug("CharacterServerConnection (" + this.socket.getRemoteSocketAddress() + ")", msg);
    }
    
    public void run() {
    	try{
    		//this.socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    		this.socketIn = new DataInputStream(this.socket.getInputStream());
    		this.socketOut = new DataOutputStream(this.socket.getOutputStream());
    		double mapNumber = this.socketIn.readDouble();
    		double axisX = this.socketIn.readDouble();
    		double axisY = this.socketIn.readDouble();
    		double status = this.socketIn.readDouble();
    		double type = this.socketIn.readDouble();
    		double face = this.socketIn.readDouble();
        
        	while (axisX != 1) {
        		//debug("client says '" + axisX + " | " + axisY + "\tstatus: " + status + " type: " + type + " face: " + face);
        		//System.out.println(axisX + " | " + axisY + "\tstatus: " + status + " type: " + type + " face: " + face);
        		
        		if(axisX == 2){
        			if (this.server.remove(this.getRemoteAddress())) {
                        this.finalize();
                        return;
                    }
        		}else{
        			this.server.writeToAllExceptThisTCP(mapNumber, this.getRemoteAddress(), axisX, axisY, status, type, face);
        		}
        		
            	mapNumber = this.socketIn.readDouble();
            	axisX = this.socketIn.readDouble();
            	axisY = this.socketIn.readDouble();
            	status = this.socketIn.readDouble();
            	type = this.socketIn.readDouble();
            	face = this.socketIn.readDouble();
        	}
    	}catch(Exception e){
    		debug("Exception (run): " + e.getMessage());
    	}
        
    }

	public void writeInfo(double mapNumber, double id, double connections, double x, double y, double status, double type, double face) {
		try {
			//restate the double cuz originally i = int
			//(At class:CharacterServer.writeToAllExcpetThisTCP) methods!!!
			//double ii = id;
			synchronized(this.socketOut){
				this.socketOut.writeDouble(mapNumber);
				this.socketOut.writeDouble(id);
				this.socketOut.writeDouble(connections);		//send connections
	            this.socketOut.writeDouble(x);					//send x-axis
	            this.socketOut.writeDouble(y);					//send y-axis
	            this.socketOut.writeDouble(status);				//send player's status
	            this.socketOut.writeDouble(type);				//send player's type 
	            this.socketOut.writeDouble(face);				//send player's facing
	            this.socketOut.flush();
			}
            //System.out.println(mapNumber + " "+ id + " " + connections + " " + x + " " + y + " " + status + " " + type + " " + face);
        } catch (Exception e) {
        	debug("Exception (writeInfo): " + e.getMessage());
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
