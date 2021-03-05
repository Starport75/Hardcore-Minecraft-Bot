package discordBot;

import java.util.concurrent.ExecutionException;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;

public class Player {
	private String minecraftUsername;
	private long discordID;
	private long resetCount;
	
	public Player(String nMinecraftUsername, long nDiscordID) {
		minecraftUsername = nMinecraftUsername;
		discordID = nDiscordID;
		resetCount = 0;
	}
	
	public Player(String nMinecraftUsername, long nDiscordID, long resets) {
		minecraftUsername = nMinecraftUsername;
		discordID = nDiscordID;
		resetCount = resets;
	}
	
	public void changeMinecraftUsername(String newUsername) {
		minecraftUsername = newUsername;
	}
	
	public String getMinecraftUsername() {
		return minecraftUsername;
	}
	
	public long getDiscordID() {
		return discordID;
	}
	
	public long getResetCount() {
		return resetCount;
	}
	
	public void addReset() {
		resetCount++;
	}
	
	// instead of passing the freaking api in, make a discordAPI class or something and let functions call that
	// just like ApexHosting and API.java
	public String getDiscordName(DiscordApi api) {
		try {
			User discordUser = api.getUserById(discordID).get();
			return discordUser.getDiscriminatedName();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
}

