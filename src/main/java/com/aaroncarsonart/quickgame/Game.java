package com.aaroncarsonart.quickgame;

import imbroglio.Maze;
import imbroglio.Position2D;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

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

    public static final Color BROWN = new Color(165, 82, 0);
    public static final Color DARK_BROWN = new Color(50, 15, 0);
    public static final Color ORANGE = new Color(255, 165, 0);
    public static final Color DARK_RED = new Color(45, 0, 0);

    private static final Random RNG = new Random();

    private static final int WINDOW_WIDTH = 80;
    private static final int WINDOW_HEIGHT = 30;
    private static final int STATUS_HEIGHT = 5;

    private JFrame jFrame;
    private Container container;
    private KeyListener keyListener;
    private TilePanel[][] textGrid;

    private int width;
    private int height;
    private int mapWidth;
    private int mapHeight;

    private char[][] gameMap;
    private boolean[][] update;

    private Monster[][] monsterMap;
    private List<Monster> monsterList;

    private Position2D playerPos;
    private boolean gameOver = false;
    private PlayerAction playerAction;

    int health = 10;
    int maxHealth = 10;
    int energy = 50;
    int maxStamina = 50;
    int experience = 0;
    int nextLevelExperience = 10;
    int level = 1;
    int treasure = 0;


    public Game() {
        initGameWindow();
    }

    public void initGameWindow() {
        jFrame = new JFrame("QuickGame");
        container = jFrame.getContentPane();
        container.setBackground(Color.BLACK);

        width = WINDOW_WIDTH;
        height = WINDOW_HEIGHT;
        mapHeight = this.height - STATUS_HEIGHT;
        mapWidth = this.width;

        update = new boolean[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                update[y][x] = true;
            }
        }

        container.setLayout(new GridLayout(height, width));
        textGrid = new TilePanel[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                TilePanel textArea = new TilePanel();
                textGrid[y][x] = textArea;
                container.add(textArea.getJPanel());
            }
        }

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                if (true || update[y][x]) {
                    update[y][x] = false;
                    char c = gameMap[y][x];
                    String symbol = "" + c;
                    TilePanel textArea = textGrid[y][x];
                    textArea.setChar(c);
                    if (c == '#') {
//                        textArea.setBackground(DARK_RED);
//                        textArea.setForeground(Color.RED);
                        textArea.setBackground(DARK_BROWN);
                        textArea.setForeground(BROWN);
                    } else if (c == '"') {
                        textArea.setBackground(Color.BLACK);
                        textArea.setForeground(Color.GREEN);
                    } else if (c == '$') {
                        textArea.setBackground(Color.BLACK);
                        textArea.setForeground(Color.YELLOW);
                    } else if (c == 'M') {
                        textArea.setBackground(Color.BLACK);
                        textArea.setForeground(Color.RED);
                    } else if (c == '~') {
                        textArea.setBackground(Color.BLACK);
                        textArea.setForeground(Color.CYAN);
                    } else {
                        textArea.setBackground(Color.BLACK);
                        textArea.setForeground(Color.DARK_GRAY);
                    }
                }
            }
        }
        // draw playerPos
        TilePanel playerTextArea = textGrid[playerPos.y()][playerPos.x()];
        playerTextArea.setText("@");
        playerTextArea.setBackground(Color.BLACK);
        playerTextArea.setForeground(Color.WHITE);

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
    }

    public void drawTextLeft(String text, Color fgColor, int y, int x) {
        for (int i = 0; i < text.length(); i++) {
            TilePanel playerTextArea = textGrid[y][x + i];
            playerTextArea.setText(String.valueOf(text.charAt(i)));
            playerTextArea.setForeground(fgColor);
        }
    }

    public void drawTextRight(String text, Color fgColor, int y, int x) {
        for (int i = 0; i < text.length(); i++) {
            TilePanel playerTextArea = textGrid[y][x - i];
            playerTextArea.setText(String.valueOf(text.charAt(text.length() - 1 - i)));
            playerTextArea.setForeground(fgColor);
        }
    }

    /**
     * Display the JFrame of this game.
     */
    public void display() {
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.requestFocusInWindow();
    }

    public void generateGameData() {
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
        List<Position2D> openPaths = new ArrayList<>();
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (gameMap[y][x] == '.') {
                    Position2D openPos = new Position2D(x, y);
                    openPaths.add(openPos);
                }
            }
        }

        playerPos = openPaths.remove(0);

        // ------------------------------------------------------
        // add items
        // ------------------------------------------------------

        // add food
        int foodCount = RNG.nextInt(120) + 120;
        for (int i = 0; i < foodCount; i++) {
            Position2D foodPos = openPaths.remove(RNG.nextInt(openPaths.size()));
            gameMap[foodPos.y()][foodPos.x()] = '"';
        }

        // add treasure
        int treasureCount = RNG.nextInt(50) + 50;
        for (int i = 0; i < treasureCount; i++) {
            Position2D treasurePos = openPaths.remove(RNG.nextInt(openPaths.size()));
            gameMap[treasurePos.y()][treasurePos.x()] = '$';
        }

        // add monsters
        monsterMap = new Monster[mapHeight][mapWidth];
        monsterList = new ArrayList<>();

