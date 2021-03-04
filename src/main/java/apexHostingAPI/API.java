package apexHostingAPI;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.dongliu.requests.Cookie;
import net.dongliu.requests.Requests;
import net.dongliu.requests.Session;

public class API {
	private static final String loginURL = "https://panel.apexminecrafthosting.com/site/login";
	private static final String endpointURL = "https://panel.apexminecrafthosting.com/server/629846"; 
	private static final String consoleURL = "https://panel.apexminecrafthosting.com/server/log/629846";
	private static final String chatURL = "https://panel.apexminecrafthosting.com/server/chat/629846";
	
	private static String username;
	private static String password;
	private static String crsfToken;
	
	private static Session session = Requests.session();
	
	static void initialize() {
		ArrayList<String> credentials = Utility.loadFile("credentials.txt");
		username = credentials.get(0);
		password = credentials.get(1);
		Utility.print("Username: " + username + "; Password: " + password);
		
		session.get(loginURL).send();
		
		List<Cookie> cookies = session.currentCookies();
		for (Cookie cookie : cookies) {
			if (cookie.name().equals("YII_CSRF_TOKEN")) {
				String rawToken = cookie.value();
				crsfToken = cropCSRFToken(rawToken);
				break;
			}
		}
		Utility.print("CSRF Token: " + crsfToken);
		
		Utility.print("Logging In");
		logIn();
		Utility.print("Logged In");
	}
	
	private static String cropCSRFToken(String token) {
		String decodedToken = URLDecoder.decode(token, Charset.defaultCharset());
		return decodedToken.substring(46, 132);
	}
	
	private static String post(String url, Map<String, Object> data) {
		//Utility.print("POST: \"" + url + "\"");
		data.put("YII_CSRF_TOKEN", crsfToken);
		
		String response = session.post(url).body(data).socksTimeout(20_000).connectTimeout(30_000).send().readToText();
		return response;
	}
	
	private static void logIn() {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("LoginForm[name]", username);
		formData.put("LoginForm[password]", password);
		formData.put("LoginForm[rememberMe]", "0");
		formData.put("LoginForm[ignoreIp]", "0");
		formData.put("LoginForm[ignoreIp]", "1");
		formData.put("yt0", "Login");
		
		post(loginURL, formData);
	}
	
	static void changeWorld(String worldName) {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("ajax", "changeWorld");
		formData.put("changeWorldName", worldName);
		
		Utility.print("Changing World To " + worldName);
		post(endpointURL, formData);
	}
	
	static void restartServer() {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("ajax", "restartFast");
		
		Utility.print("Restart Server");
		post(endpointURL, formData);
	}
	
	static String getConsole() {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("ajax", "refresh");
		formData.put("type", "all");
		formData.put("log_seq", "0");
		
		Utility.print("Getting Console");
		String rawData = post(consoleURL, formData);
		clearConsole();
		
		JSONParser parser = new JSONParser();
		JSONObject data = null;
		try {
			data = (JSONObject) parser.parse(rawData);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return (String) data.get("log");
	}
	
	static void sendConsoleCommand(String command) {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("ajax", "command");
		formData.put("command", command);
		
		Utility.print("Sending Console Command: \"" + command + "\"");
		post(consoleURL, formData);
	}
	
	private static void clearConsole() {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("ajax", "clearLog");
		
		Utility.print("Clearing Console");
		post(consoleURL, formData);
	}
	
	static String getChat() {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("ajax", "refresh");
		formData.put("type", "all");
		formData.put("log_seq", "0");
		
		Utility.print("Getting Chat");
		String rawData = post(chatURL, formData);
		clearChat();
		
		JSONParser parser = new JSONParser();
		JSONObject data = null;
		try {
			data = (JSONObject) parser.parse(rawData);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return (String) data.get("chat");
	}
	
	private static void clearChat() {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("ajax", "clearChat");
		
		Utility.print("Clearing Chat");
		post(chatURL, formData);
	}
	
	static String getOnlinePlayers() {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("ajax", "refresh");
		formData.put("type", "all");
		formData.put("log_seq", "0");
		
		Utility.print("Getting Online Players");
		String rawData = post(chatURL, formData);
		
		JSONParser parser = new JSONParser();
		JSONObject data = null;
		try {
			data = (JSONObject) parser.parse(rawData);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return (String) data.get("players");
	}
}
