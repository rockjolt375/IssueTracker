package com.mythicacraft.IssueTrackerListener;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mythicacraft.IssueTrackerExecutors.SQLExecutors;


public class IssueTrackerListener implements Listener {
	
	SQLExecutors SQLExec = new SQLExecutors();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		
		//Check if joining player is a mod
		if(player.hasPermission("issuetracker.admin")) {
			ResultSet sqlResult;
			//Calls the query to check if issues are unanswered
			try {
				sqlResult =SQLExec.statusQuery("IS NOT NULL");
				if(!sqlResult.next()){
					SQLExec.dbClose();
				}
				else{
					player.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.AQUA + "There are issues that need resolved! Type '/issue status' to view them.");
					SQLExec.dbClose();
				}
			} catch (SQLException e) {e.printStackTrace();}
		}
	}
}
