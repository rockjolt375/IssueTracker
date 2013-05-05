package com.mythicacraft.IssueTrackerExecutors;

import java.util.ArrayList;

public class IssueManager {

	public Issue createIssue(String playerName, String reason) {
		Issue newIssue = new Issue(playerName, reason);
		return newIssue;
	}
	
	public Issue[] getPlayerIssues(String playerName, String auth){
		ArrayList<Integer> IDs = new ArrayList<Integer>();
		//insert sql methods to pull player issues and get IDs for open/reviewed issues
		//using for loop to add the issues to the ID array list
		//using: IDs.add(integer);
		
		
		Issue[] openIssues = new Issue[IDs.size()];
		for(int i = 0; i < IDs.size(); i++) {
			openIssues[i] = new Issue(IDs.get(i));			
		}
		return openIssues;
	}
	
	public Issue[] getAllIssues(String auth){
		ArrayList<Integer> IDs = new ArrayList<Integer>();
		//insert sql methods to pull issues and get IDs for all open issues
		//using for loop to add the issueIDs to the ID array list
		//using: IDs.add(integer);
		Issue[] allIssues = new Issue[IDs.size()];
		for(int i = 0; i < IDs.size(); i++) {
			allIssues[i] = new Issue(IDs.get(i));			
		}
		return allIssues;
	}
	
	public Issue setIssueStatus(int issueID, String status){
		Issue setIssue = new Issue(issueID, Issue.getStatusID(status));
		return setIssue;
	}
	
	
}
