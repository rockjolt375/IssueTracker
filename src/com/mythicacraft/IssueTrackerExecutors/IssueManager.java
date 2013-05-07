package com.mythicacraft.IssueTrackerExecutors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class IssueManager {
	
	SQLExecutors SQLExec = new SQLExecutors();
	ArrayList<Integer> issueList = new ArrayList<Integer>();
	
	public Issue getIssue(int issue_ID){
		Issue oldIssue = new Issue(issue_ID);
		return oldIssue;
	}

	public Issue createIssue(CommandSender sender, String reason) {
		Issue newIssue = new Issue(sender, reason);
		return newIssue;
	}
		
	public Issue[] getOpenIssues(){
		String sqlQuery = "IS NOT NULL"; //Sets the query parameters for all open/reviewed issues without player restriction
		try {
			ResultSet sqlResult = SQLExec.statusQuery(sqlQuery);
			while(sqlResult.next()){
			issueList.add(sqlResult.getInt("issue_id")); //Adds issue_ID into ArrayList
			}
			SQLExec.dbClose(); //Closes database connection
		} catch (SQLException e) {e.printStackTrace();}
		
		//Creates object array using Issue(int issue_ID)
		Issue[] allIssues = new Issue[issueList.size()];
		for(int i = 0; i < issueList.size(); i++) {
			allIssues[i] = new Issue(issueList.get(i));			
		}
		return allIssues;
	}
	
	public Issue[] getOpenIssues(String playerName){
		String sqlQuery = "= '" + playerName + "'"; //Sets the query parameters for all open/reviewed issues for specified player
		try {
			ResultSet sqlResult = SQLExec.statusQuery(sqlQuery);
			while(sqlResult.next()){
			issueList.add(sqlResult.getInt("issue_id")); //Adds issue_ID into ArrayList
			}
			SQLExec.dbClose(); //Closes database connection
		} catch (SQLException e) {e.printStackTrace();}
		
		//Creates object array using Issue(int issue_ID)
		Issue[] allIssues = new Issue[issueList.size()];
		for(int i = 0; i < issueList.size(); i++) {
			allIssues[i] = new Issue(issueList.get(i));			
		}
		return allIssues;
	}
	
	public Issue[] getClosedIssues(){
		String sqlQuery = "IS NOT NULL"; //Sets the query parameters for all open/reviewed issues without player restriction
		try {
			ResultSet sqlResult = SQLExec.closeQuery(sqlQuery);
			while(sqlResult.next()){
			issueList.add(sqlResult.getInt("issue_id")); //Adds issue_ID into ArrayList
			}
			SQLExec.dbClose(); //Closes database connection
		} catch (SQLException e) {e.printStackTrace();}
		
		//Creates object array using Issue(int issue_ID)
		Issue[] closedIssues = new Issue[issueList.size()];
		for(int i = 0; i < issueList.size(); i++) {
			closedIssues[i] = new Issue(issueList.get(i));			
		}
		return closedIssues;
	}
	
	public Issue[] getClosedIssues(String playerName){
		String sqlQuery = "= '" + playerName + "'"; //Sets the query parameters for all open/reviewed issues for specified player
		try {
			ResultSet sqlResult = SQLExec.closeQuery(sqlQuery);
			while(sqlResult.next()){
			issueList.add(sqlResult.getInt("issue_id")); //Adds issue_ID into ArrayList
			}
			SQLExec.dbClose(); //Closes database connection
		} catch (SQLException e) {e.printStackTrace();}
		
		//Creates object array using Issue(int issue_ID)
		Issue[] closedIssues = new Issue[issueList.size()];
		for(int i = 0; i < issueList.size(); i++) {
			closedIssues[i] = new Issue(issueList.get(i));			
		}
		return closedIssues;
	}
	
	public String convertIssueToMessage(Issue issue, String auth) {
		String issueMessage = "", reason = shortenIssue(issue.getReason()), player = issue.getPlayer(), status = issue.getStatusStr(), issueID = Integer.toString(issue.getIssueID());
		if(auth == "player"){
			issueMessage = ChatColor.BLUE + "Issue ID: " + ChatColor.GOLD + issueID + ChatColor.BLUE + " - Status: " + ChatColor.GOLD + status + ChatColor.BLUE + " - " + ChatColor.GOLD + reason + "\n ";
		}
		else{
			issueMessage = ChatColor.BLUE + "Issue " + issueID + ": " + ChatColor.GOLD + reason + ChatColor.BLUE + "\n     " + ChatColor.DARK_GRAY + "Player: " + ChatColor.GRAY + player + ChatColor.DARK_GRAY + " - Status: " + ChatColor.GRAY + status + "\n ";
		}
		return issueMessage;		
	}
	
	public String shortenIssue(String issueReason){
		if(issueReason.length() < 45){
			return issueReason;
		}
		else {
			String shortReason = issueReason.substring(0,45) + "...";
			return shortReason;
		}
	}
} //Ends IssueManager

