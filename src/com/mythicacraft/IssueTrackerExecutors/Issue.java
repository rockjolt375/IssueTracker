package com.mythicacraft.IssueTrackerExecutors;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.CommandSender;

public class Issue {
	
	SQLExecutors SQLExec = new SQLExecutors();
	
	private String player;
	private String reason;
	private String status;
	private int issueID;
	private int statusID;
	private boolean exists;
	
	
//Create Issue Constructor Method
	Issue(CommandSender sender, String reason) {
		player = sender.getName();
		try {
			SQLExec.createQuery(player, reason); //Insert new issue to database
		} catch (SQLException e) {e.printStackTrace();}
	}
	
//Issue constructor method	
	Issue(String player, int statusID, int issueID , String reason){
		this.player = player;
		this.issueID = issueID;
		this.statusID = statusID;
		this.status = switchStatus(statusID);
		this.reason = reason;
		exists = true;
	}
	
	
//Issue ID Constructor Method
	Issue(int issueID){
		ResultSet sqlResult;
		try {
			sqlResult = SQLExec.issueQuery(issueID);
			sqlResult.next();
			
			//Populate local variables for the Issue object
			player = sqlResult.getString("player");
			reason = sqlResult.getString("reason");
			this.issueID = sqlResult.getInt("issue_id");
			status = switchStatus(sqlResult.getInt("status"));
			
			exists = true; //Returns boolean, valid row exists
		} catch (SQLException e) {e.printStackTrace();}
	}

//Retrieves String version of status from int value
	public int switchStatus(String status){
		if(status.equalsIgnoreCase("reviewed")){
			statusID = 2;			
		}
		else if(status.equalsIgnoreCase("close") || status.equalsIgnoreCase("closed")){
			statusID = 3;
		}
		else if (status.equalsIgnoreCase("open")){
			statusID = 1;
		}
		return statusID;
	}
		
//Retrieves int value of a status string
	public String switchStatus(int status) {
		if(status == 2) {
			return "Reviewed";
		}
		if(status == 3) {
			return "Closed";
		}
		return "Open";
	}
	
//Sets issue to desired status
	public void setStatus(int issue_ID, int status_ID){
		try {
			SQLExec.SetQuery(status_ID, issue_ID);
		} catch (SQLException e) {e.printStackTrace();}
	}
	
//Identifier for whether or not the database returns an issue
	public boolean exists(){
		return exists;
	}
		
//Getter methods for class variables
	public String getPlayer(){
		return player;
	}
	
	public String getReason(){
		return reason;
	}
	
	public int getStatusID(){
		return statusID;
	}
	
	public String getStatusStr(){
		return status;
	}
	
	public int getIssueID(){
		return issueID;
	}
	
} //End class