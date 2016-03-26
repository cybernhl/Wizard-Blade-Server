package tools.gui;


import handling.world.ChatServer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class WizardBladeServerGUI extends JFrame{
	protected ChatServer server;
	protected Timer timer;
	protected Socket client;
	protected BufferedReader socketIn;
    protected PrintWriter socketOut;
	
	//UI - JFrame
	private JTextField userText;
	private JTextArea chatWindow;
	private JLabel clientCountLabel;
	
	public WizardBladeServerGUI(ChatServer server){
		createUI();
		this.server = server;
		this.timer = new Timer();
		this.timer.schedule(new UpdateClientCountTask(this.server, this.clientCountLabel), 1000, 1000);
	}
	
	public void appendToChatWindow(String msg){
        try{
        	this.chatWindow.append(msg + "\n");
        }catch (Exception e){
        	System.out.println("Exception GUI error: " + e);
        }
    }
	
	private void createUI(){
		clientCountLabel = new JLabel();
		clientCountLabel.setText("0 clients");
		add(clientCountLabel, BorderLayout.NORTH);
		
		///////-- Create GUI --//////
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(600, 400);
		setVisible(true);
		chatWindow.setEditable(false);
			
		userText = new 	JTextField();
		userText.setEditable(true);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.SOUTH);
		///////-- Create GUI --////////
			
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	protected void sendMessage(String message) {
		this.server.writeToAll("Server Announcement: " + message);
		this.chatWindow.append("Server Anouncement: " + message + "\n");
	}

	public void showMessage(String msg){
		this.chatWindow.append(msg);
	}
}
