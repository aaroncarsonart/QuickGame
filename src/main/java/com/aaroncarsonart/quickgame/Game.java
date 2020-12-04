package com.aaroncarsonart.quickgame;

import com.aaroncarsonart.imbroglio.Maze;
import com.aaroncarsonart.imbroglio.Position2D;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

/**
 * A quickly developed game.  Cutting corners in code design for the sake of
 * programming simplicity and rapid prototyping game development.
 *
 * If the basic idea works and is playable, I may extend this into a larger
 * project at a later time.  But for now, quick and dirty coding it is.
 * The goal is results, not fancily architectured code.
 */
public class Game {
    public static final Font FONT = new Font("Courier", Font.PLAIN, 18);
    private static final int WINDOW_WIDTH = 80;
    private static final int WINDOW_HEIGHT = 30;
    private static final int STATUS_HEIGHT = 5;

    public static final Color BROWN = new Color(165, 82, 0);
    public static final Color DARK_BROWN = new Color(50, 15, 0);
    public static final Color DARK_CYAN = new Color(0, 30, 30);
    public static final Color ORANGE = new Color(255, 165, 0);
    public static final Color DARK_RED = new Color(45, 0, 0);
    public static final Color DARK_GREEN = new Color(0, 30, 0);
    public static final Color DARK_YELLOW = new Color(30, 30, 0);


    private static final Random RNG = new Random();

    private int width;
    private int height;
    private int mapWidth;
    private int mapHeight;

    private char[][] gameMap;
    private int mapLevel = 0;

    private Monster[][] monsterMap;
    private List<Monster> monsterList;
    private int monsterChanceCounter = 0;

    private Set<Position2D> treasures;

    private Position2D playerPos;
    private boolean gameOver = false;
    private PlayerAction playerAction;

    private int health = 10;
    private int maxHealth = 10;
    private int energy = 50;
    private int maxStamina = 50;
    private int experience = 0;
    private int nextLevelExperience = 10;
    private int level = 1;
    private int treasure = 0;

    private JTextGrid textGrid;
    private KeyListener keyListener;
    boolean skipRepaint;

    public Game() {
        initGameWindow();
    }

    public void initGameWindow() {

        width = WINDOW_WIDTH;
        height = WINDOW_HEIGHT;
        mapHeight = height - STATUS_HEIGHT;
        mapWidth = width;

        textGrid = new JTextGrid(height, width);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Position2D p = new Position2D(x, y);
                TilePanel tilePanel = textGrid.getTilePanel(y, x);
//                tilePanel.getJPanel().addMouseListener(new MouseListener() {
//                    public void mouseClicked(MouseEvent e) {
//                    }
//                    public void mousePressed(MouseEvent e) {
//                        Monster monster = monsterMap[p.y()][p.x()];
//                        if (monster != null) {
//                            String text = "Monster has " + monster.getHealth() + " health left.";
//                            int ty = mapHeight + 4;
//                            int tx = 20;
//                            drawTextLeft(text, Color.WHITE, ty, tx);
//                            for (int k = text.length(); k < 30; k++) {
//                                textGrid.setChar(' ', ty, tx + k);
//                            }
//                            textGrid.getJFrame().getContentPane().repaint();
//                        }
//                    }
//                    public void mouseReleased(MouseEvent e) {
//                    }
//                    public void mouseEntered(MouseEvent e) {
//                        Color fg = tilePanel.getFgColor();
//                        Color bg = tilePanel.getBgColor();
//                        tilePanel.setBackground(fg);
//                        tilePanel.setForeground(bg);
//
////                        String text = p.toString();
////                        int ty = mapHeight + 4;
////                        int tx = 20;
////                        drawTextLeft(text, Color.WHITE, ty, tx);
////                        for (int k = text.length(); k < 30; k++) {
////                            textGrid.setChar(' ', ty, tx + k);
////                        }
////                        textGrid.getJFrame().getContentPane().repaint();
//                    }
//                    public void mouseExited(MouseEvent e) {
//                        if (skipRepaint) {
//                            skipRepaint = false;
//                        } else {
//                            Color fg = tilePanel.getFgColor();
//                            Color bg = tilePanel.getBgColor();
//                            tilePanel.setBackground(fg);
//                            tilePanel.setForeground(bg);
//
//                            int ty = mapHeight + 4;
//                            int tx = 20;
//                            for (int k = 0; k < 30; k++) {
//                                textGrid.setChar(' ', ty, tx + k);
//                            }
//                            textGrid.getJFrame().getContentPane().repaint();
//                        }
//                    }
//                });
            }
        }

        JFrame jFrame = textGrid.getJFrame();
        keyListener = createKeyListener();
        jFrame.addKeyListener(keyListener);
    }

    public enum PlayerAction {
        UP, DOWN, LEFT, RIGHT, OK, CANCEL, UNKNOWN;
        boolean pressed = false;
        boolean consumed = false;

        void press() {
            if (!consumed) {
                pressed = true;
            }
        }

        void release() {
            pressed = false;
            consumed = false;
        }

        void consume() {
            consumed = true;
        }
    }

    /**
     * Draw all map updates to the screen.
     */
    public void drawMapUpdates() {
        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                renderGameTile(y, x);
            }
        }
        // draw playerPos
        textGrid.setText("@", playerPos.y(), playerPos.x());
        //textGrid.setBackground(Color.BLACK, playerPos.y(), playerPos.x());
        textGrid.setForeground(Color.WHITE, playerPos.y(), playerPos.x());

        // draw Monsters
        for (Monster monster : monsterList) {
            Position2D pos = monster.getPosition();
            char sprite = monster.getSprite();
            textGrid.setChar(sprite, pos.y(), pos.x());
            textGrid.setForeground(Color.RED, pos.y(), pos.x());
        }

