package tools.gui;

import handling.world.ChatServer;

import java.util.TimerTask;

import javax.swing.JLabel;


public class UpdateClientCountTask extends TimerTask{
     protected JLabel clientsLabel;
	 protected ChatServer wizardBladeServer;
	 
	 public UpdateClientCountTask(ChatServer wizardBladeServer, JLabel clientsLabel) {
	        this.wizardBladeServer = wizardBladeServer;
	        this.clientsLabel = clientsLabel;
	 }
	 
	 public void run() {
	        int count = this.wizardBladeServer.getClientCount();
	        String msg = count + " client" + ((count != 1) ? "s" : "");
	        this.clientsLabel.setText(msg);
	 }  
}
