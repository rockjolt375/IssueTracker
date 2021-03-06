package com.mythicacraft.IssueTracker.Utilities;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;

public class PaginateIssue {

	private String paginateString;
	private int totalPages;

	public PaginateIssue() {
	}

	public PaginateIssue(String paginateString) {
		this.paginateString = paginateString;
	}

	public void sendPage(String userPage, CommandSender sender, String header) {
		if(!letterCheck(userPage)){
			sender.sendMessage(ChatColor.RED + "You have entered an invalid page number!");
			return;
		}		
		int pageNumber = Integer.parseInt(userPage);
			
		if(pageNumber > pageTotal()){
			sender.sendMessage(ChatColor.GOLD + "[IssueTracker] " + ChatColor.RED + "That page number doesn't exist!");
			return;
		}
		ChatPage message = ChatPaginator.paginate(paginateString, pageNumber, 53, 8); //creates lines 53 characters long and 8 lines per page
		String[] pages = message.getLines(); //puts the lines from the page into a string array
		
		sender.sendMessage(ChatColor.BLUE + "*******" + ChatColor.GREEN + header + ChatColor.GOLD + " Page " + pageNumber + "/" + pageTotal() + ChatColor.BLUE + "*******" ); //header of page with current and total pages
		sender.sendMessage(pages); //send page string array
	
		if(pageNumber < pageTotal()) { //if page number is less than total, include this footer
			int nextPage = pageNumber + 1;
			sender.sendMessage(ChatColor.GOLD + "Type \"/{previous command} " + nextPage + "\" for next page.");
		}
	}

	public void setPaginateString(String paginateString) {
		this.paginateString = paginateString;
	}

	public int pageTotal() { //returns an Int of total pages
		this.totalPages = ChatPaginator.paginate(paginateString, 1, 53, 8).getTotalPages();
		return totalPages;
	}
	
	public boolean letterCheck(String args) {
       try{
    	   @SuppressWarnings("unused")
    	   int testInt = Integer.parseInt(args);
    	   return true;
        } catch(Exception e){return false;}
	}	
}