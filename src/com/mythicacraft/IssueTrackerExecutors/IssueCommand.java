package com.mythicacraft.IssueTrackerExecutors;


import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;


import com.mythicacraft.IssueTracker.cIssueTracker;

public class IssueCommand implements CommandExecutor{

	private cIssueTracker plugin;
	public static String closeIssueID;
	public static String setStatus;
	public static String closePlayer;
	public String pageString = "";
	public String notifyPlayer;
	public String reason;
	public String auth;
	
	IssueManager IM = new IssueManager();
	SQLExecutors sqlExec = new SQLExecutors();
	public IssueCommand(cIssueTracker plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		String senderName = sender.getName();
		String status = "Open";
		String reason = "";

		if(!sender.hasPermission("issuetracker.admin")){
			auth = "player";
		}
		else{
			auth = "admin";
		}
		
		if(sender.hasPermission("issuetracker.issue")){ //Initial permission check
		
		//Triggered when /issue [arg] is typed in any form
		if(commandLabel.equalsIgnoreCase("issue")){
			//When a player types /issue, /issue ?, /issue help
			if(args.length == 0 || ((args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) && args.length < 2)){
				sender.sendMessage(ChatColor.GREEN + "-----" + ChatColor.GOLD + "IssueTracker Help" + ChatColor.GREEN + "-----");
				sender.sendMessage(ChatColor.YELLOW + "/issue create {Description}" + ChatColor.BLUE + "\n    Creates an issue");
				sender.sendMessage(ChatColor.YELLOW + "/issue status" + ChatColor.BLUE + "\n    Displays issue status");
				sender.sendMessage(ChatColor.YELLOW + "/issue close <issueID>" + ChatColor.BLUE + "\n    Removes or closes an issue");
				sender.sendMessage(ChatColor.YELLOW + "/issue view closed" + ChatColor.BLUE + "\n    View your closed issues");
				if(sender.hasPermission("issuetracker.admin")){
					sender.sendMessage(ChatColor.GREEN + "-----" + ChatColor.GOLD + "Admin Commands" + ChatColor.GREEN + "-----");
					sender.sendMessage(ChatColor.YELLOW + "/issue set <issue_ID> <close/reviewed>" + ChatColor.BLUE + "\n    Sets status of an issue to close or reviewed");
					sender.sendMessage(ChatColor.YELLOW + "/issue status" + ChatColor.BLUE + "\n    Shows all open or reviewed issues");
					sender.sendMessage(ChatColor.YELLOW + "/issue view closed <player>" + ChatColor.BLUE + "\n    Shows all closed issues submitted by <player>");
				}
			}
			//When a player types '/issue status' do...
			else if(args[0].equalsIgnoreCase("status")){
				if (args.length == 1){
					//If only /issue status is typed treat as 1 page
					if(args.length == 1){
						Issue[] openIssues = IM.getOpenIssues(senderName);
						for(int i = 0; i < openIssues.length; i++){
							pageString = pageString + IM.convertIssueToMessage(openIssues[i], auth);
						}
						pageSenderOpen(sender, 1);
					}
					//If /issue status # is typed
					if(args.length == 2){
						Issue[] openIssues = IM.getOpenIssues(senderName);
						for(int i = 0; i < openIssues.length; i++){
							pageString = pageString + IM.convertIssueToMessage(openIssues[i], auth);
						}
						paginationCheck(sender, args[1]);

					}
				else{
					//If player does not have permission issuetracker.admin
					sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command!");
					}
				}
			}
			else if(args[0].equalsIgnoreCase("set")){
				if(!sender.hasPermission("issuetracker.admin")){
					sender.sendMessage(ChatColor.RED + "You do not have permissions for this command!");
				}
				else if(sender.hasPermission("issuetracker.admin")){
				//if '/issue status # <close/closed/reviewed>' is typed
					int issueID;
					if(args[2].equalsIgnoreCase("close") || args[2].equalsIgnoreCase("closed") || args[2].equalsIgnoreCase("reviewed")){
						try{
							issueID = Integer.parseInt(args[1]);
						} catch (Exception e){ sender.sendMessage(ChatColor.RED + "That is not a valid issue ID!"); return true;}
						Issue setIssue = IM.getIssue(issueID);
						setIssue.setStatus(issueID, setIssue.switchStatus(args[2]));
						}
					else {
						//if the format was wrong
						sender.sendMessage(ChatColor.RED + "Please enter an appropriate issue status. (Close or Reviewed");
						}
				}
			} //Close if status # close/reviewed is typed
			//Triggers when /issue create args[x] is typed
			else if(args[0].equalsIgnoreCase("create")){
				 if(args.length >= 2){
					String issueReason;
					for(int i = 1; i < args.length; i++){
						issueReason += " " + args[i];
					}
					issueReason = issueReason.substring(1);
					
					IM.createIssue(sender, issueReason);
					sender.sendMessage(ChatColor.GREEN + "Your issue has successfully been submitted. A moderator will review it as soon as possible. You may type '/issue status' to view the status of your issues.");

					for(Player mod: plugin.getServer().getOnlinePlayers()) {    
		                if(mod.hasPermission("issuetracker.admin")) {      
		                 mod.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.GREEN + "A player submitted an issue. Type '/issue status' to view it.");
		                	}
		            	}	
				 	}
				 else {
					 sender.sendMessage(ChatColor.RED + "Please type '/issue create <message>' to submit an issue.");
				 }
				}
			//Triggers when /issue close is typed
			else if(args[0].equalsIgnoreCase("close")){
				if(args.length == 2){
					String player = sender.getName();
					int issueID;
					try{
						issueID = Integer.parseInt(args[1]);
					}
					catch(Exception e){ sender.sendMessage(ChatColor.RED + "Please enter a valid issue ID! Type '/issue status' to view your issue"); return true;}
					Issue closeIssue = IM.getIssue(issueID);
					
					if(!player.equalsIgnoreCase(closeIssue.getPlayer())){
						sender.sendMessage(ChatColor.RED + "You can't close another player's issue!");
						return true;
					}
					closeIssue.setStatus(issueID, 3);
					sender.sendMessage(ChatColor.GREEN + "You have successfully closed your issue!");
				}
				else {
					sender.sendMessage(ChatColor.RED + "Please type '/issue close <issue_ID>' to close an issue.");
				}
			}
			//When a player types '/issue view closed' do...
			else if(args[0].equalsIgnoreCase("view")){
				//If user types /issue view closed only - treat as page 1
				if(args.length == 2 && !sender.hasPermission("issuetracker.admin")){
					if(args[1].equalsIgnoreCase("closed") || args[1].equalsIgnoreCase("close")){
						try {				
							//Calling the SELECT query for status
							sqlExec.viewCloseQuery();
							//Pulls each row of the database. Displays each row
							while (SQLExecutors.selectSQL.next()) {
								try{
									reason = shortenIssue(SQLExecutors.selectSQL.getString("reason"));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								pageString = pageString + ChatColor.BLUE + "Issue ID: " + ChatColor.GOLD + SQLExecutors.selectSQL.getString("issue_id") + ChatColor.BLUE + " - " + ChatColor.GOLD + reason + "\n";
								}
							//Close database connection
								SQLExecutors.dbClose();
								pageSenderClosed(sender, 1);//use method to send pages
							} 	
						catch (SQLException e) {
							e.printStackTrace();
							}
						}
					else {
						issueID = args[1];
						try{
							@SuppressWarnings("unused")
							int temp = Integer.parseInt(issueID);
							
							sqlExec.issueQuery();
							if(!SQLExecutors.selectSQL.next()){
								sender.sendMessage(ChatColor.RED + "No issues found. Did you type the issue ID correctly?");
							}
							else{
								SQLExecutors.selectSQL.beforeFirst();
								while (SQLExecutors.selectSQL.next()) {
									switch(SQLExecutors.selectSQL.getInt("status")){
									case 1:
										setStatus = "Open";
										break;
									case 2:
										setStatus = "Reviewed";
										break;
									case 3:
										setStatus = "Closed";
										break;
									}
									sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + "Displaying Issue #" + SQLExecutors.selectSQL.getString("issue_ID") + ChatColor.BLUE + "*******" ); //header of view issue
								    sender.sendMessage(ChatColor.BLUE + "Status: " + ChatColor.GOLD + setStatus + ChatColor.BLUE + "\nIssue: " + ChatColor.GOLD + SQLExecutors.selectSQL.getString("reason"));
								}
								SQLExecutors.dbClose();
							}
						}
						catch(Exception ex){
						sender.sendMessage(ChatColor.RED + "Please type '/issue view closed' or '/issue view <issue ID>' to view your closed issues");
						}
						}
				}
				else if(args.length == 3 && !sender.hasPermission("issuetracker.admin")){
					if(args[1].equalsIgnoreCase("closed") || args[1].equalsIgnoreCase("close")){
						try {				
							//Calling the SELECT query for status
							sqlExec.viewCloseQuery();
							//Pulls each row of the database. Displays each row
							while (SQLExecutors.selectSQL.next()) {
								try{
									reason = shortenIssue(SQLExecutors.selectSQL.getString("reason"));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								pageString = pageString + ChatColor.BLUE + "Issue ID: " + ChatColor.GOLD + SQLExecutors.selectSQL.getString("issue_id") + ChatColor.BLUE + " - " + ChatColor.GOLD + reason + "\n";
								}
							//Close database connection
								SQLExecutors.dbClose();
								if(letterCheck(args[2]) == true) { //use a method that checks argument for invalid characters (anything but numbers)
									sender.sendMessage(ChatColor.RED + "That is not a valid page number!");
									return true;
								}

								int userPage = Integer.parseInt(args[2]); //now that we know the first arg is a number, make it into an Int

								if(userPage <= pageTotal()) { //as long as the given user page number is less than or equal to the total pages paginated from first string.
									pageSenderClosed(sender, userPage); //use method to send pages
									return true;
								}
								if(userPage > pageTotal()) { // if given user page number is more than the total page number.
									sender.sendMessage(ChatColor.RED + "That is not a valid page number!");
									return true;
								}
								return false;	
								} 	
						catch (SQLException e) {
							e.printStackTrace();
							}
						}
					else {
						sender.sendMessage(ChatColor.RED + "Please type '/issue view closed' to view your closed issues");
					}
				}
				else if(sender.hasPermission("issuetracker.admin")){
					if(args.length == 2){
						issueID = args[1];
						try{
							@SuppressWarnings("unused")
							int temp = Integer.parseInt(issueID);
							
							sqlExec.adminIssueQuery();
							if(!SQLExecutors.selectSQL.next()){
								sender.sendMessage(ChatColor.RED + "No issues found. Did you type the issue ID correctly?");
							}
							else{
							SQLExecutors.selectSQL.beforeFirst();
							while (SQLExecutors.selectSQL.next()) {
								
									if(SQLExecutors.selectSQL.getInt("status") == 1){
										setStatus = "Open";
									}
									else if (SQLExecutors.selectSQL.getInt("status") == 2){
										setStatus = "Reviewed";
									}
									else if (SQLExecutors.selectSQL.getInt("status") == 3){
										setStatus = "Closed";
									}
									sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + "Displaying Issue #" + SQLExecutors.selectSQL.getString("issue_ID") + ChatColor.BLUE + "*******" ); //header of view issue
								    sender.sendMessage(ChatColor.BLUE + "Player: " + ChatColor.GOLD + SQLExecutors.selectSQL.getString("player") + ChatColor.BLUE + " - Status: " + ChatColor.GOLD + setStatus + ChatColor.BLUE + "\nIssue: " + ChatColor.GOLD + SQLExecutors.selectSQL.getString("reason"));
							}
							SQLExecutors.dbClose();
							}
						}
						catch(Exception ex){
						sender.sendMessage(ChatColor.RED + "Please type '/issue view closed' or '/issue view <issue ID>' to view your closed issues");
						}
				
					}
					else if(args.length == 3){
						if (args[1].equalsIgnoreCase("close") || args[1].equalsIgnoreCase("closed")){
							try {				
								closePlayer = args[2];
								//Calling the SELECT query for status
								sqlExec.adminCloseQuery();
								//Pulls each row of the database. Displays each row
								if(!SQLExecutors.selectSQL.next()){
									sender.sendMessage(ChatColor.RED + "No issues found. Did you type the player name correctly?");
								}
								else{
								SQLExecutors.selectSQL.beforeFirst();
								while (SQLExecutors.selectSQL.next()) {
									try{
										reason = shortenIssue(SQLExecutors.selectSQL.getString("reason"));
										} catch (SQLException e) {
											e.printStackTrace();
										}
									 pageString = pageString + ChatColor.BLUE + "> Issue #" + SQLExecutors.selectSQL.getString("issue_ID") + ": " + ChatColor.GOLD + reason + "\n";
								}
								SQLExecutors.dbClose();
								pageSenderAdminClosed(sender, 1);
								} 	
							}
							catch (SQLException e) {
								sender.sendMessage("No closed issues found for player " + closePlayer);
								}
						} //if args 1 = close/closed
						else {
							sender.sendMessage(ChatColor.RED + "Please enter a valid player ID. Type '/issue view closed <player>");
						}
					}
					else if(args.length == 4){
						if (args[1].equalsIgnoreCase("close") || args[1].equalsIgnoreCase("closed")){
							try {				
								closePlayer = args[2];
								//Calling the SELECT query for status
								sqlExec.adminCloseQuery();
								//Pulls each row of the database. Displays each row
								if(!SQLExecutors.selectSQL.next()){
									sender.sendMessage(ChatColor.RED + "No issues found. Did you type the player name correctly?");
								}
								else{
								SQLExecutors.selectSQL.beforeFirst();
								while (SQLExecutors.selectSQL.next()) {
									try{
										if(SQLExecutors.selectSQL.getString("reason").length() > 45){
											reason = SQLExecutors.selectSQL.getString("reason").substring(0,45) + "...";
										}
										else{
											reason = SQLExecutors.selectSQL.getString("reason");
										}
										}
									catch (SQLException e) {
											e.printStackTrace();
										}
									 pageString = pageString + ChatColor.BLUE + "> Issue #" + SQLExecutors.selectSQL.getString("issue_ID") + ": " + ChatColor.GOLD + reason + "\n";
									}
								SQLExecutors.dbClose();
								if(letterCheck(args[3]) == true) { //use a method that checks argument for invalid characters (anything but numbers)
									sender.sendMessage(ChatColor.RED + "That is not a valid page number!");
									return true;
								}
								int userPage = Integer.parseInt(args[3]); //now that we know the first arg is a number, make it into an Int
								if(userPage <= pageTotal()) { //as long as the given user page number is less than or equal to the total pages paginated from first string.
									pageSenderAdminClosed(sender, userPage); //use method to send pages
									return true;
								}
								if(userPage > pageTotal()) { // if given user page number is more than the total page number.
									sender.sendMessage(ChatColor.RED + "That is not a valid page number!");
									return true;
								}
								return false;
								} 	
							}
							catch (SQLException e) {
								sender.sendMessage("No closed issues found for player " + closePlayer);
								}
						} //if args 1 = close/closed
						else {
							sender.sendMessage(ChatColor.RED + "Please enter a valid issue ID. Type '/issue view closed <player>");
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You must enter a valid players name: /issue view closed <player>");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You do not have permissions to use this!");
				}
			}
			//If none of the triggers are hit - tell them how to view correct syntax
			else {
				sender.sendMessage("Please type /issue for help.");
				}
			}
		}
		return true;
		}
	public void playerNotification(String pName){
		try{
		Player player = plugin.getServer().getPlayer(pName);
		if(player != null)
		    player.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.GREEN + "Your issue has been udpated!");
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
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
	public void pageSenderClosed(CommandSender sender, int pageNumber) { //method to send pages, accepts the sender object and the user given page number
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
	
	public boolean paginationCheck(CommandSender sender, String testPage){

		if (letterCheck(testPage) == true){
			sender.sendMessage(ChatColor.RED + "That is not a valid page number!");
			return true;
		}
		int userPage = Integer.parseInt(testPage);

		if(userPage <= pageTotal()) {
			pageSenderOpen(sender, userPage);
			return true;
		}
		if(userPage > pageTotal()){
			sender.sendMessage(ChatColor.RED + "That is not a valid page number!");
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

}