//        // drawPathFinding
//        Position2D finish = new Position2D(mapWidth -1, mapHeight -1);
//        List<Position2D> path = pathfindBFS(playerPos, test, 0);
//        for (Position2D pos : path) {
//            TilePanel tile = textGrid[pos.y()][pos.x()];
//            Color fg = tile.getFgColor();
//            Color bg = tile.getBgColor();
//            tile.setBackground(fg);
//            tile.setForeground(bg);
//        }

//        for (Monster monster : monsterList) {
//            List<Position2D> path = monster.getMovementPath();
//            if (path == null) {
//                continue;
//            }
//            for (Position2D pos : path) {
//                TilePanel tile = textGrid[pos.y()][pos.x()];
//                Color fg = tile.getFgColor();
//                Color bg = tile.getBgColor();
//                tile.setBackground(fg);
//                tile.setForeground(bg);
//            }
//
//        }
    }

    public void renderGameTile(int y, int x) {
        char c = gameMap[y][x];
        textGrid.setChar(c, y, x);
        if (c == '#') {
            textGrid.setBackground(DARK_BROWN, y, x);
            textGrid.setForeground(BROWN, y, x);
        } else if (c == '"') {
            textGrid.setBackground(Color.BLACK, y, x);
            textGrid.setForeground(Color.GREEN, y, x);
        } else if (c == '$') {
            textGrid.setBackground(Color.BLACK, y, x);
            textGrid.setForeground(Color.YELLOW, y, x);
        } else if (c == 'M') {
            textGrid.setBackground(Color.BLACK, y, x);
            textGrid.setForeground(Color.RED, y, x);
        } else if (c == '~') {
            textGrid.setBackground(DARK_CYAN, y, x);
            textGrid.setForeground(Color.CYAN, y, x);
        } else if (c == '!') {
            textGrid.setBackground(Color.BLACK, y, x);
            textGrid.setForeground(Color.WHITE, y, x);
        } else {
            textGrid.setBackground(Color.BLACK, y, x);
            textGrid.setForeground(Color.DARK_GRAY, y, x);
        }
    }

    public void drawStats() {
        // --------------------------------------------
        // Draw Health
        // --------------------------------------------
        int cursor = 0;
        int sy = mapHeight;

        String text = "Health: ";
        drawTextLeft(text, Color.WHITE, sy, cursor);

        cursor += text.length();
        text = String.valueOf(health);
        drawTextLeft(text, Color.RED, sy, cursor);

        cursor += text.length();
        text = "/";
        drawTextLeft(text,  Color.WHITE, sy, cursor);

        cursor += text.length();
        text = String.valueOf(maxHealth);
        drawTextLeft(text, Color.RED, sy, cursor);

        cursor += text.length();
        while (cursor < 20) {
            drawTextLeft(" ", Color.WHITE, sy, cursor++);
        }
        // --------------------------------------------
        // draw Stamina stat
        // --------------------------------------------
        cursor += text.length();
        cursor += 1;
        cursor = 0;
        sy += 1;

        text = "Energy: ";
        drawTextLeft(text, Color.WHITE, sy, cursor);

        cursor += text.length();
        text = String.valueOf(energy);
        drawTextLeft(text, Color.GREEN, sy, cursor);

//        cursor += text.length();
//        text = "/";
//        drawTextLeft(text,  Color.WHITE, sy, cursor);
//
//        cursor += text.length();
//        text = String.valueOf(maxStamina);
//        drawTextLeft(text, Color.GREEN, sy, cursor);

        cursor += text.length();
        while (cursor < 20) {
            drawTextLeft(" ", Color.WHITE, sy, cursor++);
        }
        // --------------------------------------------
        // draw Experience stat
        // --------------------------------------------
        cursor += text.length();
        cursor += 1;
        cursor = 0;
        sy += 1;

        text = "Exp: ";
        drawTextLeft(text, Color.WHITE, sy, cursor);

        cursor += text.length();
        text = String.valueOf(experience);
        drawTextLeft(text, Color.CYAN, sy, cursor);

        cursor += text.length();
        text = "/";
        drawTextLeft(text,  Color.WHITE, sy, cursor);

        cursor += text.length();
        text = String.valueOf(nextLevelExperience);
        drawTextLeft(text, Color.CYAN, sy, cursor);

        cursor += text.length();
        while (cursor < 20) {
            drawTextLeft(" ", Color.WHITE, sy, cursor++);
        }

        // --------------------------------------------
        // draw Level stat
        // --------------------------------------------
        cursor += 1;
        cursor = 0;
        sy += 1;

        text = "Lv: ";
        drawTextLeft(text, Color.WHITE, sy, cursor);

        cursor += text.length();
        text = String.valueOf(level);
        drawTextLeft(text, Color.MAGENTA, sy, cursor);

        cursor += text.length();
        while (cursor < 20) {
            drawTextLeft(" ", Color.WHITE, sy, cursor++);
        }
        // --------------------------------------------
        // draw Treasure stat
        // --------------------------------------------
        cursor += 1;
        cursor = 0;
        sy += 1;

        text = "Treasure: ";
        drawTextLeft(text, Color.WHITE, sy, cursor);

        cursor += text.length();
        text = String.valueOf(treasure);
        drawTextLeft(text, Color.YELLOW, sy, cursor);

        cursor += text.length();
        while (cursor < 20) {
            drawTextLeft(" ", Color.WHITE, sy, cursor++);
        }

        // --------------------------------------------
        // draw Map Depth stat
        // --------------------------------------------
        cursor += 1;
        cursor = width - 13;

        text = "Map Depth: ";
        drawTextLeft(text, Color.WHITE, sy, cursor);

        cursor += text.length();
        text = String.valueOf(mapLevel);
        drawTextLeft(text, Color.BLUE, sy, cursor);

        cursor += text.length();
        while (cursor < 2) {
            drawTextLeft(" ", Color.WHITE, sy, cursor++);
        }
    }

    public void drawTextLeft(String text, Color fgColor, int y, int x) {
        for (int i = 0; i < text.length(); i++) {
            textGrid.setChar(text.charAt(i), y, x + i);
            textGrid.setForeground(fgColor, y, x + i);
        }
    }

    public void drawTextRight(String text, Color fgColor, int y, int x) {
        for (int i = 0; i < text.length(); i++) {
            textGrid.setChar(text.charAt(text.length() - 1 - i), y, x - i);
            textGrid.setForeground(fgColor, y, x - i);
        }
    }

    /**
     * Display the JFrame of this game.
     */
    public void display() {
        textGrid.show();
    }

    public void generateGameData() {
        monsterChanceCounter = 0;
        mapLevel ++;

        // generate maze
        Maze maze = Maze.generateCellularAutomataRoom(mapWidth, mapHeight);
        for (int i = 0; i < 3; i++) {
            maze.cellularAutomataIteration();
            maze.connectDisconnectedComponents();
        }
//        maze = Maze.generateRandomWalledMaze(mapWidth, mapHeight);
//        maze.setDifficulty(Difficulty.EASY);

        // generate map from maze
        gameMap = new char[mapHeight][mapWidth];
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                byte b = maze.getCell(x, y);
                char c = b == Maze.WALL ? '#' : '.';
                gameMap[y][x] = c;
            }
        }

        // init playerPos state
        List<Position2D> openPaths = findOpenPaths();

        playerPos = openPaths.remove(0);

        // ------------------------------------------------------
        // add food
        // ------------------------------------------------------

        int foodCount = RNG.nextInt(20) + 20;
        for (int i = 0; i < foodCount; i++) {
            if (openPaths.isEmpty()) {
                break;
            }
            Position2D foodPos = openPaths.remove(RNG.nextInt(openPaths.size()));
            gameMap[foodPos.y()][foodPos.x()] = '"';
        }

        char tile = '"';
        int count = 5;
        int maxNeighbors = Math.max(0, 20 - mapLevel) + RNG.nextInt(Math.min(1, 20 - mapLevel));
        if (maxNeighbors > 0) {
            addBlobsOfTiles(tile, openPaths, count, maxNeighbors);
        }

        // ------------------------------------------------------
        // add water
        // ------------------------------------------------------

