package hardcorediscordbot;


import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {
	public static void main(String[] args) {
        // Insert your bot's token here
        String token = "ODE0MjIwOTU5NDAwODUzNTU2.YDasXA._oNNnqtCLxaRB0-bMGtmt7y21v8";

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
        		
        		event.getChannel().sendMessage(message.substring(6) + " Has died! This means the server will reset, and attempt #X will begin shortly! " + event.getChannel());
        	}
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }
}
