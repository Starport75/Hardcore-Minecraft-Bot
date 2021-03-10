package discordBot;

import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.user.User;

public class Player implements Comparable<Player> {
	private String minecraftUsername;
	private long discordID;
	private long resetCount;
	private long worldCount;
	private long lastWorldJoined;
	
	public Player(String nMinecraftUsername, long nDiscordID) {
		minecraftUsername = nMinecraftUsername;
		discordID = nDiscordID;
		resetCount = 0;
		worldCount = 0;
		lastWorldJoined = -1;
	}
	
	public Player(String nMinecraftUsername, long nDiscordID, long resets, long worlds, long lastWorld) {
		minecraftUsername = nMinecraftUsername;
		discordID = nDiscordID;
		resetCount = resets;
		worldCount = worlds;
		lastWorldJoined = lastWorld;
	}
	
	@Override
	public int compareTo(Player other) {
		Long l = other.resetCount - resetCount;
		return l.intValue();
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
	
	public long getWorldCount() {
		return worldCount;
	}
	
	public long getLastWorld() {
		return lastWorldJoined;
	}
	
	public void addReset() {
		resetCount++;
	}
	
	public void addWorld() {
		worldCount++;
	}
	
	public void setLastWorld(long lastWorld) {
		lastWorldJoined = lastWorld;
	}
	
	public int getRatioPercent() {
		if(worldCount != 0) {
			return (int)((double)(resetCount) / (double)(worldCount) * 100);
		}
		return -1;
	}
	
	// instead of passing the freaking api in, make a discordAPI class or something and let functions call that
	// just like ApexHosting and API.java
	public String getDiscordName() {
		try {
			User discordUser = Main.api.getUserById(discordID).get();
			return discordUser.getDisplayName(Main.server);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
}

