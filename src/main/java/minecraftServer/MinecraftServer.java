package minecraftServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apexHostingAPI.API;

public class MinecraftServer {
	private String serverPath = "C:\\Users\\Chase\\Downloads\\server test";
	private Runtime runtime = Runtime.getRuntime();
	private ArrayList<String> log = new ArrayList<String>();
	
	public void startServer() {
		String command = "C:\\Users\\Chase\\.p2\\pool\\plugins\\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_15.0.2.v20210201-0955\\jre\\bin\\java.exe -jar server.jar nogui";
		
		Thread t = new Thread(() -> {
			Process process = null;
			try {
				process = runtime.exec(command, null, new File(serverPath));
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					log.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		t.start();
	}
	
	public ArrayList<String> getLog() {
		ArrayList<String> out = new ArrayList<String>(log);
		log.clear();
		return out;
	}
	
	public String[] getPossibleDeath(ArrayList<String> log) {
		Pattern pattern = Pattern.compile("\\[Server\\] Server thread\\/INFO ([A-Za-z0-9_]{3,16}) (.+)");
		String[] deathMessages = loadFile("death_messages.txt");
		
		for (String line : log) {
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
	
	public ArrayList<String[]> getChatMessages(ArrayList<String> log) {
		Pattern pattern = Pattern.compile("\\[CHAT\\] <?([A-Za-z0-9_]{3,16})>?(.+)");
		
		ArrayList<String[]> chatMessages = new ArrayList<String[]>();
		for (String message : log) {
			Matcher matcher = pattern.matcher(message);
			if (matcher.find()) {
				String name = matcher.group(1);
				String data = matcher.group(2);
				chatMessages.add(new String[] {name, data});
			}
		}
		return chatMessages;
	}
	
	private static String[] loadFile(String fileName) {
		File file = new File("src/main/resources/" + fileName);
		
		ArrayList<String> fileData = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNext()) {
				fileData.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String[] text = new String[fileData.size()];
		text = fileData.toArray(text);
		return text;
	}
}
