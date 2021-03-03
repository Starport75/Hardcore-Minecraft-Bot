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

import apexHostingAPI.ApexHosting;

public class Attempts {
	private ArrayList<Attempt> attempts = new ArrayList<Attempt>();
	private String fileName = "src/main/resources/attempts.json";
	
	public void addAttempt(Attempt attempt, Boolean changeWorld) {
		attempts.add(attempt);
		
		if (changeWorld) {
			System.out.println("Changing world!");
			ApexHosting.changeWorld("Attempt " + currentAttemptNumber());
		}
		saveToFile();
	}
	
	public String listAttempts() {
	    ArrayList<String> output = new ArrayList<String>();
	    String format = "Attempt #%d lasted from %s until %s and was ended by %s. It lasted for %s.";
	    for (Attempt a : attempts.subList(0, attempts.size() - 1)) {
	        String fString = String.format(format,
	        							   a.getAttemptNumber(),
	        							   a.getStartTimeString(),
	        							   a.getDeathTimeString(),
	        							   a.getRunKiller(),
	        							   milliToTime(a.getDeathTime() - a.getStartTime())
	        							   );
	        output.add(fString);
	    }

	    Attempt finalAttempt = attempts.get(attempts.size() - 1);
	    String finalFormat = "Attempt #%d began on %s and has not ended yet.";
	    String fString = String.format(finalFormat, finalAttempt.getAttemptNumber(), finalAttempt.getStartTimeString());
	    output.add(fString);

	    return String.join("\n", output);
	}
	
	public int length() {
		return attempts.size();
	}
	
	public int currentAttemptNumber() {
		return length();
	}
	
	public Attempt currentAttempt() {
		return attempts.get(currentAttemptNumber());
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
			
			long attemptNumber = (long) obj.get("attemptNumber");
			String runKiller = (String) obj.get("runKiller");
			long startTime = (long) obj.get("startTime");
			long deathTime = (long) obj.get("deathTime");
			long successTime = (long) obj.get("successTime");
			
			Attempt attempt = new Attempt((int) attemptNumber, runKiller, startTime, deathTime, successTime);
			addAttempt(attempt, false);
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
		
		for (Attempt a : attempts) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("attemptNumber", a.getAttemptNumber());
			jsonObject.put("runKiller", a.getRunKiller());
			jsonObject.put("startTime", a.getStartTime());
			jsonObject.put("deathTime", a.getDeathTime());
			jsonObject.put("successTime", a.getSuccessTime());
			
			jsonArray.add(jsonObject);
		}
		
		try (FileWriter file = new FileWriter(fileName)) {
            file.write(jsonArray.toJSONString()); 
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public String milliToTime(long millis) {
		long days;
		long hours;
		long minutes;
		@SuppressWarnings("unused")
		long seconds;
				
		days = millis / 86400000;
		millis %= 86400000;
		hours = millis / 3600000;
		millis %= 3600000;
		minutes = millis / 60000;
		millis %= 60000;
		seconds = millis / 1000;
		millis %= 1000;
		
		return String.format("%d days, %d hours, and %d minutes.", days, hours, minutes);
	}
	
}
