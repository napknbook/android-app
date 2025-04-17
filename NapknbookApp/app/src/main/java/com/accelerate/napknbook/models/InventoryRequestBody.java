package com.accelerate.napknbook.models;

public class InventoryRequestBody {
    private String name;
    private int level;



    private String characterPk ;

    public InventoryRequestBody(String name, int level, String characterPk) {
        this.name = name;
        this.level = level;
        this.characterPk = characterPk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCharacterPk() {
        return characterPk;
    }

    public void setCharacterPk(String characterPk) {
        this.characterPk = characterPk;
    }


    // Getters and Setters
}
