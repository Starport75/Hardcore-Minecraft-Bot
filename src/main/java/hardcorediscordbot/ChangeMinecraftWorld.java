package hardcorediscordbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.nio.charset.Charset;

import net.dongliu.requests.Cookie;
import net.dongliu.requests.Requests;
import net.dongliu.requests.Session;

public class ChangeMinecraftWorld {
	static final String loginURL = "https://panel.apexminecrafthosting.com/site/login";
	static final String endpointURL = "https://panel.apexminecrafthosting.com/server/629846"; 
	
	private static String username;
	private static String password;
	
	private static Session session = Requests.session();
	
	public static void main(String worldName) {
		loadCredentials();
		
		session.get(loginURL).send().readToText();
		
		List<Cookie> cookies = session.currentCookies();
		String crsfToken = null;
		
		for (Cookie cookie : cookies) {
			if (cookie.name().equals("YII_CSRF_TOKEN")) {
				String rawToken = cookie.value();
				crsfToken = cropCSRFToken(rawToken);
				break;
			}
		}
		
		logIn(crsfToken);
		changeWorld(crsfToken, worldName);
		System.out.println("Changed world.");
		
		restartServer(crsfToken);
		System.out.println("Restarted server.");
	}
	
	private static void loadCredentials() {
		try {
			Scanner scanner = new Scanner(new File("src/main/resources/credentials.txt"));

			username = scanner.nextLine();
			password = scanner.nextLine();
			
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String cropCSRFToken(String token) {
		String decodedToken = URLDecoder.decode(token, Charset.defaultCharset());
		return decodedToken.substring(46, 132);
	}
	
	private static void logIn(String crsfToken) {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("YII_CSRF_TOKEN", crsfToken);
		formData.put("LoginForm[name]", username);
		formData.put("LoginForm[password]", password);
		formData.put("LoginForm[rememberMe]", "0");
		formData.put("LoginForm[ignoreIp]", "0");
		formData.put("LoginForm[ignoreIp]", "1");
		formData.put("yt0", "Login");
		
		post(loginURL, formData);
	}
	
	private static void changeWorld(String crsfToken, String worldName) {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("YII_CSRF_TOKEN", crsfToken);
		formData.put("ajax", "changeWorld");
		formData.put("changeWorldName", worldName);
		
		post(endpointURL, formData);
	}
	
	private static void restartServer(String crsfToken) {
		HashMap<String, Object> formData = new HashMap<>();
		formData.put("YII_CSRF_TOKEN", crsfToken);
		formData.put("ajax", "restartFast");
		
		post(endpointURL, formData);
	}
	
	private static void post(String url, Map<String, Object> data) {
		session.post(url).body(data).send().toTextResponse();
	}
}
