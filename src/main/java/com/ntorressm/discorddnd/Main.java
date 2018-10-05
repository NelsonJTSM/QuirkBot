package com.ntorressm.discorddnd;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static DiscordApi api;

    static String botToken;
    static long botId;

    static final String CARLOSMEME = "I leave because I am worthless and can't defend myself for shit.";

    static Game game;
    static Server server;
    static Player dm = null;

    /*
    java Main robot_id robot_token
     */

    public static void main(String[] args) throws Exception {
        botToken = args[1];
        botId = Long.parseLong(args[0]);

        game = new Game();
        server = null;

        api = new DiscordApiBuilder().setToken(botToken).login().join();

        api.addMessageCreateListener(event -> {
            long authorId = event.getMessage().getAuthor().getId();
            String authorName = null;
            String message = event.getMessage().getContent();

            try {
                authorName = api.getUserById(authorId).get().getName();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (authorId != botId) {
                // General
                if (message.equals("$carlos")) {
                    event.getChannel().sendMessage(CARLOSMEME);
                } else if (message.equals("$help")) {
                    event.getChannel().sendMessage(getHelp());
                } else if (message.equals("$dmhelp")) {
                    event.getChannel().sendMessage(getHelpDM());
                } else if (message.equals("$playerhelp")) {
                    event.getChannel().sendMessage(getHelpPlayer());
                } else if (message.equals("$join")) {
                    event.getChannel().sendMessage(game.playerJoin(new Player(authorId, authorName)));
                } else if (message.equals("$players")) {
                    event.getChannel().sendMessage(game.getAllPlayers());
                } else if (message.startsWith("$add")) {
                    ArrayList<Player> players = new ArrayList<>();
                    List<User> users = event.getMessage().getMentionedUsers();
                    System.out.println(users.size());

                    for (int u = 0; u < users.size(); u++) {
                        players.add(new Player(users.get(u).getId(), users.get(u).getName()));
                    }

                    event.getChannel().sendMessage(game.addPlayers(players, authorId));
                } else if (message.equals("$dm")) {
                    event.getChannel().sendMessage(game.setDM(new DungeonMaster(authorId, authorName)));

                    server = event.getServer().get();

                    try {
                        server.updateNickname(api.getUserById(authorId).get(), "Dungeon Master");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (message.startsWith("$setdm")) {
                    if (event.getMessage().getMentionedUsers().size() != 1) {
                        event.getChannel().sendMessage("Correct format: $setdm @User");
                    } else {
                        User user = event.getMessage().getMentionedUsers().get(0);
                        event.getChannel().sendMessage(game.setNewDM(new DungeonMaster(user.getId(), user.getName())));
                    }
                } else if (message.startsWith("$choose")) {
                    event.getChannel().sendMessage(game.chooseQuirk(authorId, message.substring("$choose".length()).trim()));
                    sendDMLog(api);
                } else if (message.equals("$start")) {
                    event.getChannel().sendMessage(game.start(authorId));

                    if (game.isRunning()) {
                        for (int p = 0; p < game.getPlayers().size(); p++) {
                            Player player = game.getPlayers().get(p);
                            try {
                                api.getUserById(player.getId()).get().sendMessage(player.getPotentialQuirks());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (message.equals("$allquirks")) {
                    event.getChannel().sendMessage(game.getAllQuirks(authorId));
                } else if (message.startsWith("$dice") || message.startsWith("$roll")) {
                    event.getChannel().sendMessage(game.rollDice(authorId, authorName, message.substring("$dice".length()).trim()));
                } else if (message.equals("$gamestatus")) {
                    event.getChannel().sendMessage(game.getGameStatus(authorId));
                } else if (message.startsWith("$status")) {
                    String[] messageSplit = message.split(" ");

                    if (messageSplit.length != 2) {
                        event.getChannel().sendMessage("Correct usage: $status @Player\n");
                    }  else if (event.getMessage().getMentionedUsers().size() != 1) {
                        event.getChannel().sendMessage("You have to name only 1 Player.\n");
                    } else {
                        User user = event.getMessage().getMentionedUsers().get(0);
                        event.getChannel().sendMessage(game.getPlayerStatus(authorId, user.getId()));
                    }
                } else if (message.equals("$mystatus")) {
                    event.getChannel().sendMessage(game.getPlayerStatus(authorId));
                } else if (message.startsWith("$heroname")) {
                    event.getChannel().sendMessage(game.setPlayerHeroName(authorId, message.substring("$heroname".length())));
                    try {
                        server.updateNickname(api.getUserById(authorId).get(), game.getPlayerFromId(authorId).getHeroName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendDMLog(api);
                } else if (message.startsWith("$damage")) {
                    String[] messageSplit = message.split(" ");

                    if (messageSplit.length != 3) {
                        event.getChannel().sendMessage("Correct format: $damage @Player [amount].\n");
                    } else if (event.getMessage().getMentionedUsers().size() != 1) {
                        event.getChannel().sendMessage("You can only damage 1 person.\n");
                    } else {
                        try {
                            int damage = Integer.parseInt(messageSplit[2]);
                            User user = event.getMessage().getMentionedUsers().get(0);

                            event.getChannel().sendMessage(game.damagePlayer(authorId, user.getId(), damage));
                        } catch (Exception e) {
                            event.getChannel().sendMessage(String.format("%s is not a number.\n", messageSplit[2]));
                        }
                    }
                } else if (message.equals("$users")) {
                    event.getChannel().sendMessage(game.getAllPlayers());
                } else if (message.startsWith("$heal")) {
                    String[] messageSplit = message.split(" ");

                    if (messageSplit.length != 3) {
                        event.getChannel().sendMessage("Correct format: $heal @Player [amount].\n");
                    } else if (event.getMessage().getMentionedUsers().size() != 1) {
                        event.getChannel().sendMessage("You can only heal 1 person.\n");
                    } else {
                        try {
                            int heal = Integer.parseInt(messageSplit[2]);
                            User user = event.getMessage().getMentionedUsers().get(0);

                            event.getChannel().sendMessage(game.healPlayer(authorId, user.getId(), heal));
                        } catch (Exception e) {
                            event.getChannel().sendMessage(String.format("%s is not a number.\n", messageSplit[2]));
                        }
                    }
                } else if (message.equals("$reset")) {
                    event.getChannel().sendMessage(game.reset(authorId));
                } else if (message.equals("$myrolls")) {
                    event.getChannel().sendMessage(game.getPlayerRolls(authorId));
                } else if (message.startsWith("$")){
                    event.getChannel().sendMessage(String.format("Invalid command \"%s\" Use $help to see possible commands", message));
                }
            }
        });
    }

    public static void sendDMLog(DiscordApi api) {
        try {
            api.getUserById(game.getDM().getId()).get().sendMessage(game.getDMLog());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getHelp() {
        String output = "";
        output += "$help - You just used this, are you stupid?\n";
        output += "$dmhelp - Prints out possible commands for the dm.\n";
        output += "$playerhelp - Prints out possible commands for the players.\n";
        output += "$users - Prints out all users in the game.\n";

        return output;
    }

    public static String getHelpDM() {
        String output = "";

        output += "$dm - Sets the user as the DM.\n";
        output += "$setdm [User] - Transfer DM to another user.\n";
        output += "$add [User] - Adds user to the game.\n";
        output += "$start - Starts the game.\n";
        output += "$reset - Resets the game.\n";
        output += "$status - Shows all player quirks.\n";
        output += "$health [Player] - Returns the health of a player.\n";
        output += "$damage [Player] [num] - Damages the player.\n";
        output += "$heal [Player] [num] - Heals the player.\n";

        return output;
    }

    public static String getHelpPlayer() {
        String output = "";

        output += "$join - Join the players.\n";
        output += "$mystatus - Get player status.\n";
        output += "$choose [num] - Picks the quirks that the user wants.\n";
        output += "$heroname [name] - Picks the hero name for the user.\n";
        output += "$myrolls - Returns a summary of all your rolls.\n";

        return output;
    }
}


