package com.mythicacraft.IssueTracker.Utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.ChatColor;

import com.mythicacraft.IssueTracker.Utilities.Issue;

public class IssueManager {
	
	DatabaseHandler dbHand = new DatabaseHandler();

	public Issue getIssue(int issueID){
		return new Issue(issueID);
	}
	
	public void createIssue(String player, String reason){
		new Issue(player, reason);
	}
	
	public ArrayList<Issue> getOpenIssues() throws SQLException{
		return makeIssues(dbHand.getOpenIssues("IS NOT NULL"));
	}
	
	public ArrayList<Issue> getOpenIssues(String player) throws SQLException{
		return makeIssues(dbHand.getOpenIssues("= '" + player + "'"));
	}
	
	public ArrayList<Issue> getClosedIssues() throws SQLException{
		return makeIssues(dbHand.getClosedIssues("IS NOT NULL"));	
	}
	
	public ArrayList<Issue> getClosedIssues(String player) throws SQLException{
		return makeIssues(dbHand.getClosedIssues("= '" + player + "'"));	
	}
	
	public ArrayList<Issue> makeIssues(ResultSet result) throws SQLException{
		ArrayList<Issue> issues = new ArrayList<Issue>();
		while(result.next()){
			issues.add(new Issue(result.getString("player"), result.getInt("status"),
					result.getInt("issue_id"), result.getString("reason")));
		}
		dbHand.close();
		return issues;
	}
	
	//If there are still NULL or weird breaks - check here first
	public String convertIssueToMessage(Issue issue, String auth) {
		String issueMessage, reason = shortenIssue(issue.getReason(), auth), player = issue.getPlayer(),
				status = issue.getStatusString(), issueID = Integer.toString(issue.getIssueID());
		if(auth == "player"){
			issueMessage = ChatColor.BLUE + "Issue ID: " + ChatColor.GOLD + issueID + ChatColor.BLUE +
					" - Status: " + ChatColor.GOLD + status + ChatColor.BLUE + " - " + ChatColor.GOLD + reason + "\n ";
		}
		else{
			issueMessage = ChatColor.BLUE + "Issue " + issueID + ": " + ChatColor.GOLD + reason + ChatColor.BLUE + "\n     "
					+ ChatColor.DARK_GRAY + "Player: " + ChatColor.GRAY + player + ChatColor.DARK_GRAY + " - Status: " + ChatColor.GRAY + status + "\n ";
		}
		return issueMessage;
	}
	
	public String issuesToMessage (ArrayList<Issue> issue, String auth){
		StringBuilder build = new StringBuilder();
		for(int i = 0; i < issue.size(); i++){
			build.append(convertIssueToMessage(issue.get(i), auth));
		}
		System.out.println(build.toString());
		return build.toString();
	}
	
	public String shortenIssue(String issueReason, String auth){
		int length = (auth.equals("player")) ? 23 : 45;
		if(issueReason.length() < length){
			return issueReason;
		}
		else {
			String shortReason = issueReason.substring(0,length - 3) + "...";
			return shortReason;
		}
	}
}
