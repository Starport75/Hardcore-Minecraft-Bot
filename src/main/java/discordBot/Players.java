package discordBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Players {
	private ArrayList<Player> players = new ArrayList<Player>();
	private String fileName = "src/main/resources/players.json";
	
	public Players() {
		readFromFile();
	}
	
	public Player getPlayer(int index) {
		return players.get(index);
	}
	
	public void addPlayer(Player player) {
		players.add(player);
		saveToFile();
	}
	
	public String listPlayers() {
		ArrayList<String> playerInfo = new ArrayList<String>();
		String format = "%s (aka %s) has caused %d server reset";
		
		for (Player p : players) {
			long resets = p.getResetCount();
			
			// TODO: Make a static "DiscordAPI" class (or just static Main variable) for Player to reference and use to generate Discord Usernames.   
			// Passing in Main.api and it being a public static variable is probably bad practice in general.
			String info = String.format(format, p.getMinecraftUsername(), p.getDiscordName(Main.api), resets);
			if (resets != 1) {
				info += "s";
			}
			info += ".";
			playerInfo.add(info);
		}
		return String.join("\n", playerInfo);
	}
	
	public int length() {
		return players.size();
	}
	
	public boolean uniqueMUsername(String checkNameM) {
		for (Player p : players) {
			if (checkNameM.equals(p.getMinecraftUsername())) {
				return false;
			}
		}
		return true;
	}
	
	public Player findPlayerWM(String checkNameM){
		for (Player p : players) {
			if (checkNameM.equals(p.getMinecraftUsername())) {
				return p;
			}
		}
		return new Player("Unknown Player", -1);
	}
	
	public boolean uniqueDID(long checkID) {
		for (Player p : players) {
			if (checkID == p.getDiscordID()) {
				return false;
			}
		}
		return true;
	}
	
	public Player findPlayerWD(long checkID){
		for (Player p : players) {
			if (checkID == p.getDiscordID()) {
				return p;
			}
		}
		return new Player("Unknown Player", -1);
	}
	
	public void readFromFile() {
		File file = new File(fileName);
		FileReader reader = null;
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		JSONParser parser = new JSONParser();
		
		Object json = null;
		try {
			json = parser.parse(reader);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		JSONArray jsonArray = (JSONArray) json;
		
		for (Object item : jsonArray) {
			JSONObject obj = (JSONObject) item;
			
			String minecraftUsername = (String) obj.get("minecraftUsername");
			long discordID = (long) obj.get("discordID");
			long resetCount = (long) obj.get("resetCount");
			
			Player player = new Player(minecraftUsername, discordID, resetCount);
			players.add(player);
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveToFile() {
		JSONArray jsonArray = new JSONArray();
		
		for (Player p : players) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("minecraftUsername", p.getMinecraftUsername());
			jsonObject.put("discordID", p.getDiscordID());
			jsonObject.put("resetCount", p.getResetCount());
			
			jsonArray.add(jsonObject);
		}
		
		try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonArray.toJSONString()); 
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
