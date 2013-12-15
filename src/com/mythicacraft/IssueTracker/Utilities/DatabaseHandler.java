package com.mythicacraft.IssueTracker.Utilities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.mythicacraft.IssueTracker.IssueTracker;

public class DatabaseHandler {

	private String connUsername;
	private String connPassword;
	private String connHost;
	private String connPort;
	private String connDatabase;
	private Connection conn;
	
	private ResultSet result;
	private PreparedStatement queryStatement;
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public DatabaseHandler(){
		this.connUsername = IssueTracker.username;
		this.connPassword = IssueTracker.password;
		this.connHost = IssueTracker.host;
		this.connPort = IssueTracker.port;
		this.connDatabase = IssueTracker.database;
	}
	
	public void connect() throws SQLException{
        String sqlURL = "jdbc:mysql://" + connHost + ":" + connPort + "/" + connDatabase;
		conn = DriverManager.getConnection(sqlURL, connUsername, connPassword);
	}
	
	public void close() throws SQLException{
		if(queryStatement != null) queryStatement.close(); // Closes statement
		if(result != null) result.close(); // Closes ResultSet
		conn.close(); // Closes the connection
	}
	
	public void CreateTable() throws SQLException { 
		connect();
		DatabaseMetaData dbm = conn.getMetaData();
		ResultSet tables = dbm.getTables(null, null, "itrack_issuetracker", null);
		this.logger.info("Checking for IssueTracker database table....");
		if (!tables.next()) {
			this.logger.info("Table not found, creating table...");
			Statement stmt = conn.createStatement();
			String sql = "CREATE TABLE itrack_issuetracker(issue_id INT AUTO_INCREMENT KEY, player varchar(255) NOT NULL, status int, reason varchar(255))";
			stmt.executeUpdate(sql);
		}
		else
			this.logger.info("Database found! Continuing...");
		close();
	}
	
	//For /issue create
	public void createIssue(String sender, String reason) throws SQLException { 
		connect();
		PreparedStatement queryStatement = conn.prepareStatement("INSERT INTO itrack_issuetracker (player, status, reason) VALUES ('"+ sender + "', 1, '"+ reason + "')"); //Put your query in the quotes
		queryStatement.executeUpdate(); //Executes the query
		close();
	}  

	//Returns issue from DB based on issue ID, MUST be closed after done using ResultSet
	public ResultSet getIssue(int issueID) throws SQLException { 
		connect();
		queryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE issue_ID = " + issueID); //Put your query in the quotes
		result = queryStatement.executeQuery(); //Executes the query
		return result;
	}
	//Sets issue to entered status
	public void setStatus(int statusID, int issueID) throws SQLException { 
		connect();	
		PreparedStatement queryStatement = conn.prepareStatement("UPDATE itrack_issuetracker SET status = " + statusID + " WHERE issue_ID = " + issueID); //Put your query in the quotes
		queryStatement.executeUpdate(); //Executes the query	
		close();
	}	
	
    //Pulls all open/reviewed issues
	public ResultSet getOpenIssues(String auth) throws SQLException { 
		connect();
		queryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE status !=  '3' AND player " + auth); //Put your query in the quotes
		result = queryStatement.executeQuery(); //Executes the query
		return result;
	}
	
	//Pulls closed issues
	public ResultSet getClosedIssues(String auth) throws SQLException { 
		connect();
		queryStatement = conn.prepareStatement("SELECT * FROM itrack_issuetracker WHERE status = '3' AND player " + auth); //Put your query in the quotes
		result = queryStatement.executeQuery(); //Executes the query
		return result;
	}
}
