package com.mythicacraft.IssueTrackerExecutors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class IssueManager {
	
	SQLExecutors SQLExec = new SQLExecutors();
	
	public Issue getIssue(int issue_ID){
		Issue oldIssue = new Issue(issue_ID);
		return oldIssue;
	}

	public Issue createIssue(CommandSender sender, String reason) {
		Issue newIssue = new Issue(sender, reason);
		return newIssue;
	}
		
	public Issue[] getOpenIssues(){
		ArrayList<Issue> issueArray = new ArrayList<Issue>();
		String sqlQuery = "IS NOT NULL"; //Sets the query parameters for all open/reviewed issues without player restriction
		try {
			ResultSet sqlResult = SQLExec.statusQuery(sqlQuery);
			while(sqlResult.next()){
				Issue issue = new Issue(sqlResult.getString("player"), sqlResult.getInt("status"), sqlResult.getInt("issue_id"), sqlResult.getString("reason"));
				issueArray.add(issue); //Adds issue_ID into ArrayList
			}
			SQLExec.dbClose(); //Closes database
		} catch (SQLException e) {e.printStackTrace();}
		Issue [] openIssues = new Issue[issueArray.size()];
		for(int i = 0; i < issueArray.size(); i++) {
		     openIssues[i] = issueArray.get(i);
		}
		return openIssues;
	}
	
	public Issue[] getOpenIssues(String playerName){
		ArrayList<Issue> issueArray = new ArrayList<Issue>();
		String sqlQuery = "= '" + playerName + "'"; //Sets the query parameters for all open/reviewed issues for specified player
		try {
			ResultSet sqlResult = SQLExec.statusQuery(sqlQuery);
			while(sqlResult.next()){
				Issue issue = new Issue(sqlResult.getString("player"), sqlResult.getInt("status"), sqlResult.getInt("issue_id"), sqlResult.getString("reason"));
				issueArray.add(issue); //Adds issue_ID into ArrayList
			}
			SQLExec.dbClose(); //Closes database
		} catch (SQLException e) {e.printStackTrace();}
		Issue [] openIssues = new Issue[issueArray.size()];
		for(int i = 0; i < issueArray.size(); i++) {
		     openIssues[i] = issueArray.get(i);
		}
		return openIssues;
	}
	
	public Issue[] getClosedIssues(){
		ArrayList<Issue> issueArray = new ArrayList<Issue>();
		String sqlQuery = "IS NOT NULL"; //Sets the query parameters for all open/reviewed issues without player restriction
		try {
			ResultSet sqlResult = SQLExec.closeQuery(sqlQuery);
			while(sqlResult.next()){
				Issue issue = new Issue(sqlResult.getString("player"), sqlResult.getInt("status"), sqlResult.getInt("issue_id"), sqlResult.getString("reason"));
				issueArray.add(issue); //Adds issue_ID into ArrayList
			}
			SQLExec.dbClose(); //Closes database
		} catch (SQLException e) {e.printStackTrace();}
		Issue [] closedIssues = new Issue[issueArray.size()];
		for(int i = 0; i < issueArray.size(); i++) {
		     closedIssues[i] = issueArray.get(i);
		}
		return closedIssues;
	}
	
	public Issue[] getClosedIssues(String playerName){
		ArrayList<Issue> issueArray = new ArrayList<Issue>();
		String sqlQuery = "= '" + playerName + "'"; //Sets the query parameters for all open/reviewed issues for specified player
		try {
			ResultSet sqlResult = SQLExec.closeQuery(sqlQuery);
			while(sqlResult.next()){
				Issue issue = new Issue(sqlResult.getString("player"), sqlResult.getInt("status"), sqlResult.getInt("issue_id"), sqlResult.getString("reason"));
				issueArray.add(issue); //Adds issue_ID into ArrayList
			}
			SQLExec.dbClose(); //Closes database
		} catch (SQLException e) {e.printStackTrace();}
		Issue [] closedIssues = new Issue[issueArray.size()];
		for(int i = 0; i < issueArray.size(); i++) {
		     closedIssues[i] = issueArray.get(i);
		}
		return closedIssues;
	}
	
	public String convertIssueToMessage(Issue issue, String auth) {
		String issueMessage, reason = shortenIssue(issue.getReason()), player = issue.getPlayer(), status = issue.getStatusStr(), issueID = Integer.toString(issue.getIssueID());
		if(auth == "player"){
			issueMessage = ChatColor.BLUE + "Issue ID: " + ChatColor.GOLD + issueID + ChatColor.BLUE + " - Status: " + ChatColor.GOLD + status + ChatColor.BLUE + " - " + ChatColor.GOLD + reason + "\n ";
		}
		else{
			issueMessage = ChatColor.BLUE + "Issue " + issueID + ": " + ChatColor.GOLD + reason + ChatColor.BLUE + "\n     " + ChatColor.DARK_GRAY + "Player: " + ChatColor.GRAY + player + ChatColor.DARK_GRAY + " - Status: " + ChatColor.GRAY + status + "\n ";
		}
		return issueMessage;
	}
	
	public String issuesToMessage (Issue[] issue, String auth){
		StringBuilder build = new StringBuilder();
		for(int i = 0; i < issue.length; i++){
			build.append(convertIssueToMessage(issue[i], auth));
		}
		return build.toString();
	}
	
	public String shortenIssue(String issueReason){
		if(issueReason.length() < 45){
			return issueReason;
		}
		else {
			String shortReason = issueReason.substring(0,42) + "...";
			return shortReason;
		}
	}
} //Ends IssueManager

