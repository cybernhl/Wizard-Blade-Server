package server;

import handling.login.LoginServer;
import handling.world.CharacterServer;
import handling.world.ChatServer;
import tools.PolicyServer;
import tools.gui.WizardBladeServerGUI;
import database.DatabaseConnection;


public class Start {
	private static final boolean DEBUG = true;					//check debug
	private static int portChatServer = 5757;					//port (chat server)
	private static int policyPort = portChatServer + 1;			//policy port
	private static int portCharacterServer = 6666;				//port (character server)
	private static int portLoiginServer = 8484;					//port (login server)
	
	//Server
	private static PolicyServer policyServer = new PolicyServer(policyPort);		//Policy Server setup
	private static ChatServer chatServer = new ChatServer(portChatServer);					//Chat Server
	private static CharacterServer characterServer = new CharacterServer(portCharacterServer);		//Character Server
	private static WizardBladeServerGUI wizardBladeServerGUI =						//GUI setup
			new WizardBladeServerGUI(chatServer);									//
	private static LoginServer loginServer = new LoginServer(portLoiginServer);
    
	//Database
	public static String driver = "com.mysql.jdbc.Driver";
	public static String url = "jdbc:mysql://localhost:3307/wbdb";
	public static String user = "root";
	public static String password = "";
	public static DatabaseConnection db = new DatabaseConnection(driver, url, user, password);
	
	
    /**
     * Debug and Send the message to GUI
     */
    public static void debug(String label, String msg) {
        if (DEBUG && Start.wizardBladeServerGUI != null) {
            Start.wizardBladeServerGUI.appendToChatWindow(label + ": " + msg);
        }
    }
	
	public final static void main(final String[] args) {
   	 try {
   		 System.out.println("Server is initialize...");
   		 debug("Server","Server is initialize...");
   		 
   		 //Start all server
   		 policyServer.start();
   		 loginServer.start();
   		 chatServer.start();
   		 characterServer.start();
   		 
   		 //Setup simple server GUI
   		 wizardBladeServerGUI.setTitle("Wizard Blade Server");
   		 wizardBladeServerGUI.setLocationRelativeTo(null);
   		 wizardBladeServerGUI.setVisible(true);
   		 
   		 //Connect to Database
   		 db.dbConnect();
   		 
   		 debug("-------", "-----------------------------------------------------------------------------------");
   		 debug("Server", "All Server initialized and Database connected properly...");
   		 debug("-------", "-----------------------------------------------------------------------------------");
   	 }
   	 catch (Exception e) {
   		 debug("Main", "Exception (main)" + e.getMessage());
   	 }
    }
}
