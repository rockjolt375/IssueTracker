package com.mythicacraft.IssueTracker;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythicacraft.IssueTracker.Utilities.*;

public class IssueCommands implements CommandExecutor {

	IssueTracker plugin;
	
	public IssueCommands(IssueTracker plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		String senderName = sender.getName();
		IssueManager IM = new IssueManager();
		
		String auth = (sender.hasPermission("issuetracker.admin")) ? "admin" : "player";
		
		if(commandLabel.equalsIgnoreCase("issue")){
			//When a player types /issue, /issue ?, /issue help
			if(args.length == 0 || ((args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) && args.length < 2)){
				sender.sendMessage(ChatColor.GREEN + "-----" + ChatColor.GOLD + "IssueTracker Help" + ChatColor.GREEN + "-----");
				sender.sendMessage(ChatColor.YELLOW + "/issue create {Description}" + ChatColor.BLUE + "\n    Creates an issue");
				sender.sendMessage(ChatColor.YELLOW + "/issue status" + ChatColor.BLUE + "\n    Displays issue status");
				sender.sendMessage(ChatColor.YELLOW + "/issue close <issueID>" + ChatColor.BLUE + "\n    Removes or closes an issue");
				sender.sendMessage(ChatColor.YELLOW + "/issue view <issueID>" + ChatColor.BLUE + "\n    View specified issue");
				sender.sendMessage(ChatColor.YELLOW + "/issue view closed" + ChatColor.BLUE + "\n    View your closed issues");
				if(sender.hasPermission("issuetracker.admin")){
					sender.sendMessage(ChatColor.GREEN + "-----" + ChatColor.GOLD + "Admin Commands" + ChatColor.GREEN + "-----");
					sender.sendMessage(ChatColor.YELLOW + "/issue set <issue_ID> <close/reviewed>" + ChatColor.BLUE + "\n    Sets status of an issue to close or reviewed");
					sender.sendMessage(ChatColor.YELLOW + "/issue status" + ChatColor.BLUE + "\n    Shows all open or reviewed issues");
					sender.sendMessage(ChatColor.YELLOW + "/issue view closed <player>" + ChatColor.BLUE + "\n    Shows all closed issues submitted by <player>");
				}
				return true;
			}
			
			else if(args[0].equalsIgnoreCase("create")){
				if(args.length < 2){
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "Please enter the reason for creating an issue.");
					return true;
				}
				String reasonString = null;
				for(int i=1; i<args.length;i++){
					reasonString += " " + args[i];
				}
				reasonString = reasonString.substring(1);
				IM.createIssue(senderName, reasonString);
				sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.GREEN + "Your issue has been submitted. A mod will review it shortly.");
				return true;
			}
			
			else if(args[0].equalsIgnoreCase("status")) {
				if(args.length > 2){
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "For issue status, please type \"/issue status [page]\".");
					return true;
				}
				ArrayList<Issue> openIssues;
				try{
					if(!sender.hasPermission("issuetracker.admin")){
						openIssues = IM.getOpenIssues(senderName);
						auth = "player";
					}
					else{
						openIssues = IM.getOpenIssues();
					}
					if(openIssues.size() < 1){
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.BLUE+ "There are no unresolved issues!");
						return true;
					}
					PaginateIssue pageIssue = new PaginateIssue(IM.issuesToMessage(openIssues, auth));
					
					String page = (args.length > 1) ? args[1] : "1";
					
