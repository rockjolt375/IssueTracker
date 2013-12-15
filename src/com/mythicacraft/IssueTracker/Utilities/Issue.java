package com.mythicacraft.IssueTracker.Utilities;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Issue {

	DatabaseHandler dbHand = new DatabaseHandler();
	
	int issueID;
	int statusID;
	String player;
	String status;
	String reason;
	boolean exists = false;
	
	//Create Issue Constructor Method
		Issue(String player, String reason) {
			try {
				dbHand.createIssue(player, reason); //Insert new issue to database
			} catch (SQLException e) {e.printStackTrace();}
		}
		
	//Issue constructor method	
		Issue(String player, int statusID, int issueID , String reason){
			this.player = player;
			this.issueID = issueID;
			this.statusID = statusID;
			this.status = switchStatus(statusID);
			this.reason = reason;
			this.exists = true;
		}
		
	//Issue ID Constructor Method
		Issue(int issueID){
			ResultSet sqlResult;
			try {
				sqlResult = dbHand.getIssue(issueID);
				sqlResult.next();
				this.player = sqlResult.getString("player");
				this.reason = sqlResult.getString("reason");
				this.issueID = sqlResult.getInt("issue_id");
				this.status = switchStatus(sqlResult.getInt("status"));
				this.exists = true;
				sqlResult.close();
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
		public void setStatus(int status_ID){
			try {
				dbHand.setStatus(status_ID, issueID);
			} catch (SQLException e) {e.printStackTrace();}
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
		
		public String getStatusString(){
			return status;
		}
		
		public int getIssueID(){
			return issueID;
		}
		
		public boolean exists(){
			return exists;
		}
}
