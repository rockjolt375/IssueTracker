package com.mythicacraft.IssueTracker;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.mythicacraft.IssueTracker.Utilities.DatabaseHandler;
import com.mythicacraft.IssueTracker.Utilities.Listeners;


public class IssueTracker extends JavaPlugin{
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public static String username;
	public static String password;
	public static String port;
	public static String host;
	public static String database;
	
//**********ENABLE/DISABLE METHODS**************
	public void onEnable() {
		
		//*******CREATES PLUGIN DIRECTORY AND TXT STORAGE FILE*********
		String pluginFolder = this.getDataFolder().getAbsolutePath();
		(new File(pluginFolder)).mkdirs();
		File file = new File(getDataFolder() + File.separator + "config.yml");
		
		//Creates default config and disables the plugin until valid data is input
		if (!file.exists()){
			this.getLogger().info("Generating config.yml");
			this.getConfig().addDefault("MySql.host", "hostname");
			this.getConfig().addDefault("MySql.port", "3306");
			this.getConfig().addDefault("MySql.user", "username");
			this.getConfig().addDefault("MySql.pass", "password");
			this.getConfig().addDefault("MySql.dbase", "database");
			this.getConfig().options().copyDefaults(true);
			this.saveConfig();
			logger.severe(String.format("[IssueTracker] - You must configure your plugin before use!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
			}
		//If config exists but has not been edited
		else if (file.exists() && this.getConfig().getString("MySql.host").equalsIgnoreCase("hostname")){
			logger.severe(String.format("[IssueTracker] - Default configuration detected. Please configure your database information!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
		}
		else {
			getDBInfo();
			try {
				//Checks for database table itrack_issuetracker
				new DatabaseHandler().CreateTable();
				
				//prepares the /issue commands
				this.getCommand("issue").setExecutor(new IssueCommands(this));
				//Enables the Listener for mod joins
				 getServer().getPluginManager().registerEvents(new Listeners(), this);
			} catch (SQLException e) {
				logger.severe(String.format("[IssueTracker] - Couldn't connect to database!", getDescription().getName()));
				getServer().getPluginManager().disablePlugin(this);		
			}
		}	
	}
	//Disables the plugin
	public void onDisable() {
		this.logMessage("Disabled.");
		}
	
	//Logger method
	public void logMessage(String load){
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("[" + pdfFile.getName() + "]" + " v" + pdfFile.getVersion() + " is " + load);
		}
	
	public void getDBInfo(){
		username = this.getConfig().getString("MySql.user");
		password = this.getConfig().getString("MySql.pass");
		host = this.getConfig().getString("MySql.host");
		port = this.getConfig().getString("MySql.port");
		database = this.getConfig().getString("MySql.dbase");
	}

}