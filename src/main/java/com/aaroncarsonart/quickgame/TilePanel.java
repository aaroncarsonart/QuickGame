package com.aaroncarsonart.quickgame;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.LayoutManager;

public class TilePanel {

    private JLabel label;
    private JPanel panel;
    private Color bgColor;
    private Color fgColor;

    public TilePanel() {
        label = new JLabel(" ");
        label.setFont(Game.FONT);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(label);

        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
    }

    public void setBackground(Color color) {
        panel.setBackground(color);
        bgColor = color;
    }

    public void setForeground(Color color) {
        label.setForeground(color);
        fgColor = color;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public Color getFgColor() {
        return fgColor;
    }

    public void setChar(char c) {
        label.setText(String.valueOf(c));
    }

    public void setText(String s) {
        label.setText(s);
    }

    public JComponent getJPanel() {
        return panel;
    }
}
