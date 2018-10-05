package com.ntorressm.discorddnd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Game {

    private DungeonMaster dm;
    private ArrayList<Player> players;
    private HashMap<Long, Player> playerMap;
    private boolean quirkless;
    private boolean running;
    private String dmLog;

    private File quirksFile;
    private Scanner scan;
    private ArrayList<String> strongQuirksList;
    private ArrayList<String> weakQuirksList;

    public Game() {
        setupQuirkArray();

        reset();
    }

    public String reset(long id) {
        if (!running)
            return "Can't reset retard, it's not running.\n";

        if (id != dm.getId())
            return "Only the DM can reset the game.\n";

        return reset();
    }

    public String reset() {
        this.players = new ArrayList<>();
        this.playerMap = new HashMap<>();
        this.dm = null;
        this.quirkless = false;
        this.dmLog = "";
        this.running = false;

        return "Successful reset";
    }

    public boolean containsPlayer(long id) {
        return playerMap.containsKey(id);
    }

    public String chooseQuirk(long id, String message) {
        if (!running)
            return "The game is not running.\n";

        if (dm.getId() == id)
            return String.format("Stop %s, you are the DM, and this command is only for players.\n", dm.getName());
        if (!playerMap.containsKey(id))
            return "You are not a player.\n";

        int choice = -1;
        try {
            choice = Integer.parseInt(message);
        } catch (Exception e) {
            e.printStackTrace();
            return String.format("Wrong input format, use $choose [num].\n");
        }

        if (choice == 1 || choice == 2) {
            String output = playerMap.get(id).chooseQuirks(choice);
            dmLog = String.format("[%s] %s", playerMap.get(id).getName(), output);
            return output;
        }

        return String.format("Wrong input, please pick 1 or 2.\n");
    }

    public String setNewDM(DungeonMaster dm) {
        this.dm = dm;
        return String.format("Successfully made %s the DM.\n", dm.getName());
    }

    public String setDM(DungeonMaster dm) {
        if (this.dm != null) {
            return String.format("%s is already the DM.\n", this.dm.getName());
        }

        this.dm = dm;
        return String.format("Successfully made %s the DM.\n", dm.getName());
    }

    public Player getPlayerFromId(long id) {
        return playerMap.get(id);
    }

    public String setQuirkless(long id) {
        if (id != dm.getId())
            return "Only the DM can do that.\n";

        quirkless = true;
        return "The game now has 1 quirkless person.\n";
    }

    public String start(long id) {
        if (dm == null) {
            return "Can't start, no DM.\n";
        }

        if (id != dm.getId()) {
            return "Only DMs can start the game.\n";
        }

        if (players.size() <= 1) {
            return "Not enough players.\n";
        }

        Collections.shuffle(strongQuirksList);
        Collections.shuffle(weakQuirksList);
        Collections.shuffle(players);

        int beginning = 0;
        if (quirkless) {
            players.get(0).setQuirks("Carlos' Fat", "Nelson's Forehead", "Daniel's Nose");
            beginning = 1;
        }

        for (int i = beginning; i < players.size(); i++) {
            if (strongQuirksList.size() >= 1 && weakQuirksList.size() >= 2) {
                players.get(i).setQuirks(strongQuirksList.remove(0), weakQuirksList.remove(0), weakQuirksList.remove(0));
            } else {
                return "Not enough quirks for all players.\n";
            }
        }

        running = true;
        return "Starting game...\n";
    }

    public String damagePlayer(long dmId, long playerId, int amount) {
        if (dmId != dm.getId())
            return "Can't change damage.";

        return playerMap.get(playerId).damage(amount);
    }

    public String healPlayer(long dmId, long playerId, int amount) {
        if (dmId != dm.getId())
            return "Can't change damage.";

        return playerMap.get(playerId).heal(amount);
    }

    public String getPlayerStatus(long dmId, long playerId) {
        if (dmId != dm.getId())
            return "Only DMs can do this.\n";

        return playerMap.get(playerId).getHiddenStatus();
    }

    public String addPlayers(ArrayList<Player> players1, long id) {
        if (dm == null || id != dm.getId())
            return "Only the DM can add players.\n";

        System.out.println(players1.size());
        if (players1.isEmpty())
            return "No Players added.\n";

        String output = "Added player(s): ";
        for (int i = 0; i < players1.size(); i++) {
            playerJoin(players1.get(i));
            output += players1.get(i).getName() + " ";
        }

        output += "\n";
        return output;
    }

    public boolean isRunning() {
        return running;
    }

    public String playerJoin(Player player) {
        if (running)
            return "The game is already running.\n";

        if (playerMap.containsKey(player.getId()))
            return String.format("Player %s already added.\n", player.getName());

        if (player.getId() == dm.getId())
            return "The DM can't be a player.\n";

        players.add(player);
        playerMap.put(player.getId(), player);

        return String.format("Added player %s.\n", player.getName());
    }

    public String rollDice(long id, String name, String message) {
        try {
            int highest = Integer.parseInt(message);

            if (highest > 0) {
                int random = (int)(Math.random()*highest)+1;

                if (playerMap.containsKey(id)) {
                    return playerMap.get(id).addRoll(random);
                } else {
                    return String.format("%s rolled a %d.\n", name, random);
                }
            } else {
                return "Please choose an actual fucking number.\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Proper use: $roll [num].\n";
        }
    }

    public String setPlayerHeroName(long id, String heroName) {
        if (!playerMap.containsKey(id))
            return "You are not a player.\n";

        if (heroName.length() == 0) {
            return "Please enter an actual player name.\n";
        }

        if (heroName.startsWith(" ")) {
            heroName = heroName.substring(1);
        }

        Player player = playerMap.get(id);
        player.setHeroName(heroName);
        dmLog = String.format("[%s] set hero name to %s.\n", player.getName(), heroName);
        return String.format("Set hero name to %s.\n", heroName);
    }

    public String getPlayerRolls(long id) {
        return playerMap.get(id).printRolls();
    }

    public String getGameStatus(long id) {
        if (id != dm.getId())
            return "Unable to use this command, you are not the DM.\n";

        String output = "Current Game Status...\n";
        output += String.format("DM: %s.\n\n", dm.getName());

        for (int i = 0; i < players.size(); i++) {
            output += players.get(i).getHiddenStatus();
        }

        return output;
    }

    public String getAllPlayers() {
        String output = "Players:\n";
        for (int i = 0; i < players.size(); i++) {
            output += players.get(i).getName()+"\n";
        }
        return output;
    }

    public boolean hasPlayer(long id) {
        return playerMap.containsKey(id);
    }

    public String getPlayerStatus(long id) {
        return playerMap.get(id).getStatus();
    }

    private void setupQuirkArray() {
        this.quirksFile = new File("quirks.txt");
        this.strongQuirksList = new ArrayList<>();
        this.weakQuirksList = new ArrayList<>();

        // Setup quirk list from file
        try {
            scan = new Scanner(quirksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        scan.nextLine();
        String line = scan.nextLine();
        while (!line.startsWith("-")) {
            strongQuirksList.add(line);
            line = scan.nextLine();
        }

        line = scan.nextLine();
        while(scan.hasNext()) {
            weakQuirksList.add(line);
            line = scan.nextLine();
        }

        scan.close();
    }

    public String getAllQuirks(long id) {
        if (dm == null)
            return "No DM set";

        if (id != dm.getId()) {
            return "Only the DM can see all player quirks.\n";
        }

        String output = "";
        output += "ALL QUIRKS\n";
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            output += String.format("%s (%s):\n", player.getName(), player.getHeroName());
            output += String.format("%s\n\n", player.getQuirks());
        }

        return output;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getDMLog() {
        return dmLog;
    }

    public DungeonMaster getDM() {
        return dm;
    }
}
