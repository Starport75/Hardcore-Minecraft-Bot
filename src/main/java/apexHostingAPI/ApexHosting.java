package apexHostingAPI;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApexHosting {
	public static void main() {
		API.initialize();
	}
	
	public static void changeWorld(String worldName) {
		Utility.print("Changing World To \"" + worldName + "\"");
		API.changeWorld(worldName);
		Utility.print("Restarting Server");
		API.restartServer();
	}
	
	public static String[] getPossibleDeath() {
		String output = API.getConsole();
		String[] lines = output.split("\n");
		
		Pattern pattern = Pattern.compile("\\[Server\\] Server thread\\/INFO ([A-Za-z0-9_]{3,16}) (.+)");
		
		ArrayList<String> deathMessages = Utility.loadFile("death_messages.txt");
		
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			
			if (matcher.find()) {
				String name = matcher.group(1);
				String reason = matcher.group(2);
				
				for (String message : deathMessages) {
					if (reason.contains(message)) {
						return new String[] {name, reason};
					}
				}
				
			}
		}
		return null;
	}
	
	public static ArrayList<String[]> getChatMessages() {
		String rawData = API.getChat();
		if (rawData.length() == 0) {
			return null;
		}
		
		String[] rawMessages = rawData.split("\n");
		Pattern pattern = Pattern.compile("<([A-Za-z0-9_]{3,16})>\\s+(.+)");
		
		ArrayList<String[]> messages = new ArrayList<String[]>();
		for (String rawMessage : rawMessages) {
			Matcher matcher = pattern.matcher(rawMessage);
			if (matcher.find()) {
				String name = matcher.group(1);
				String message = matcher.group(2);
				messages.add(new String[] {name, message});
			}
		}
		messages.forEach(message -> {
			Utility.print("Chat Message: \"" + message[1] + "\"");
		});
		return messages;
	}
	
	public static void sendChatMessage(String username, String message) {
		String command = String.format("/tellraw @a {\"text\":\"<%s> %s\"}", username, message);
		API.sendConsoleCommand(command);
	}
	
	public static void sendConsoleCommand(String command) {
		API.sendConsoleCommand(command);
	}
	
	public static ArrayList<String> getOnlinePlayers() {
		String rawData = API.getOnlinePlayers();
		
		Pattern pattern = Pattern.compile("([A-Za-z0-9_]{3,16})<\\/span>");
		Matcher matcher = pattern.matcher(rawData);
		
		ArrayList<String> players = new ArrayList<String>();
		while (matcher.find()) {
			String player = matcher.group(1);
			players.add(player);
		}
		return players;
	}
}
