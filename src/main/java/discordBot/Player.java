package discordBot;

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
	
	public void changeMinecraftUsername(String newUsername) {
		minecraftUsername = newUsername;
	}
}

