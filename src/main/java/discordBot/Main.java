package discordBot;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.*;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.*;
import org.javacord.api.entity.user.User;

import apexHostingAPI.ApexHosting;

public class Main {
	private static String token;
	private static DiscordApi api;
	private static String commandPrefix = "!";
	
	private static String serverID = "814169786232995850";
	private static Server server;
	
	private static String botAnnouncementChannelID = "815713609510027265";
	private static TextChannel botAnnouncementChannel;
	private static String minecraftChatChannelID = "815789202087477258";
	private static TextChannel minecraftChatChannel;
	
	private static String adminRoleID = "814184008388182026";
	private static Role adminRole;
	private static String playersRoleID = "815377435884388362";
	private static Role playersRole;
	private static String runMurdererRoleID = "815406531155722250";
	private static Role runMurdererRole;
	
	private static Players players = new Players();
	private static Attempts attempts = new Attempts();

	public static void main(String[] args) throws InterruptedException {
		token = loadToken();
		api = new DiscordApiBuilder().setToken(token).login().join();
		System.out.println(token);

		ApexHosting.main();
		
		players.readFromFile();
		attempts.readFromFile();
		
		// Print the invite url of your bot
		System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
		
		server = api.getServerById(serverID).get();
		
		botAnnouncementChannel = api.getTextChannelById(botAnnouncementChannelID).get();
		minecraftChatChannel = api.getTextChannelById(minecraftChatChannelID).get();
		
		adminRole = api.getRoleById(adminRoleID).get();
		playersRole = api.getRoleById(playersRoleID).get();
		runMurdererRole = api.getRoleById(runMurdererRoleID).get();
		
		api.addMessageCreateListener(event -> {
			Message message = event.getMessage();
			String messageContent = message.getContent().strip();
			MessageAuthor author = event.getMessageAuthor();
			TextChannel channel = event.getChannel();
			
			boolean isCommand = messageContent.startsWith(commandPrefix);
			if (isCommand) {
				String[] command = messageContent.substring(1).split(" ");
				String commandName = command[0];
				String[] arguments = Arrays.copyOfRange(command, 1, command.length);
				
				String username = null;
				Player player = null;
				if (arguments.length > 0) {
					username = arguments[0];
					player = players.findPlayerWM(username);
				}
				
				switch (commandName) {
				case "begin":
					if (attempts.currentAttemptNumber() == -1) {
					    String format = "%s!\n\nThe first world has been created! Good luck on your first attempt!";
					    String announcement = String.format(format, playersRole.getMentionTag());
					    botAnnouncementChannel.sendMessage(announcement);
					    attempts.addAttempt(new Attempt(1), true);
					} else {
					    channel.sendMessage("You have already began attempts!");
					}
				case "death":
					// Duplicate of the one in the main loop, consider moving to a function
					try {
						User discordUser = api.getUserById(player.getDiscordID()).get();
					    String format = "%s!\n\n%s has died! This means that the server will reset, and attempt %d will begin shortly!";
					    String announcement = String.format(format, playersRole.getMentionTag(), discordUser.getMentionTag(), attempts.currentAttemptNumber() + 1);
					    botAnnouncementChannel.sendMessage(announcement);
					    server.addRoleToUser(discordUser, runMurdererRole);
					} catch (InterruptedException | ExecutionException e) {
					    e.printStackTrace();
					}

					player.addReset();
					players.saveToFile();
					attempts.currentAttempt().endRun(username);
					attempts.addAttempt(new Attempt(attempts.currentAttemptNumber() + 1), true);
					break;
				case "addPlayer":
					if (username != null) {
					    if (players.uniqueMUsername(username)) {
					        if (players.uniqueDID(author.getId())) {
					            Player newPlayer = new Player(username, author.getId());
					            players.addPlayer(newPlayer);
					            try {
						            User discordUser = api.getUserById(newPlayer.getDiscordID()).get();
					                server.addRoleToUser(discordUser, playersRole);
					                String format = "Thank you %s! You have been linked with the Minecraft username `%s`!";
					                String response = String.format(format, discordUser.getMentionTag(), newPlayer.getMinecraftUsername());
					                channel.sendMessage(response);
					            } catch (InterruptedException | ExecutionException e) {
					                e.printStackTrace();
					            }
					        } else {
					            channel.sendMessage("Your Discord ID has already been added to the database!");
					        }
					    } else {
					        channel.sendMessage("That username has already been added to the database!");
					    }
					} else {
					    channel.sendMessage("Please type the command as: `!addPlayer <Minecraft Username>`.");
					}
					break;
				case "listPlayers":
					channel.sendMessage("```" + players.listPlayers() + "```");
					break;
				case "listAttempts":
					channel.sendMessage("```" + attempts.listAttempts() + "```");
					break;
				case "listOnline":
					ArrayList<String> onlinePlayers = ApexHosting.getOnlinePlayers();
					if (onlinePlayers.size() == 0) {
					    channel.sendMessage("```There are currently no players online.```");
					} else {
					    String response = "Players Online: \n";
					    String format = "%s (@%s)\n";
					    for (String onlinePlayerName : onlinePlayers) {
					    	Player onlinePlayer = players.findPlayerWM(onlinePlayerName);
					    	long discordUserID = onlinePlayer.getDiscordID(); 
					    	User discordUser = null;
							try {
								discordUser = api.getUserById(discordUserID).get();
							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
							}
					    	
					        response += String.format(format, onlinePlayerName, discordUser.getMentionTag());
					    }
					    channel.sendMessage("```" + response + "```");
					}
					break;
				default:
					channel.sendMessage("This command does not exist.");
				}
			}
			
			if (channel == minecraftChatChannel && !author.isYourself()) {
		        ApexHosting.sendChatMessage("@" + author.getDisplayName(), messageContent);
		    }
		});

		while (true) {
			System.out.println("Updating console.");
			ApexHosting.updateConsole();
			System.out.println("Updating chat.");
			ApexHosting.updateChat();

			System.out.println("Getting possible deaths.");
			String[] possibleDeath = ApexHosting.getPossibleDeath();
		    if (possibleDeath != null) {
		        String mcUsername = possibleDeath[0];
		        String reason = possibleDeath[1];
		        Player player = players.findPlayerWM(mcUsername);
		        try {
		            User discordUser = api.getUserById(player.getDiscordID()).get();
		            String format = "%s!\n\n%s %s! This means that the server will reset, and attempt %d will begin shortly!";
		            String announcement = String.format(format, playersRole.getMentionTag(), discordUser.getMentionTag(), reason, attempts.currentAttemptNumber());
		            botAnnouncementChannel.sendMessage(announcement);
		            server.addRoleToUser(discordUser, runMurdererRole);
		        } catch (InterruptedException | ExecutionException e) {
		            e.printStackTrace();
		        }

		        player.addReset();
		        players.saveToFile();
		        attempts.currentAttempt().endRun(mcUsername);
		        attempts.addAttempt(new Attempt(attempts.currentAttemptNumber() + 1), true);
		        
		        ApexHosting.clearConsole();
		        ApexHosting.clearChat();
		    }

		    System.out.println("Getting chat messages.");
		    ArrayList<String[]> chatMessages = ApexHosting.getChatMessages();
		    if (chatMessages != null) {
		        for (String[] message : chatMessages) {
		            String username = message[0];
		            String output = message[1];
		            output = output.replaceAll("@everyone", "@ everyone")
		                           .replaceAll("@Run Murderer", runMurdererRole.getMentionTag())
		                           .replaceAll("@Admin", adminRole.getMentionTag())
		                           .replaceAll("@Players", playersRole.getMentionTag());
		            for (int i = 0; i < players.length(); i++) {
		            	Player player = players.getPlayer(i);
		                try {
		                	User user = api.getUserById(player.getDiscordID()).get();
		                    output = output.replaceAll("@" + user.getDisplayName(server), user.getMentionTag());
		                } catch (InterruptedException | ExecutionException e) {
		                    e.printStackTrace();
		                }
		            }
		            minecraftChatChannel.sendMessage("**<" + username + ">** " + output);
		        }
		    }
		    
			System.out.println("[LOOP]");
		    Thread.sleep(5000);
		}
	}

	private static String loadToken() {
		String token = null;
		try {
			Scanner scanner = new Scanner(new File("src/main/resources/bot_token.txt"));

			token = scanner.nextLine();

			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return token;
	}
}
