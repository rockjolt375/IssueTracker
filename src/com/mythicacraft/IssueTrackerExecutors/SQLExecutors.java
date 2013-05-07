package com.mythicacraft.IssueTrackerExecutors;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import com.mythicacraft.IssueTracker.cIssueTracker;

public class SQLExecutors {
    public static String sqlURL;
    public final Logger logger = Logger.getLogger("Minecraft");

    public static ResultSet selectSQL;
    public PreparedStatement statusQueryStatement;
    public static PreparedStatement sampleQueryStatement;
    public static Connection conn;
    public String errorString;
    public static boolean openIssue;
    
  //Creates the connection
    public static void dbConnect() throws SQLException {
    	sqlURL = "jdbc:mysql://" + cIssueTracker.sqlHost + ":" + cIssueTracker.sqlPort+ "/" + cIssueTracker.sqlDbase;
		conn = DriverManager.getConnection(sqlURL, cIssueTracker.sqlUser, cIssueTracker.sqlPass);
    }
    
   //Closes open connections
    public void dbClose() throws SQLException {
		sampleQueryStatement.close(); //Closes the query
		conn.close(); //Closes the connection
    }
  
	//For /issue create
	public void createQuery(CommandSender sender, String reason) throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("INSERT INTO itrack_issuetracker (player, status, reason) VALUES ('"+ sender + "', 1, '"+ reason + "')"); //Put your query in the quotes
		sampleQueryStatement.executeUpdate(); //Executes the query
		dbClose();
	}  

	//Returns issue from DB based on issue ID
	public ResultSet issueQuery(int issueID) throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE issue_ID = " + issueID); //Put your query in the quotes
		ResultSet sqlSelect = sampleQueryStatement.executeQuery(); //Executes the query
		return sqlSelect;
	}
	//Sets issue to entered status
	public void SetQuery(int statusID, int issueID) throws SQLException { 
		dbConnect();	
		try{
		sampleQueryStatement = conn.prepareStatement("UPDATE itrack_issuetracker SET status = " + statusID + " WHERE issue_ID = " + issueID); //Put your query in the quotes
		sampleQueryStatement.executeUpdate(); //Executes the query	
		}
		catch (Exception e){
		}
		dbClose();
	}	
	
    //Pulls all open/reviewed issues
	public ResultSet statusQuery(String auth) throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE status !=  '3' AND player " + auth); //Put your query in the quotes
		ResultSet sqlSelect = sampleQueryStatement.executeQuery(); //Executes the query
		selectSQL = sqlSelect;
		return sqlSelect;
	}
	
	//Pulls closed issues
	public ResultSet closeQuery(String auth) throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE status = '3' AND player " + auth); //Put your query in the quotes
		ResultSet sqlSelect = sampleQueryStatement.executeQuery(); //Executes the query
		selectSQL = sqlSelect;
		return sqlSelect;
	}	
	
	//Called onEnable - checks if database exists, if not - creates database
	public void CreateTable() throws SQLException { 
		dbConnect();
		DatabaseMetaData dbm = conn.getMetaData();
		ResultSet tables = dbm.getTables(null, null, "itrack_issuetracker", null);
		this.logger.info("Checking for IssueTracker database table....");
		if (!tables.next()) {
			this.logger.info("Table not found, creating table");
			Statement stmt = conn.createStatement();
			String sql = "CREATE TABLE itrack_issuetracker(issue_id INT AUTO_INCREMENT KEY, player varchar(255) NOT NULL, status int, reason varchar(255))";
			stmt.executeUpdate(sql);
		}
		conn.close();
	}
	
	
	
	
	
	
	
	
/* Unused SQL for now
	
    //For /issue view <ticket ID>
	public void issueQuery() throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE issue_ID = " + IssueCommand.issueID + " AND player = '" + IssueCommand.senderName + "'"); //Put your query in the quotes
		ResultSet sqlSelect = sampleQueryStatement.executeQuery(); //Executes the query
		selectSQL = sqlSelect;
	}
	
	//For /issue close
	public void closeQuery() throws SQLException { 
		dbConnect();
		String tempname = null;
		//Connect to pull players name from database
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE issue_ID = " + IssueCommand.closeIssueID + " AND player = '" + IssueCommand.senderName + "'"); //Put your query in the quotes
		ResultSet nameCheck = sampleQueryStatement.executeQuery();
		while (nameCheck.next()){
		tempname = nameCheck.getString("player");
		}
		dbClose();	
		//Check players name in database is equal to the command sender and fire the SQL update
			if (tempname.matches(IssueCommand.senderName)){		
				dbConnect();
				sampleQueryStatement = conn.prepareStatement("UPDATE itrack_issuetracker SET status = 3 WHERE issue_ID = " + IssueCommand.closeIssueID); //Put your query in the quotes
				sampleQueryStatement.executeUpdate(); //Executes the query	
				}
		dbClose();
	}
	
	public static void adminStatusQuery() throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE status !=  '3'"); //Put your query in the quotes
		ResultSet sqlSelect = sampleQueryStatement.executeQuery(); //Executes the query
		selectSQL = sqlSelect;
		
		//Check if open tickets are present
		if(!selectSQL.next()){
			openIssue = false;
			selectSQL.beforeFirst();
		}
		else {
			openIssue = true;
			selectSQL.beforeFirst();
		}
	}
	
	public void viewCloseQuery() throws SQLException { 
		dbConnect();
		sampleQueryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE status = '3' AND player = '" + IssueCommand.senderName + "'"); //Put your query in the quotes
		ResultSet sqlSelect = sampleQueryStatement.executeQuery(); //Executes the query
		selectSQL = sqlSelect;
	}
	
	*/
}
