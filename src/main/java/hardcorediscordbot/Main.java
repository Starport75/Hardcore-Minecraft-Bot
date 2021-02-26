package hardcorediscordbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {
	private static String token;
	
	public static void main(String[] args) {
		loadToken();
		System.out.println(token);

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        
        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("$ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });
        
        api.addMessageCreateListener(event -> {
        	String message = event.getMessageContent();
        	if (message.contains("/death")) {
        		event.getChannel().sendMessage(message.substring(6) + " has died! This means the server will reset, and attempt #X will begin shortly! " + event.getChannel());
        		
        		ChangeMinecraftWorld.main("Test");
        	}
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }
	
	private static void loadToken() {
		try {
			Scanner scanner = new Scanner(new File("src/main/resources/api_key.txt"));

			token = scanner.nextLine();
			
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
