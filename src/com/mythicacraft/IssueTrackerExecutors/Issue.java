package com.mythicacraft.IssueTrackerExecutors;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Issue {
	
	SQLExecutors SQLExec = new SQLExecutors();
	
	private String player;
	private String reason;
	private String status;
	private static int statusID;
	private boolean exists;
	
	
//Create Issue Constructor Method
	public Issue(String playerName, String reason) {
		try {
			SQLExec.createQuery(playerName, reason); //Insert new issue to database
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//Issue Modify Constructor Method
	public Issue(int issueID, int statusID){
		try {
			SQLExec.SetQuery(statusID, issueID); //Set issue to reviewed or closed
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//Issue ID Constructor Method
	public Issue(int issueID){
		ResultSet sqlResult;
		try {
			sqlResult = SQLExec.adminIssueQuery(issueID);
			while(sqlResult.next()){
				player = sqlResult.getString("player");
				reason = sqlResult.getString("reason");
				status = getStatus(sqlResult.getInt("status"));
				exists = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//Retrieves String version of status from int value
	public static int getStatusID(String status){
		if(status.equalsIgnoreCase("reviewed")){
			statusID = 2;			
		}
		else if(status.equalsIgnoreCase("close") || status.equalsIgnoreCase("closed")){
			statusID = 3;
		}
		else{
			statusID = 1; //Returns Open issue
		}
		return statusID;
	}
	
//Retrieves int value of a status string
	public String getStatus(int status) {
		if(status == 2) {
			return "Reviewed";
		}
		if(status == 3) {
			return "Closed";
		}
		return "Open";
	}
	
//Identifier for if the database returns an issue or not
	public boolean exists(){
		return exists;
	}
	
	

	
	

	
} //End class