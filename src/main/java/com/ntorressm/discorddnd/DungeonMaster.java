package com.ntorressm.discorddnd;

public class DungeonMaster {
    private long id;
    private String name;

    public DungeonMaster(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }
}
