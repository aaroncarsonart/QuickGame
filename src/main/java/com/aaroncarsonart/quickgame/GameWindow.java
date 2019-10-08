package com.aaroncarsonart.quickgame;

import imbroglio.Maze;
import imbroglio.Position2D;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWindow {

    private Random random = new Random();

    private JFrame jFrame;
    private Container container;
    private JTextArea[][] textGrid;

    private int width;
    private int height;
    private char[][] gameMap;
    private boolean[][] update;

    private Font font;

    private Position2D playerPos;
    private boolean gameOver = false;
    private PlayerAction playerAction;

    public GameWindow() {
        initGameWindow();
    }

    public void initGameWindow() {
        jFrame = new JFrame("Hello, RogueLike!");
        container = jFrame.getContentPane();
        font = new Font("CourierNew", Font.PLAIN, 18);

        width = 60;
        height = 20;

        update = new boolean[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                update[y][x] = true;
            }
        }

        container.setLayout(new GridLayout(height, width));
        textGrid = new JTextArea[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                JTextArea textArea = new JTextArea();
                textArea.setFont(font);
                textArea.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
                textArea.setAlignmentY(JTextArea.CENTER_ALIGNMENT);
                textArea.setEditable(false);
                textGrid[y][x] = textArea;
                container.add(textArea);
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
        // draw map
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (update[y][x]) {
                    update[y][x] = false;
                    char b = gameMap[y][x];
                    String symbol = "" + b;
                    JTextArea textArea = textGrid[y][x];
                    textArea.setText(symbol);
                    if (symbol.equals("#")) {
                        textArea.setBackground(Color.BLACK);
                        textArea.setForeground(Color.RED);
                    } else {
                        textArea.setBackground(Color.BLACK);
                        textArea.setForeground(Color.DARK_GRAY);
                    }
                }
            }
        }
        // draw playerPos
        JTextArea playerTextArea = textGrid[playerPos.y()][playerPos.x()];
        playerTextArea.setText("@");
        playerTextArea.setBackground(Color.BLACK);
        playerTextArea.setForeground(Color.WHITE);
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
        Maze maze = Maze.generateCellularAutomataRoom(width, height);
        for (int i = 0; i < 3; i++) {
            maze.cellularAutomataIteration();
            maze.connectDisconnectedComponents();
        }

        // generate map from maze
        gameMap = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                byte b = maze.getCell(x, y);
                char c = b == Maze.WALL ? '#' : '.';
                gameMap[y][x] = c;
            }
        }

        // init playerPos state
        List<Position2D> openPaths = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (gameMap[y][x] == '.') {
                    Position2D openPos = new Position2D(x, y);
                    openPaths.add(openPos);
                }
            }
        }

        playerPos = openPaths.remove(0);
    }

    /**
     * Begin a new Game.
     */
    public void startGame() {
        generateGameData();
        drawMapUpdates();
        display();
    }

    private int iterations = 0;

    public void gameLoop() {
        respondToInputs();
        updateGameState();
        drawMapUpdates();
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
            System.out.println("player position: "+ playerPos);
        }
    }

    public boolean withinBounds(Position2D p) {
        return p.x() >= 0 && p.x() < width && p.y() >= 0 && p.y() < height;
    }

    public boolean occupied(Position2D p) {
        char c = gameMap[p.y()][p.x()];
        return c != '.';
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
