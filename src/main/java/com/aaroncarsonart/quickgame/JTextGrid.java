package com.aaroncarsonart.quickgame;

import imbroglio.Maze;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.KeyListener;

public class JTextGrid {

    public int width;
    public int height;

    private JFrame jFrame;
    private Container container;
    private TilePanel[][] textGrid;

    public JTextGrid(int height, int width) {
        jFrame = new JFrame("QuickGame");
        container = jFrame.getContentPane();
        container.setBackground(Color.BLACK);
        container.setLayout(new GridLayout(height, width));

        textGrid = new TilePanel[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                TilePanel textArea = new TilePanel();
                textArea.setChar(' ');
                textArea.setForeground(Color.BLACK);
                textArea.setForeground(Color.DARK_GRAY);
                textGrid[y][x] = textArea;
                container.add(textArea.getJPanel());
            }
        }
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setBackground(Color color, int y, int x) {
        TilePanel panel = textGrid[y][x];
        panel.setBackground(color);
    }

    public void setForeground(Color color, int y, int x) {
        TilePanel panel = textGrid[y][x];
        panel.setForeground(color);
    }

    public void setChar(char c, int y, int x) {
        TilePanel panel = textGrid[y][x];
        panel.setChar(c);
    }

    public void setText(String s, int y, int x) {
        TilePanel panel = textGrid[y][x];
        panel.setText(s);
    }



    public void drawTextLeft(String text, Color fgColor, int y, int x) {
        for (int i = 0; i < text.length(); i++) {
            TilePanel playerTextArea = textGrid[y][x + i];
            playerTextArea.setText(String.valueOf(text.charAt(i)));
            playerTextArea.setForeground(fgColor);
        }
    }

    public JFrame getJFrame() {
        return jFrame;
    }

    public void show() {
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.setVisible(true);
        jFrame.requestFocusInWindow();
    }

    public static void main(String[] args) {
        JTextGrid tileGrid = new JTextGrid(30, 80);

        int width = 10;
        int height = 10;
        Maze maze = Maze.generateCellularAutomataRoom(5, 5);
        for (int i = 0; i < 3; i++) {
            maze.cellularAutomataIteration();
            maze.connectDisconnectedComponents();
        }


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char c = maze.getCell(x, y) == Maze.WALL? '#' : '.';
                tileGrid.setChar(c, y, x);
            }
        }

        tileGrid.show();
    }
}
