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
import java.util.List;
import java.util.Random;

public class Game {
    public static final Font FONT = new Font("Courier", Font.PLAIN, 18);

    public static final Color BROWN = new Color(165, 42, 42);
    public static final Color ORANGE = new Color(255, 165, 0);

    private static final Random RNG = new Random();

    private static final int WINDOW_WIDTH = 80;
    private static final int WINDOW_HEIGHT = 30;
    private static final int STATUS_HEIGHT = 5;

    private JFrame jFrame;
    private Container container;
    private TilePanel[][] textGrid;

    private int width;
    private int height;
    private int mapWidth;
    private int mapHeight;
    private char[][] gameMap;
    private boolean[][] update;

    private Position2D playerPos;
    private boolean gameOver = false;
    private PlayerAction playerAction;

    int health = 100;
    int maxHealth = 100;
    int stamina = 42;
    int maxStamina = 42;
    int experience = 0;
    int nextLevelExperience = 10;
    int level = 1;
    int treasure = 0;


    public Game() {
        initGameWindow();
    }

    public void initGameWindow() {
        jFrame = new JFrame("Hello, RogueLike!");
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
        jFrame.addKeyListener(createKeyListener());
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

    public void drawMapUpdates() {
        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                if (update[y][x]) {
                    update[y][x] = false;
                    char c = gameMap[y][x];
                    String symbol = "" + c;
                    TilePanel textArea = textGrid[y][x];
                    textArea.setChar(c);
                    if (c == '#') {
                        textArea.setBackground(Color.BLACK);
                        textArea.setForeground(Color.RED);
                    } else if (c == '"') {
                        textArea.setBackground(Color.BLACK);
                        textArea.setForeground(Color.GREEN);
                    } else if (c == '$') {
                        textArea.setBackground(Color.BLACK);
                        textArea.setForeground(Color.YELLOW);
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

        text = "Stamina: ";
        drawTextLeft(text, Color.WHITE, sy, cursor);

        cursor += text.length();
        text = String.valueOf(stamina);
        drawTextLeft(text, Color.GREEN, sy, cursor);

        cursor += text.length();
        text = "/";
        drawTextLeft(text,  Color.WHITE, sy, cursor);

        cursor += text.length();
        text = String.valueOf(maxStamina);
        drawTextLeft(text, Color.GREEN, sy, cursor);

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

    public void drawText(String text, int y, int x) {
        for (int i = 0; i < text.length(); i++) {
            TilePanel playerTextArea = textGrid[y][x + i];
            playerTextArea.setText(String.valueOf(text.charAt(i)));
            playerTextArea.setBackground(Color.BLACK);
            playerTextArea.setForeground(Color.WHITE);
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

        // add items
        int foodCount = RNG.nextInt(10) + 10;
        for (int i = 0; i < foodCount; i++) {
            Position2D foodPos = openPaths.remove(RNG.nextInt(openPaths.size()));
            gameMap[foodPos.y()][foodPos.x()] = '"';
        }

        // add treasure
        int treasureCount = RNG.nextInt(5) + 5;
        for (int i = 0; i < treasureCount; i++) {
            Position2D treasurePos = openPaths.remove(RNG.nextInt(openPaths.size()));
            gameMap[treasurePos.y()][treasurePos.x()] = '$';
        }

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
        updateGameState();
        drawMapUpdates();
        drawStats();
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
        Position2D newPos;
        switch(playerAction) {
            case UP:    newPos = playerPos.above(); break;
            case DOWN:  newPos = playerPos.below(); break;
            case LEFT:  newPos = playerPos.left();  break;
            case RIGHT: newPos = playerPos.right(); break;
            default:    newPos = playerPos;         break;
        }

        System.out.printf("oldPos: %s newPos: %s\n", playerPos, newPos);

        if (withinBounds(newPos) && !occupied(newPos)) {
            update[playerPos.y()][playerPos.x()] = true;
            playerPos = newPos;
            stamina -= 1;

            // ------------------------------------------------
            // check for items to collect
            // ------------------------------------------------

            // found food
            if (gameMap[newPos.y()][newPos.x()] == '"') {
                gameMap[newPos.y()][newPos.x()] = '.';
                stamina += 25;
                experience += 1;
            }

            // found treasure
            if (gameMap[newPos.y()][newPos.x()] == '$') {
                gameMap[newPos.y()][newPos.x()] = '.';
                treasure += 10;
                experience += 2;
            }
        }

        // check for level-up
        if (experience >= nextLevelExperience) {
            nextLevelExperience += (int) (nextLevelExperience * 1.5);
            level += 1;

            int healthGained = 10 + level + RNG.nextInt(10 + level);
            health += healthGained;
            maxHealth += healthGained;

            int staminaGained = 5 + level + RNG.nextInt(5 + level);
            health += staminaGained;
            maxHealth += staminaGained;
        }
    }



    public boolean withinBounds(Position2D p) {
        return p.x() >= 0 && p.x() < mapWidth && p.y() >= 0 && p.y() < mapHeight;
    }

    public boolean occupied(Position2D p) {
        char c = gameMap[p.y()][p.x()];
        return c == '#';
    }

    public void updateGameState() {
    }

    public void sleep() {
        try {
            Thread.sleep(30L);
        } catch (Exception e) {
        }
    }

}