//        int waterCount = RNG.nextInt(60) + 60;
//        for (int i = 0; i < waterCount; i++) {
//            if (openPaths.isEmpty()) {
//                break;
//            }
//            Position2D waterPos = openPaths.remove(RNG.nextInt(openPaths.size()));
//            gameMap[waterPos.y()][waterPos.x()] = '~';
//        }

        tile = '~';
        count = 5;
        maxNeighbors = Math.max(1, 10 - mapLevel) + RNG.nextInt(Math.max(1, 10 - mapLevel));
        if (maxNeighbors > 0) {
            addBlobsOfTiles(tile, openPaths, count, maxNeighbors);
        }
        tile = '~';
        count = Math.max( 1, 10 - mapLevel);
        maxNeighbors = 3 + RNG.nextInt(3);
        addBlobsOfTiles(tile, openPaths, count, maxNeighbors);

        // ------------------------------------------------------
        // add treasure
        // ------------------------------------------------------
        treasures = new HashSet<>();
        int treasureCount = RNG.nextInt(5 + mapLevel) + 5 + mapLevel;
        for (int i = 0; i < treasureCount; i++) {
            if (openPaths.isEmpty()) {
                break;
            }
            Position2D treasurePos = openPaths.remove(RNG.nextInt(openPaths.size()));
            gameMap[treasurePos.y()][treasurePos.x()] = '$';
            treasures.add(treasurePos);
        }

        tile = '$';
        count = mapLevel;
        maxNeighbors = 5 + RNG.nextInt(5);
        boolean useTreasure = true;
        addBlobsOfTiles(tile, openPaths, count, maxNeighbors, useTreasure);

        // ------------------------------------------------------
        // add monsters
        // ------------------------------------------------------
        monsterMap = new Monster[mapHeight][mapWidth];
        monsterList = new ArrayList<>();

