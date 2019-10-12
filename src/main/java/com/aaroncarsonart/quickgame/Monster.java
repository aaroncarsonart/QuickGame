package com.aaroncarsonart.quickgame;

import imbroglio.Graph;
import imbroglio.Position2D;

import java.util.List;

public class Monster {

    private char sprite;
    private Position2D position;
    private int health;
    private int maxHealth;
    private int level;
    private List<Position2D> movementPath;

    public Monster(Position2D position, int index) {
        this.position = position;

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        this.sprite = chars.charAt(index);
        this.health = 10 + index * 10;
        this.maxHealth = health;

        this.level = index;
    }

    public char getSprite() {
        return sprite;
    }

    public void setSprite(char sprite) {
        this.sprite = sprite;
    }

    public Position2D getPosition() {
        return position;
    }

    public void setPosition(Position2D position) {
        this.position = position;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public List<Position2D> getMovementPath() {
        return movementPath;
    }

    public void setMovementPath(List<Position2D> movementPath) {
        this.movementPath = movementPath;
    }

    public int getLevel() {
        return level;
    }
}
