package com.aaroncarsonart.quickgame;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.LayoutManager;

public class TilePanel {

    private JLabel label;
    private JPanel panel;

    public TilePanel() {
        label = new JLabel(" ");
        label.setFont(Game.FONT);

        panel = new JPanel();
        panel.add(label);

        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
    }

    public void setBackground(Color color) {
        panel.setBackground(color);
    }

    public void setForeground(Color color) {
        label.setForeground(color);
    }

    public void setChar(char c) {
        label.setText(String.valueOf(c));
    }

    public void setText(String s) {
        label.setText(s);
    }

    public JComponent getJPanel() {
        return label;
    }
}