//        int monsterCount = 1;
        int monsterCount = RNG.nextInt(5 + mapLevel * 2) + 5 + mapLevel * 2;
        for (int i = 0; i < monsterCount; i++) {
            if (openPaths.isEmpty()) {
                break;
            }
            Position2D monsterPos = openPaths.remove(RNG.nextInt(openPaths.size()));
            //gameMap[monsterPos.y()][monsterPos.x()] = 'M';

            // TODO: implement A-Z monsters
//            int index = RNG.nextInt(Math.min(mapLevel + 1, 27));
            int monsterLevel = Math.min(RNG.nextInt(Math.max(mapLevel, level)), 27);
            Monster monster = new Monster(monsterPos, monsterLevel);
            monsterMap[monsterPos.y()][monsterPos.x()] = monster;
            monsterList.add(monster);

            List<Position2D> path = pathfindBFS(monsterPos, playerPos, 50);
            monster.setMovementPath(path);
        }

        // ------------------------------------------------------
        // add BANG!
        // ------------------------------------------------------

        for (int i = 0; i < mapLevel; i++) {
            if (!openPaths.isEmpty()) {
                Position2D pos = openPaths.remove(RNG.nextInt(openPaths.size()));
                gameMap[pos.y()][pos.x()] = '!';
            }
        }

