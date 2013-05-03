package com.mythicacraft.IssueTrackerExecutors;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;

import com.mythicacraft.IssueTracker.cIssueTracker;

public class Utility {
	
	SQLExecutors SQLExec = new SQLExecutors();
	cIssueTracker tracker = new cIssueTracker();
	String status;
	String pageString = "";
	
//Constructor Method
	public Utility() {
	}
	
//Issue Create method
	public void createIssue(String playerName, String reason){
		
		Player sender = tracker.getServer().getPlayer(playerName);
		
		try {
			SQLExec.createQuery(playerName, reason); //Insert new issue to database
			sender.sendMessage(ChatColor.GREEN + "Your issue has been submitted. A mod will review it shortly.");
		} catch (SQLException e) {
			sender.sendMessage(ChatColor.RED + "Something went wrong with the database! Contact a mod for help."); //Detects failure with DB connection
		}
	}

//Issue Status method
	public boolean statusIssue(CommandSender sender, String pageNumber, String auth){
		String reason = "";
		String sql;
		//Sets parameters for SQL query
		if(auth == "player"){
			sql = "= '" + sender + "'";
		}
		else {
			sql = "IS NOT NULL";
		}
		//Runs SQL
		try {
			SQLExec.statusQuery(sender, sql);	//Pulls statuses for sender
			if(!SQLExecutors.selectSQL.next()){ //Checks if there are rows to display
				sender.sendMessage(ChatColor.GREEN + "You have no issues to display!");
			}
			else {
				SQLExecutors.selectSQL.beforeFirst();
				while (SQLExecutors.selectSQL.next()) {	//Cycles through all statuses and displays
					try{
						reason = shortenIssue(SQLExecutors.selectSQL.getString("reason"));
					} catch (SQLException e) {
						e.printStackTrace();
					}
					//Sets status
					int tempstatus = SQLExecutors.selectSQL.getInt("status");
					if (tempstatus == 2){
						status = "Reviewed";
					}
					else {
						status = "Open";
					}
					//Chooses content to display according to permissions
					if (auth == "player"){
					pageString = pageString + ChatColor.BLUE + "Issue ID: " + ChatColor.GOLD + SQLExecutors.selectSQL.getString("issue_id") + ChatColor.BLUE + " - Status: " + ChatColor.GOLD + status + ChatColor.BLUE + " - " + ChatColor.GOLD + reason + "\n ";
					}
					else {
					pageString = pageString + ChatColor.BLUE + "Issue ID: " + SQLExecutors.selectSQL.getString("issue_ID") + ": " + ChatColor.GOLD + reason + ChatColor.BLUE + "\n     " + ChatColor.DARK_GRAY + "Player: " + ChatColor.GRAY + SQLExecutors.selectSQL.getString("player") + ChatColor.DARK_GRAY + " - Status: " + ChatColor.GRAY + status + "\n ";
					}
				}
				//Close database connection
				SQLExecutors.dbClose();
			}
		}
		catch (SQLException e) {
			sender.sendMessage(ChatColor.RED + "Something went wrong with the database! Contact a mod for help."); //Detects failure with DB connection
		}
		if(letterCheck(pageNumber) == true) { //use a method that checks argument for invalid characters (anything but numbers)
			sender.sendMessage(ChatColor.RED + "That is not a valid page number!");
			return true;
		}

		int userPage = Integer.parseInt(pageNumber); //now that we know the first arg is a number, make it into an Int

		if(userPage <= pageTotal()) { //as long as the given user page number is less than or equal to the total pages paginated from first string.
			pageSenderOpen(sender, userPage); //use method to send pages
			return true;
		}
		if(userPage > pageTotal()) { // if given user page number is more than the total page number.
			sender.sendMessage(ChatColor.RED + "That is not a valid page number!");
			return true;
		}
		return false;	
		}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		public void pageSenderOpen(CommandSender sender, int pageNumber) { //method to send pages, accepts the sender object and the user given page number
	        ChatPage message = ChatPaginator.paginate(pageString, pageNumber, 53, 8); //paginate string, pulling the page number the player provided. It creates the page with the lines 53 characters long and 8 lines per page
		    String[] pages = message.getLines(); //puts the lines from the page into a string array
		    sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + "All Open/Reviewed Statuses " + ChatColor.GOLD + "Page " + pageNumber + "/" + pageTotal() + ChatColor.BLUE + "*******" ); //header of page with current and total pages
		    sender.sendMessage(pages); //send page string array
		    if(pageNumber < pageTotal()) { //if page number is less than total, include this footer
			    int nextPage = pageNumber + 1;
			    sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + "Type \"/issue status " + nextPage + "\" for next page." + ChatColor.BLUE + "*******");
		    }
		    pageString = "";
		}
		public void pageSenderAdminClosed(CommandSender sender, int pageNumber) { //method to send pages, accepts the sender object and the user given page number
	        ChatPage message = ChatPaginator.paginate(pageString, pageNumber, 53, 8); //paginate string, pulling the page number the player provided. It creates the page with the lines 53 characters long and 8 lines per page
		    String[] pages = message.getLines(); //puts the lines from the page into a string array
		    sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + "All Closed Issues For: " + closePlayer + ChatColor.GOLD + " Page " + pageNumber + "/" + pageTotal() + ChatColor.BLUE + "*******" ); //header of page with current and total pages
		    sender.sendMessage(pages); //send page string array
		    if(pageNumber < pageTotal()) { //if page number is less than total, include this footer
			    int nextPage = pageNumber + 1;
			    sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + "Type \"/issue view closed <player> " + nextPage + ChatColor.BLUE + "*******");
		    }
		    pageString = "";
		}
		public void pageSenderClosed(CommandSender sender, int pageNumber) { //method to send pages, accepts the sender object and the user given page number
	        ChatPage message = ChatPaginator.paginate(pageString, pageNumber, 53, 8); //paginate string, pulling the page number the player provided. It creates the page with the lines 53 characters long and 8 lines per page
		    String[] pages = message.getLines(); //puts the lines from the page into a string array
		    sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + "Your Closed Issues" + ChatColor.GOLD + " Page " + pageNumber + "/" + pageTotal() + ChatColor.BLUE + "*******" ); //header of page with current and total pages
		    sender.sendMessage(pages); //send page string array
		    if(pageNumber < pageTotal()) { //if page number is less than total, include this footer
			    int nextPage = pageNumber + 1;
			    sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + "Type \"/issue view closed " + nextPage + "\" for next page." + ChatColor.BLUE + "*******");
		    }
		    pageString = "";
		}
		public int pageTotal() { //returns an Int of total pages
		    ChatPage message = ChatPaginator.paginate(pageString, 1, 53, 8);
		    int totalPages = message.getTotalPages();
		    return totalPages;
		}
		public boolean letterCheck(String args) { //uses a regex to check for anything that ISN'T a number
	        Pattern checkRegex = Pattern.compile("[\\D]");
	        Matcher regexMatcher = checkRegex.matcher(args);
	        if(regexMatcher.find()) {
	    	return true;
	        }
	        return false;
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
		

	
	
	
	
} //End class