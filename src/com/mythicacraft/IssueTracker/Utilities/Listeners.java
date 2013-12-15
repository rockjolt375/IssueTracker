package com.mythicacraft.IssueTracker.Utilities;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Listeners implements Listener {
	
	DatabaseHandler dbHand = new DatabaseHandler();
	
	public Listeners(){
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		
		//Check if joining player is a mod
		if(player.hasPermission("issuetracker.admin")) {
			ResultSet sqlResult;
			//Calls the query to check if issues are unanswered
			try {
				sqlResult = dbHand.getOpenIssues("IS NOT NULL");
				if(sqlResult.next()){
					player.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.AQUA + "There are issues that need resolved! Type '/issue status' to view them.");
				}
			} catch (SQLException e) {e.printStackTrace();}
		}
	}
}