//        // count treasures
//        int tc1 = treasures.size();
//        int tc2 = 0;
//        for (int x = 0; x < mapWidth; x++) {
//            for (int y = 0; y < mapHeight; y++) {
//                if (gameMap[y][x] == '$') {
//                    tc2++;
//                }
//            }
//        }
//        System.out.println("tc1: " + tc1 + " tc2:" + tc2);
    }

    public void addBlobsOfTiles(char tile, List<Position2D> openPaths, int count, int maxNeighbors) {
        addBlobsOfTiles(tile, openPaths, count, maxNeighbors, false);
    }

    public void addBlobsOfTiles(char tile, List<Position2D> openPaths, int count, int maxNeighbors, boolean treasures) {
        for (int i = 0; i < count; i++) {
            if (openPaths.isEmpty()) {
                break;
            }
            Position2D pos = openPaths.remove(RNG.nextInt(openPaths.size()));

            Stack<Position2D> positions = new Stack<>();
            positions.add(pos);

            for (int j = 0; j < maxNeighbors; j++) {
                Position2D next = positions.peek();
                List<Position2D> neighbors = next.getNeighbors();
                for (int k = 0; k < neighbors.size(); k++) {
                    Position2D neighbor = neighbors.get(k);
                    if (positions.contains(neighbor) || !withinBounds(neighbor)) {
                        neighbors.remove(neighbor);
                    }
                }
                if (neighbors.isEmpty()) {
                    continue;
                }
                Position2D nextNeighbor = neighbors.get(RNG.nextInt(neighbors.size()));
                positions.add(nextNeighbor);
            }

            for (Position2D position : positions) {
                if (withinBounds(position)) {
                    openPaths.remove(position);
                    gameMap[position.y()][position.x()] = tile;
                    if (treasures) {
                        this.treasures.add(position);
                    }

                }
            }
        }

    }


    /**
     * Run a BFS bathfinding algorithm between two positions on the gameMap.
     * @param start The starting position.
     * @param finish The ending position.
     * @param maxIterations The max number of iterations.
     *                      If zero, then ignore this parameter.
     * @return The path between the target and
     */
    public List<Position2D> pathfindBFS(Position2D start, Position2D finish, int maxIterations) {
        Queue<List<Position2D>> queue = new LinkedList<>();
        int iterations = 0;

        List<Position2D> visited = new ArrayList<>();
        List<Position2D> initial = new ArrayList<>();
        initial.add(start);
        visited.add(start);
        queue.add(initial);

        while (!queue.isEmpty()) {
            if (maxIterations != 0 && iterations > maxIterations) {
                return new ArrayList<>();
            }
            iterations ++;

            List<Position2D> next = queue.remove();
            Position2D head = next.get(0);
            if (head.equals(finish)) {
                next.remove(next.size() - 1);
                return next;
            }

            for (Position2D neighbor : head.getNeighbors()) {
                if (!visited.contains(neighbor)
                        && withinBounds(neighbor)
                        && !occupied(neighbor)) {
                    visited.add(neighbor);
                    List<Position2D> copy = new ArrayList<>(next);
                    copy.add(0, neighbor);
                    queue.add(copy);
                }
            }
        }
        return new ArrayList<>();
    }


    /**
     * Begin a new Game.
     */
    public void startGame() {
        generateGameData();
        drawMapUpdates();
//        drawMapUpdatesCentered();
        drawStats();
        display();
    }

    private int iterations = 0;

    public void gameLoop() {
        respondToInputs();
        checkForNextMapLevel();
        drawMapUpdates();
//        drawMapUpdatesCentered();
        drawStats();
        textGrid.getJFrame().getContentPane().repaint();
        skipRepaint = true;
        System.out.println("iteration " + ++iterations);
    }

    public void checkForNextMapLevel() {
        if (treasures.isEmpty()) {
            generateGameData();
        }
    }

    /**
     * @return The KeyListener that handles raw KeyEvents.
     */
    public KeyListener createKeyListener() {
        return new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        PlayerAction.UP.press();
                        playerAction = PlayerAction.UP;
                        break;
                    case KeyEvent.VK_DOWN:
                        PlayerAction.DOWN.press();
                        playerAction = PlayerAction.DOWN;
                        break;
                    case KeyEvent.VK_LEFT:
                        PlayerAction.LEFT.press();
                        playerAction = PlayerAction.LEFT;
                        break;
                    case KeyEvent.VK_RIGHT:
                        PlayerAction.RIGHT.press();
                        playerAction = PlayerAction.RIGHT;
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_Z:
                        PlayerAction.OK.press();
                        playerAction = PlayerAction.OK;
                        break;
                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_X:
                        PlayerAction.CANCEL.press();
                        playerAction = PlayerAction.CANCEL;
                        break;
                    default:
                        playerAction = PlayerAction.UNKNOWN;
                        break;
                }
                gameLoop();
            }
        };
    }

    /**
     * Move the player.
     */
    public void respondToInputs() {
        movePlayer();
        checkForLevelUp();
        moveMonsters();
        spawnMonsters();
    }

    public void movePlayer() {
        Position2D newPos;
        switch(playerAction) {
            case UP:    newPos = playerPos.above(); break;
            case DOWN:  newPos = playerPos.below(); break;
            case LEFT:  newPos = playerPos.left();  break;
            case RIGHT: newPos = playerPos.right(); break;
            default:    newPos = playerPos;         break;
        }

        if (newPos != playerPos && withinBounds(newPos) && !occupied(newPos)) {
            playerPos = newPos;
            if (gameMap[newPos.y()][newPos.x()] == '~') {
                energy -= 3;
            } else {
                energy -= 1;
            }

            if (energy <= 0) {
                gameOver("Ran out of energy.");
            }


            // ------------------------------------------------
            // check for items to collect
            // ------------------------------------------------

            // found food
            if (gameMap[newPos.y()][newPos.x()] == '"') {
                gameMap[newPos.y()][newPos.x()] = '.';
                energy += 6;
            }

            // found treasure
            if (gameMap[newPos.y()][newPos.x()] == '$') {
                gameMap[newPos.y()][newPos.x()] = '.';
                treasure += 25;
                experience += 1;
                treasures.remove(newPos);
            }

            // found water
            if (gameMap[newPos.y()][newPos.x()] == '~') {
                if (health < maxHealth) {
                    gameMap[newPos.y()][newPos.x()] = '.';
                    health += 1 + RNG.nextInt(level);
                    if (health > maxHealth) {
                        health = maxHealth;
                    }
                }
            }

            // found bang
            if (gameMap[newPos.y()][newPos.x()] == '!') {
                gameMap[newPos.y()][newPos.x()] = '.';
                List<Monster> monstersToRemove = new ArrayList<>();
                for (int i = 0; i < monsterList.size(); i++) {
                    Monster monster = monsterList.get(i);
                    int damage = 2 * level + RNG.nextInt(5 * level) + RNG.nextInt(5 * level);
                    monster.setHealth(monster.getHealth() - damage);

                    // check if monster killed
                    if (monster.getHealth() <= 0) {
                        monstersToRemove.add(monster);
                        Position2D monsterPos = monster.getPosition();
                        monsterMap[monsterPos.y()][monsterPos.x()] = null;
                        //gameMap[monsterPos.y()][monsterPos.x()] = '.';
                        experience += monster.getMaxHealth();
                    }
                }
                monsterList.removeAll(monstersToRemove);
            }
        } else if (withinBounds(newPos) && monsterMap[newPos.y()][newPos.x()] != null) {
            Monster monster = monsterMap[newPos.y()][newPos.x()];
            int damage = level * level + RNG.nextInt(1 + level) + RNG.nextInt(1 + level);
            monster.setHealth(monster.getHealth() - damage);

            // check if monster killed
            if (monster.getHealth() <= 0) {
                monsterList.remove(monster);
                Position2D monsterPos = monster.getPosition();
                monsterMap[monsterPos.y()][monsterPos.x()] = null;
                //gameMap[newPos.y()][newPos.x()] = '.';
                experience += monster.getMaxHealth();
            }
        }

    }

    public void checkForLevelUp() {
        if (experience >= nextLevelExperience) {
            nextLevelExperience += (int) (nextLevelExperience * 2.0);
            level += 1;

            int healthGained = 6 + level + RNG.nextInt(6 + level);
            health += healthGained;
            maxHealth += healthGained;

            int staminaGained = 10 + 2 * level + RNG.nextInt(10 + 2 * level);
            energy += staminaGained;
            maxStamina += staminaGained;
        }
    }

    /**
     * Calculate the absolute value of the distance between two positions.
     * @param a
     * @param b
     * @return
     */
    public double distance(Position2D a, Position2D b) {
        return Math.sqrt((a.x() - b.x()) * (a.x() - b.x()) + (a.y() - b.y()) * (a.y() - b.y()));
    }

    public void moveMonsters() {
        for (Monster monster : monsterList) {
            List<Position2D> movementPath = pathfindBFS(monster.getPosition(), playerPos, 50);
            monster.setMovementPath(movementPath);
            Position2D old = monster.getPosition();
            Position2D next;

            // stay still if far away from the player
            int distance = (int) distance(old, playerPos);
            boolean stayStill = RNG.nextInt(distance) > RNG.nextInt(15);
            if (stayStill) {
                continue;
            }

            // Move randomly if path is empty
            // Or, if chance to move random rolls true
            boolean moveRandom = RNG.nextInt(100) < movementPath.size();

            if (movementPath == null || movementPath.isEmpty() || moveRandom) {
                next = getRandomNeighboringPosition(old);
            }
            // consume next step on the path
            else {
                next = movementPath.remove(movementPath.size() - 1);
                if (occupied(next, 'M')) {
                    next = old;
                    monster.setMovementPath(null);
                } else if (next.equals(playerPos)) {
                    next = old;
                    int damage = RNG.nextInt(monster.getLevel() + 1) + monster.getLevel() + 1;
                    health -= damage;
                    if (health <= 0) {
                        treasure -= Math.abs(health) * 25;
                        health = 0;
                        if (treasure <= 0) {
                            gameOver("Ran out of health and treasure.");
                        }
                    }
                }
            }

            try {
                monsterMap[old.y()][old.x()] = null;
                monsterMap[next.y()][next.x()] = monster;
//                gameMap[old.y()][old.x()] = '.';
//                gameMap[next.y()][next.x()] = 'M';
                monster.setPosition(next);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println ("Hello");
            }
        }
    }

    public Position2D getRandomNeighboringPosition(Position2D old) {

        List<Position2D> neighbors = old.getNeighbors();
        List<Position2D> neighborsToRemove = new ArrayList<>();
        for (int k = 0; k < neighbors.size(); k++) {
            Position2D neighbor = neighbors.get(k);
            if (!withinBounds(neighbor) || occupied(neighbor, "#M")) {
                neighborsToRemove.add(neighbor);
            }
        }
        neighbors.removeAll(neighborsToRemove);

        if (neighbors.isEmpty()) {
            return old;
        } else {
            return neighbors.get(RNG.nextInt(neighbors.size()));
        }
    }

    public List<Position2D> findOpenPaths() {
        List<Position2D> openPaths = new ArrayList<>();
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (gameMap[y][x] == '.') {
                    Position2D openPos = new Position2D(x, y);
                    openPaths.add(openPos);
                }
            }
        }
        return openPaths;
    }

    /**
     * calculate chances of new monsters spawning into a map, based on the player's level.
     */
    public void spawnMonsters() {
        monsterChanceCounter += 1;
        int spawnNewMonster = RNG.nextInt(monsterChanceCounter);
        if (spawnNewMonster > 15) {
            monsterChanceCounter = 0;
            List<Position2D> openPaths = findOpenPaths();
            Position2D pos = openPaths.get(RNG.nextInt(openPaths.size()));
            int monsterLevel = Math.max(level, mapLevel) - 1;

            Monster monster = new Monster(pos, monsterLevel);
            monsterList.add(monster);
            monsterMap[pos.y()][pos.x()] = monster;
        }
    }

    public boolean withinBounds(Position2D p) {
        return p.x() >= 0 && p.x() < mapWidth && p.y() >= 0 && p.y() < mapHeight;
    }

    public boolean occupied(Position2D p) {
        return occupied(p, "#");
    }

    public boolean occupied(Position2D p, String chars) {
        char c = gameMap[p.y()][p.x()];
        return chars.contains(String.valueOf(c)) || monsterMap[p.y()][p.x()] != null;
    }

    public boolean occupied(Position2D p, char sprite) {
        return gameMap[p.y()][p.x()] == sprite || monsterMap[p.y()][p.x()] != null;
    }

    public void gameOver(String message) {
        JFrame jFrame = textGrid.getJFrame();
        jFrame.removeKeyListener(keyListener);
        drawTextLeft("Game Over!  " + message, Color.WHITE, mapHeight + 2, 20);
        jFrame.getContentPane().repaint();
    }
}
