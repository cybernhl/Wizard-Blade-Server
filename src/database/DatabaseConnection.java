package database;

import handling.login.LoginServer;

import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import server.Start;

public class DatabaseConnection {
	protected String driver;
	protected String url;
	protected String user;
	protected String password;
	
	public DatabaseConnection(String driver,String url,String user,String password){
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	protected void debug(String msg) {
		Start.debug("DatabaseConnection", msg);
	}
	
	public Connection dbConnect(){
		try
		{
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(
					url, user, password);
                
			String query = "SELECT * FROM accounts";		//table name
                
			Statement st = conn.createStatement();
                
			ResultSet rs = st.executeQuery(query);
                
	/*		while (rs.next())
			{
				int id = rs.getInt("id");
				String username = rs.getString("username");
				String password = rs.getString("password");
				int ss = rs.getInt("account_status");				//0: online, 1: offline
				//Example!!! (boolean, int, etc.)
				//Date dateCreated = rs.getDate("date_created");
				//boolean isAdmin = rs.getBoolean("is_admin");
				// int numPoints = rs.getInt("num_points");
				
				// print the results
				//	%s = 1 data block!!!
				System.out.format("%s, %s, %s, %s\n", id, username, password, ss);
			}
			st.close();*/
			debug("DRIVER connect-> " + driver);
			debug("URL connect-> " + url);
			return conn;
                
		}catch (Exception e){
                e.printStackTrace();
                debug("Exception(Database): " + e);
                return null;
        }
	}
	
	public Connection checkAccountInfo(LoginServer server,SocketAddress remoteAddress, String name, String pass){
		try{
			Class.forName(driver).newInstance();
			Connection dbConnection = DriverManager.getConnection(
					url, user, password);
			dbConnection.setAutoCommit(false);
			
			Statement st = dbConnection.createStatement();
			
			//Check 'USERNAME' and 'PASSWORD'
			String query = "SELECT * FROM accounts";
			ResultSet rs = st.executeQuery(query);
			
			//update 'ACCOUNT_STATUS'
			String updateStatus = "UPDATE accounts SET account_status = ? WHERE username = ?";
			PreparedStatement updateAccountStatus = dbConnection.prepareStatement(updateStatus);
			
			String databaseUsername = "";
			String databasePassword = "";
			
			int status = 0;		//0: offline, 1: online
			
			while (rs.next()) {
		        databaseUsername = rs.getString("username");
		        databasePassword = rs.getString("password");
		        status = rs.getInt("account_status");
		        
		        if (name.equals(databaseUsername) && pass.equals(databasePassword)) {
		            System.out.println("Checking status...\n----");
		            if(status == 0){
		            	System.out.println("Successful Login!\n----");
		            	updateAccountStatus.setInt(1, status + 1);				//set 'status'
		            	updateAccountStatus.setString(2, name);			//find which 'user'
		            	updateAccountStatus.executeUpdate();
		            	dbConnection.commit();
		            	break;
		            }else{
		            	System.out.println("This account is still online! Plz try again later!\n----");
		            	break;
		            }
		        }
		        
		        System.out.println("Incorrect Password\n----");
		        status = 2;
		    }
			
			server.checkStatusAndWriteToClient(remoteAddress, status);
			
			updateAccountStatus.close();
			st.close();
			rs.close();
			
			return dbConnection;
		}catch(Exception e){
			e.printStackTrace();
            debug("Exception(Database): " + e);
            return null;
		}
	}
	
	public Connection logoutRequest(LoginServer server,SocketAddress remoteAddress, String name, String pass){
		try{
			Class.forName(driver).newInstance();
			Connection dbConnection = DriverManager.getConnection(
					url, user, password);
			dbConnection.setAutoCommit(false);
			
			Statement st = dbConnection.createStatement();
			
			//Check 'USERNAME' and 'PASSWORD'
			String query = "SELECT * FROM accounts";
			ResultSet rs = st.executeQuery(query);
			
			//update 'ACCOUNT_STATUS'
			String updateStatus = "UPDATE accounts SET account_status = ? WHERE username = ?";
			PreparedStatement updateAccountStatus = dbConnection.prepareStatement(updateStatus);
			
			String databaseUsername = "";
			String databasePassword = "";
			
			int status = 1;		//0: offline, 1: online
			
			while (rs.next()) {
		        databaseUsername = rs.getString("username");
		        databasePassword = rs.getString("password");
		        status = rs.getInt("account_status");
		        
		        if (name.equals(databaseUsername) && pass.equals(databasePassword)) {
		            //System.out.println("Checking status...\n----");
		            if(status == 1){
		            	//System.out.println("Successful Login!\n----");
		            	System.out.println("Change status back to '0'!\n----");
		            	updateAccountStatus.setInt(1, status - 1);				//set 'status'
		            	updateAccountStatus.setString(2, name);			//find which 'user'
		            	updateAccountStatus.executeUpdate();
		            	dbConnection.commit();
		            	break;
		            }else{
		            	//System.out.println("This account is still online! Plz try again later!\n----");
		            	break;
		            }
		        }
		        
		        //System.out.println("Incorrect Password\n----");
		        //status = 2;
		    }
			
			//server.checkStatusAndWriteToClient(remoteAddress, status);
			
			System.out.println("Successful Logout!\n----");
			
			updateAccountStatus.close();
			st.close();
			rs.close();
			
			return dbConnection;
		}catch(Exception e){
			e.printStackTrace();
			debug("Exception(Database): " + e);
			return null;
		}
	}
}
