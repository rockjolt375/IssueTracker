package com.mythicacraft.IssueTrackerExecutors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
				Issue[] openIssues;
				if (args.length == 1){
					//If-Else triggers getter method for correct permission level
					if(!sender.hasPermission("issuetracker.admin")){
						openIssues = IM.getOpenIssues(senderName);
					}
					else{
						openIssues = IM.getOpenIssues();
					}
					if(!openIssues[0].exists()){
						sender.sendMessage(ChatColor.GOLD + "No issues exist for " + closePlayer);
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.BLUE+ "No issues exist for " + closePlayer);
						return true;
					}
					String pageMessage = IM.issueToMessage(openIssues, sender, auth);
					PaginateIssue pageIssue = new PaginateIssue(pageMessage);
					
					pageIssue.sendPage("1", sender, "Viewing all Open/Reviewed Issues");
				}
				else if(args.length == 2){
					//If-Else triggers getter method for correct permission level
					if(!sender.hasPermission("issuetracker.admin")){
						openIssues = IM.getOpenIssues(senderName);
					}
					else{
					openIssues = IM.getOpenIssues();
					}
					if(!openIssues[0].exists()){
						sender.sendMessage(ChatColor.GOLD + "No issues exist!");
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.BLUE + "No issues exist!");
						return true;
					}
					String pageMessage = IM.issueToMessage(openIssues, sender, auth);
					PaginateIssue pageIssue = new PaginateIssue(pageMessage);
					
					pageIssue.sendPage(args[1], sender, "Viewing All Open/Reviewed Issues");
					}
				else{ //Triggers if issuetracker permissions are not set
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "You do not have permissions to use this command!");
					}
				}
			else if(args[0].equalsIgnoreCase("set")){
				if(!sender.hasPermission("issuetracker.admin")){
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "You do not have permissions for this command!");
				}
				else if(sender.hasPermission("issuetracker.admin")){
				//if '/issue status # <close/closed/reviewed>' is typed
					int issueID;
					if(args[2].equalsIgnoreCase("close") || args[2].equalsIgnoreCase("closed") || args[2].equalsIgnoreCase("reviewed")){
						try{
							issueID = Integer.parseInt(args[1]);
						} catch (Exception e){ sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "That is not a valid issue ID!"); return true;}
						Issue setIssue = IM.getIssue(issueID);
						setIssue.setStatus(issueID, setIssue.switchStatus(args[2]));
						playerNotification(setIssue.getPlayer());
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.GREEN + "The issue's status has been updated!");
						}
					else {
						//if the format was wrong
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "Please enter an appropriate issue status. (Close or Reviewed)");
						}
				}
			} //Close if status # close/reviewed is typed
			//Triggers when /issue create args[x] is typed
			else if(args[0].equalsIgnoreCase("create")){
				 if(args.length >= 2){
					String issueReason = "";
					for(int i = 1; i < args.length; i++){
						issueReason += " " + args[i];
					}
					issueReason = issueReason.substring(1);
					
					IM.createIssue(sender, issueReason);
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.GREEN + "Your issue has successfully been submitted. A moderator will review it as soon as possible. You may type '/issue status' to view the status of your issues.");

					for(Player mod: plugin.getServer().getOnlinePlayers()) {    
		                if(mod.hasPermission("issuetracker.admin")) {      
		                 mod.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.GREEN + "A player submitted an issue. Type '/issue status' to view it.");
		                	}
		            	}	
				 	}
				 else {
					 sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "Please type '/issue create <message>' to submit an issue.");
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
					catch(Exception e){ sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "Please enter a valid issue ID! Type '/issue status' to view your issue"); return true;}
					Issue closeIssue = IM.getIssue(issueID);
					
					if(!player.equalsIgnoreCase(closeIssue.getPlayer())){
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "You can't close another player's issue!");
						return true;
					}
					closeIssue.setStatus(issueID, 3);
					sender.sendMessage(ChatColor.GREEN + "You have successfully closed an issue!");
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.GREEN + "You have successfully closed an issue!");
				}
				else {
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "Please type '/issue close <issue_ID>' to close an issue.");
				}
			}
			//When a player types '/issue view closed' do...
			else if(args[0].equalsIgnoreCase("view")){
				if(args.length == 1){
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "Please type '/issue view <ID>' or '/issue view closed'");
					return true;
				}
				//Player trigger conditions
				if(!sender.hasPermission("issuetracker.admin")){
					if(args.length == 2 && (args[1].equalsIgnoreCase("close") || args[1].equalsIgnoreCase("closed"))){
						Issue[] closedIssues = IM.getClosedIssues(sender.getName());
						if(!closedIssues[0].exists()){
							sender.sendMessage(ChatColor.GOLD + "No issues exist for " + closePlayer);
							sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.BLUE + "No issues exist for " + closePlayer);
							return true;
						}
						String pageMessage = IM.issueToMessage(closedIssues, sender, auth);
						PaginateIssue pageIssue = new PaginateIssue(pageMessage);
						
						pageIssue.sendPage("1", sender, "Viewing All Closed Issues");
						return true;
					}
					sender.sendMessage(ChatColor.RED + "Please type '/issue view closed' to view your closed issues");
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "Please type '/issue view closed' to view your closed issues");
				}
				if(args.length == 2){
					int issueID;
					try{
						issueID = Integer.parseInt(args[1]); 
					} catch (Exception e){sender.sendMessage("Please enter a valid ID number!"); return true;}
					Issue viewIssue = IM.getIssue(issueID);
					if(!sender.hasPermission("issuetracker.admin") && !viewIssue.getPlayer().equalsIgnoreCase(sender.getName())){
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.RED + "You may not view an issue you do not own!");
						return true;
					}
					sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + "Displaying Issue " + issueID + " for " + viewIssue.getPlayer() + ChatColor.BLUE + "*******" );
					sender.sendMessage(ChatColor.BLUE + "Status: " + ChatColor.GRAY + viewIssue.getStatusStr() + ChatColor.BLUE + " Reason: " + ChatColor.GOLD + viewIssue.getReason()); 
					return true;
				}	
				else if(args.length == 3 && (args[1].equalsIgnoreCase("close") || args[1].equalsIgnoreCase("closed"))){
					String pageNumber;
					if(!sender.hasPermission("issuetracker.admin")){
						closePlayer = sender.getName();
						pageNumber = args[2];
					}
					else{
						closePlayer = args[2];
						pageNumber = "1";
					}
					Issue[] closedIssues = IM.getClosedIssues(closePlayer);
					if(!closedIssues[0].exists()){
						sender.sendMessage(ChatColor.GOLD + "No issues exist for " + closePlayer);
						return true;
					}
					String pageMessage = IM.issueToMessage(closedIssues, sender, auth);
					PaginateIssue pageIssue = new PaginateIssue(pageMessage);
					
					pageIssue.sendPage(pageNumber, sender, "Viewing All Closed Issues");
					
					return true;
				}	
				else if(sender.hasPermission("issuetracker.admin") && args.length == 4 && (args[1].equalsIgnoreCase("close") || args[1].equalsIgnoreCase("closed"))){
					Issue[] closedIssues = IM.getClosedIssues(args[2]);
					if(!closedIssues[0].exists()){
						sender.sendMessage(ChatColor.GOLD + "No issues exist for " + closePlayer);
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.BLUE + "No issues exist for " + closePlayer);
						return true;
					}
					String pageMessage = IM.issueToMessage(closedIssues, sender, auth);
					PaginateIssue pageIssue = new PaginateIssue(pageMessage);
					
					pageIssue.sendPage(args[3], sender, "Viewing All Closed Issues");
					return true;
				}	
			} //End /issue view
			//If none of the triggers are hit - tell them how to view correct syntax
			else {
				sender.sendMessage(ChatColor.GOLD + "[IssueTracker]" + ChatColor.BLUE + "Please type /issue for help.");
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

} //End class