					pageIssue.sendPage(page, sender, "Viewing all Open/Reviewed Issues");
				} catch(SQLException sql){sql.printStackTrace();}
				return true;
			}
			
			else if(args[0].equalsIgnoreCase("close")){
				if(args.length != 2){
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "Please type '/issue close <issue_ID>' to close an issue.");
					return true;
				}
				String player = sender.getName();
				int issueID;
				try{
					issueID = Integer.parseInt(args[1]);
				}
				catch(Exception e){ sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "Please enter a valid issue ID! Type '/issue status' to view your issues."); return true;}
				
				Issue closeIssue = IM.getIssue(issueID);
				if(!sender.hasPermission("issuetracker.admin") && !player.equalsIgnoreCase(closeIssue.getPlayer())){
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "You can't close another player's issue!");
					return true;
				}
				closeIssue.setStatus(3);
				sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.GREEN + "You have successfully closed an issue!");
				return true;
			}
			
			else if(args[0].equalsIgnoreCase("view")){
				if(args.length < 2){
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "To view issues, please type \"/issue view closed\" or \"/issue view <issueID>\"");
					return true;
				}
				else if(isNumber(args[1])){
					Issue viewIssue = IM.getIssue(Integer.parseInt(args[1]));
					if(!viewIssue.exists()){
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "That issue does not exist!");
						return true;
					}
					else if(!sender.hasPermission("issuetracker.admin") && !viewIssue.getPlayer().equalsIgnoreCase(sender.getName())){
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "You may not view an issue you do not own!");
						return true;
					}
					sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + "Displaying Issue " + args[1] + " for " + viewIssue.getPlayer() + ChatColor.BLUE + "*******" );
					sender.sendMessage(ChatColor.BLUE + "Status: " + ChatColor.GRAY + viewIssue.getStatusString() + ChatColor.BLUE + " Reason: " + ChatColor.GOLD + viewIssue.getReason());
					return true;
				}
				else if(args.length > 1 && (args[1].equalsIgnoreCase("close") || args[1].equalsIgnoreCase("closed"))){
					String page = "1";
					if(args.length == 2){
						page = "1";
					}
					else if(args.length == 3 && isNumber(args[2])){
						page = args[2];
					}
					else if(auth.equals("admin")){
						senderName = args[2];
						page = (args.length > 3 && isNumber(args[3])) ? args[3] : "1";
					}
					try{
					ArrayList<Issue> closedIssues = IM.getClosedIssues(senderName);
					if(closedIssues.size() < 1){
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.BLUE + "No closed issues exist!");
						return true;
					}
					PaginateIssue pageIssue = new PaginateIssue(IM.issuesToMessage(closedIssues, auth));	
					pageIssue.sendPage(page, sender, "Viewing All Closed Issues");
					return true;
					}catch(Exception e){e.printStackTrace();}
				}
				else{
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "Type \"/issue view <issueID>\" or \"/issue view closed [page #]\"");
				}
				return true;
			}
			
			else if(args[0].equalsIgnoreCase("set")){
				if(!sender.hasPermission("issuetracker.admin")){
					sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "You do not have permissions for this command!");
					return true;
				}
				else{
					if(args[2].equalsIgnoreCase("close") || args[2].equalsIgnoreCase("closed") || args[2].equalsIgnoreCase("reviewed")){
						if(!isNumber(args[1])){
							sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "That is not a valid issue ID!");
							return true;
						}
						int issueID = Integer.parseInt(args[1]);
						Issue setIssue = IM.getIssue(issueID);
						if(!setIssue.exists()){
							sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "Issue #" + issueID + " does not exist!");
							return true;
						}
						setIssue.setStatus(setIssue.switchStatus(args[2]));
						playerNotification(setIssue.getPlayer());
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.GREEN + "The issue's status has been updated!");
						return true;
						}
					else {
						sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "Please enter an appropriate issue status. (Closed or Reviewed)");
						return true;
						}
				}
			}
			return true;
		} // End commandLabel 'issue'
		return false;
	}
	
	public void playerNotification(String pName){
		try{
		Player player = plugin.getServer().getPlayer(pName);
		if(player != null) player.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.GREEN + "Your issue has been udpated!");
		}catch(Exception ex){ex.printStackTrace();}
	}
	
	private boolean isNumber(String arg){
		try{
			@SuppressWarnings("unused")
			int i = Integer.parseInt(arg);
		} catch(Exception e){return false;}
		return true;
	}
} // End IssueCommand.class
