package apexHostingAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

public class ApexHosting {
	private static JSONObject console;
	private static Integer consoleIndex = 0;
	private static String[] consoleLog;
	
	private static JSONObject chat;
	private static ArrayList<String[]> chatMessages;
	private static String[] lastChatMessage;
	
	public static void main() {
		API.initialize();
	}
	
	public static void updateConsole() {
		int logOffset = 0;
		if (consoleIndex != 0) {
			logOffset = 1;
		}
		
		console = API.getConsole(consoleIndex);
		consoleIndex = Integer.parseInt(console.get("log_seq").toString());
		
		consoleLog = ((String) console.get("log")).split("\n");
		consoleLog = Arrays.copyOfRange(consoleLog, 0, consoleLog.length - logOffset);
		Collections.reverse(Arrays.asList(consoleLog));
	}
	
	public static void updateChat() {
		chat = API.getChat();
		
		Pattern pattern = Pattern.compile("<([A-Za-z0-9_]{3,16})>\\s+(.+)");
		String[] rawMessages = chat.get("chat").toString().split("\n");
		
		ArrayList<String[]> retrievedChatMessages = new ArrayList<String[]>();
		for (String rawMessage : rawMessages) {
			Matcher matcher = pattern.matcher(rawMessage);
			if (matcher.find()) {
				String name = matcher.group(1);
				String message = matcher.group(2);
				retrievedChatMessages.add(new String[] {name, message});
			}
		}
		
		if (retrievedChatMessages.size() == 0) {
			return;
		}
		
		if (lastChatMessage == null) {
			lastChatMessage = retrievedChatMessages.get(retrievedChatMessages.size() - 1);
		}
		
		int i;
		for (i = retrievedChatMessages.size() - 1; i >= 0; i--) {
			String[] message = retrievedChatMessages.get(i);
			if (message[0].equals(lastChatMessage[0]) && message[1].equals(lastChatMessage[1])) {
				break;
			}
		}
		
		List<String[]> subList = retrievedChatMessages.subList(i + 1, retrievedChatMessages.size());
		chatMessages = new ArrayList<String[]>(subList);
		
		if (chatMessages.size() > 0) {
			lastChatMessage = chatMessages.get(chatMessages.size() - 1);
		}
	}
	
	public static String[] getConsoleLog() {
		return consoleLog;
	}
	
	public static ArrayList<String[]> getChatMessages() {
		return chatMessages;
	}
	
	public static void clearConsole() {
		API.clearConsole();
		consoleIndex = 0;
	}
	
	public static void clearChat() {
		API.clearChat();
		lastChatMessage = new String[2];
	}
	
	public static void changeWorld(String worldName) {
		Utility.print("Changing World To \"" + worldName + "\"");
		API.changeWorld(worldName);
		Utility.print("Restarting Server");
		restartServer();
	}
	
	public static void restartServer() {
		API.restartServer();
	}
	
	public static String[] getPossibleDeath() {
		Pattern pattern = Pattern.compile("\\[Server\\] Server thread\\/INFO ([A-Za-z0-9_]{3,16}) (.+)");
		ArrayList<String> deathMessages = Utility.loadFile("death_messages.txt");
		
		for (String line : consoleLog) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				String name = matcher.group(1);
				String reason = matcher.group(2);
				System.out.println("1 - Name: " + name + " Reason: " + reason);
				for (String deathMessage : deathMessages) {
					if (reason.contains(deathMessage) && !line.contains("Villager")) {
						return new String[] {name, reason};
					}
				}
				
			}
		}
		return null;
	}
	
	public static void sendChatMessage(String username, String message) {
		String command = String.format("/tellraw @a {\"text\":\"<%s> %s\"}", username, message);
		API.sendConsoleCommand(command);
	}
	
	public static void sendConsoleCommand(String command) {
		API.sendConsoleCommand(command);
	}
	
	public static ArrayList<String> getOnlinePlayers() {
		String playerData = (String) chat.get("players");
		
		Pattern pattern = Pattern.compile("([A-Za-z0-9_]{3,16})<\\/span>");
		Matcher matcher = pattern.matcher(playerData);
		
		ArrayList<String> players = new ArrayList<String>();
		while (matcher.find()) {
			String player = matcher.group(1);
			players.add(player);
		}
		return players;
	}
}
