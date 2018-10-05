package com.ntorressm.discorddnd;

import java.util.ArrayList;
import java.util.Collections;

public class Player {
    private long id;
    private String name;
    private String heroName;
    private int health;
    ArrayList<Integer> rolls;

    private String[] quirks;
    private String[] possibleQuirks;
    private boolean pickedQuirk;

    private static String WIKI_LINK = "http://bokunoheroacademia.wikia.com/wiki/";

    public Player(long id, String name) {
        this.id = id;
        this.name = name;
        this.heroName = "I PEED AND POOPED AND SHARTED";
        this.health = 15;
        this.rolls = new ArrayList<>();

        this.possibleQuirks = new String[3];
        this.quirks = null;
        this.pickedQuirk = false;
    }

    public String printRolls() {
        ArrayList<Integer> sortedRolls = (ArrayList<Integer>)rolls.clone();
        Collections.sort(sortedRolls);

        String output = "";
        if (!rolls.isEmpty()) {
            double average = 0.0;
            for (int r = 0; r < rolls.size(); r++) {
                average += rolls.get(r);
            }
            average /= rolls.size();

            output += String.format("Minimum: %d\n", sortedRolls.get(0));
            output += String.format("Maximum: %d\n", sortedRolls.get(sortedRolls.size()-1));
            output += String.format("Average: %.2f\n", average);
            output += "History: ";
            for (int r = 0; r < rolls.size(); r++) {
                output += rolls.get(r) + " ";
            }
            output += ".\n";
        } else {
            output += "No rolls.\n";
        }

        return output;
    }

    public String getStatus() {
        String output = "";

        output += String.format("Name: %s\n", name);
        output += String.format("Hero Name: %s\n", heroName);
        output += String.format("Health: %d\n\n", health);

        return output;
    }

    public String getHiddenStatus() {
        String output = "";
        output += String.format("Name: %s\n", name);
        output += String.format("Hero Name: %s\n", heroName);
        if (quirks.length == 1) {
            output += String.format("Quirks: %s\n",quirks[0]);
        } else if (quirks.length == 2) {
            output += String.format("Quirks: %s and %s\n", quirks[0], quirks[1]);
        }
        output += String.format("Health: %d\n\n", health);

        return output;
    }

    public String addRoll(int roll) {
        rolls.add(roll);
        return String.format("Player %s rolled a %d", name, roll);
    }

    public String damage(int amount) {
        health -= amount;

        if (health <= 0) {
            return String.format("Player %s has died.\n", name);
        }

        return String.format("Player %s now has %d health.\n", name, health);
    }

    public String heal(int amount) {
        health += amount;

        return String.format("Player %s now has %d health.\n", name, health);
    }


    public void setHeroName(String heroName) {
        this.heroName = heroName;
    }

    public String getHeroName() {
        return heroName;
    }

    public String getPotentialQuirks() {
        String output = "Potential Quirks:\n";
        output += String.format("$choose 1 for %s.\n", possibleQuirks[0]);
        output += String.format("$choose 2 for %s/%s.\n", possibleQuirks[1], possibleQuirks[2]);
        output += (WIKI_LINK+possibleQuirks[0]).replaceAll(" ", "_") + "\n";
        output += (WIKI_LINK+possibleQuirks[1]).replaceAll(" ", "_") + "\n";
        output += (WIKI_LINK+possibleQuirks[2]).replaceAll(" ", "_") + "\n";
        return output;
    }

    public void setQuirks(String q1, String q2, String q3) {
        possibleQuirks[0] = q1;
        possibleQuirks[1] = q2;
        possibleQuirks[2] = q3;
    }

    public String getQuirks() {
        if (quirks == null)
            return "No quirk chosen";
        if (quirks.length == 1)
            return quirks[0];
        if (quirks.length == 2)
            return quirks[0] + "/" + quirks[1];

        return "Someone messed up";
    }

    public String chooseQuirks(int choice) {
        if (pickedQuirk) {
            return "You already picked your quirk you retarded";
        }

        if (choice == 1) {
            quirks = new String[1];
            quirks[0] = possibleQuirks[0];
            pickedQuirk = true;
            return String.format("Chose quirks %s.\n", quirks[0]);
        } else if (choice == 2) {
            quirks = new String[2];
            quirks[0] = possibleQuirks[1];
            quirks[1] = possibleQuirks[2];
            pickedQuirk = true;
            return String.format("Chose quirks %s and %s.\n", quirks[0], quirks[1]);
        }

        return "Please enter 1 or 2 to choose your quirk";
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }
}