//        int monsterCount = 1;
        int monsterCount = RNG.nextInt(8) + 8;
        for (int i = 0; i < monsterCount; i++) {
            Position2D monsterPos = openPaths.remove(RNG.nextInt(openPaths.size()));
            gameMap[monsterPos.y()][monsterPos.x()] = 'M';

            Monster monster = new Monster('M', monsterPos, 10);
            monsterMap[monsterPos.y()][monsterPos.x()] = monster;
            monsterList.add(monster);

            List<Position2D> path = pathfindBFS(monsterPos, playerPos, 0);
            monster.setMovementPath(path);
        }
        test = openPaths.remove(RNG.nextInt(openPaths.size()));

        // add water
        int waterCount = RNG.nextInt(30) + 30;
        for (int i = 0; i < waterCount; i++) {
            Position2D waterPos = openPaths.remove(RNG.nextInt(openPaths.size()));
            gameMap[waterPos.y()][waterPos.x()] = '~';
        }
    }

    Position2D test;

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
        drawStats();
        display();
    }

    private int iterations = 0;

    public void gameLoop() {
        respondToInputs();
        drawMapUpdates();
        drawStats();
        jFrame.getContentPane().repaint();
        System.out.println("iteration " + ++iterations);
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
            update[playerPos.y()][playerPos.x()] = true;
            playerPos = newPos;
            energy -= 1;

            if (energy <= 0) {
                treasure -= energy * 10;
                if (treasure <= 0) {
                    gameOver("Ran out of energy and treasure.");
                } else {
                    energy = 1;
                }
            }


            // ------------------------------------------------
            // check for items to collect
            // ------------------------------------------------

            // found food
            if (gameMap[newPos.y()][newPos.x()] == '"') {
                gameMap[newPos.y()][newPos.x()] = '.';
                energy += 10;
                experience += 1;
            }

            // found treasure
            if (gameMap[newPos.y()][newPos.x()] == '$') {
                gameMap[newPos.y()][newPos.x()] = '.';
                treasure += 10;
                experience += 2;
            }

            // found water
            if (gameMap[newPos.y()][newPos.x()] == '~') {
                if (health < maxHealth) {
                    gameMap[newPos.y()][newPos.x()] = '.';
                    health += 2 + RNG.nextInt(level);
                    if (health > maxHealth) {
                        health = maxHealth;
                    }
                }
            }
        }
        else if (withinBounds(newPos) && gameMap[newPos.y()][newPos.x()] == 'M') {
            Monster monster = monsterMap[newPos.y()][newPos.x()];
            int damage = level + RNG.nextInt(2 * level);
            monster.setHealth(monster.getHealth() - damage);

            // check if monster killed
            if (monster.getHealth() <= 0) {
                monsterList.remove(monster);
                monsterMap[newPos.y()][newPos.x()] = null;
                gameMap[newPos.y()][newPos.x()] = '.';
                experience += monster.getMaxHealth();
            }
        }

    }

    public void checkForLevelUp() {
        if (experience >= nextLevelExperience) {
            nextLevelExperience += (int) (nextLevelExperience * 1.5);
            level += 1;

            int healthGained = 6 + level + RNG.nextInt(6 + level);
            health += healthGained;
            maxHealth += healthGained;

            int staminaGained = 10 + 2 * level + RNG.nextInt(10 + 2 * level);
            energy += staminaGained;
            maxStamina += staminaGained;
        }
    }

    public void moveMonsters() {
        for (Monster monster : monsterList) {
            List<Position2D> movementPath = pathfindBFS(monster.getPosition(), playerPos, 0);
            monster.setMovementPath(movementPath);
            Position2D old = monster.getPosition();
            Position2D next;

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
                    health -= 1 + RNG.nextInt(level);
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
                gameMap[old.y()][old.x()] = '.';
                gameMap[next.y()][next.x()] = 'M';
                monster.setPosition(next);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println ("Hello");
            }
        }
    }

    public Position2D getRandomNeighboringPosition(Position2D old) {
        List<Position2D> neighbors = old.getNeighbors();

        for (int i = 0; i < neighbors.size(); i++) {
            Position2D neighbor = neighbors.get(i);
            if (!withinBounds(old) || occupied(old)) {
                neighbors.remove(neighbor);
            }
        }
        if (neighbors.isEmpty()) {
            return old;
        } else {
            return neighbors.get(RNG.nextInt(neighbors.size()));
        }
    }

    public boolean withinBounds(Position2D p) {
        return p.x() >= 0 && p.x() < mapWidth && p.y() >= 0 && p.y() < mapHeight;
    }

    public boolean occupied(Position2D p) {
        char c = gameMap[p.y()][p.x()];
        return "#M".contains(String.valueOf(c));
    }

    public boolean occupied(Position2D p, char sprite) {
        return gameMap[p.y()][p.x()] == sprite;
    }

    public void gameOver(String message) {
        jFrame.removeKeyListener(keyListener);
        drawTextLeft("Game Over!  " + message, Color.WHITE, mapHeight + 2, 20);
        jFrame.getContentPane().repaint();
    }

}
