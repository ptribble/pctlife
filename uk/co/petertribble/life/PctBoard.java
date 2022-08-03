/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

package uk.co.petertribble.life;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The main board for pctlife.
 */
public class PctBoard extends JPanel implements ActionListener {

    /*
     * The initial filling factor.
     */
    private static final double FILL_FACTOR = 0.15d;
    private static Color livecolor = Color.RED;
    private static Color deadcolor = Color.black;

    /** The current size in cells of the board. */
    private final int BOARD_SIZE;
    /** The current size in cells of the middle of the board. */
    private final int BOARD_MID;
    /** The current cell size in pixels. */
    private final int CELL_SIZE;

    /**
     * A Timer, to update the model in a loop.
     */
    private Timer timer;

    /**
     * An array of int[] for the current generation.
     */
    private int[][] oldgen;
    /**
     * An array of int[] for the next generation.
     */
    private int[][] newgen;
    /**
     * An array of boolean[] to record the liveness of a cell..
     */
    private boolean[][] labels;

    /**
     * Construct a new PctBoard instance of the given size.
     *
     * @param BOARD_SIZE the size of the board
     * @param CELL_SIZE the size of each cell
     */
    public PctBoard(int BOARD_SIZE, int CELL_SIZE) {
	this.BOARD_SIZE = BOARD_SIZE;
	this.CELL_SIZE = CELL_SIZE;
	BOARD_MID = BOARD_SIZE/2;

	oldgen = new int[BOARD_SIZE+2][BOARD_SIZE+2];
	newgen = new int[BOARD_SIZE+2][BOARD_SIZE+2];
	labels = new boolean[BOARD_SIZE][BOARD_SIZE];

	Dimension dboard = new Dimension(BOARD_SIZE*(CELL_SIZE+1),
					BOARD_SIZE*(CELL_SIZE+1));
	setSize(dboard);
	setMinimumSize(dboard);
	setPreferredSize(dboard);
	setBackground(Color.BLACK.brighter());
    }

    /**
     * Populate the board with random data.
     */
    public void randomize() {
	for (int i = 0; i < BOARD_SIZE; i++) {
	    for (int j = 0; j < BOARD_SIZE; j++) {
		oldgen[i+1][j+1] = Math.random() < FILL_FACTOR ? 1 : 0;
		labels[i][j] = oldgen[i+1][j+1] == 1;
	    }
	}
	repaint();
    }

    private void blank() {
	for (int i = 0; i < BOARD_SIZE+2; i++) {
	    for (int j = 0; j < BOARD_SIZE+2; j++) {
		oldgen[i][j] = 0;
	    }
	}
	for (int i = 0; i < BOARD_SIZE; i++) {
	    for (int j = 0; j < BOARD_SIZE; j++) {
		labels[i][j] = false;
	    }
	}
    }

    /**
     * Import a life pattern. If there's a problem, return false and the board
     * will be left in an indeterminate state.
     *
     * @param f the File to read in
     *
     * @return false in the event of a problem
     */
    public boolean loadPattern(File f) {
	blank();
	if (!f.exists()) {
	    return false;
	}
	int x = 0;
	int xblock = 0;
	int y = 0;
	try (BufferedReader input =  new BufferedReader(new FileReader(f))) {
	    String line;
	    try {
		line = input.readLine();
		if (line == null || !line.startsWith("#Life")) {
		    return false;
		}
		while ((line = input.readLine()) != null) {
		    if (line.startsWith("#P")) {
			String[] ds = line.split("\\s+", 3);
			try {
			    x = BOARD_MID + Integer.parseInt(ds[1]);
			    xblock = x;
			    y = BOARD_MID + Integer.parseInt(ds[2]);
			} catch (NumberFormatException nfe) {
			    return false;
			}
		    } else if (line.startsWith("#")) { // NOPMD
			// continue
			// FIXME check for #R and #N
		    } else {
			// read the pattern and populate the board
			for (int i = 0; i < line.length(); i++) {
			    char c = line.charAt(i);
			    if (c == '.') {
				x++;
			    } else if (c == '*') {
				x++;
				setAlive(x, y);
			    }
			}
			// ready for the next line
			y++;
			x = xblock;
		    }
		}
	    } catch (IOException ioe) {
		return false;
	    }
	} catch (IOException ioe) {
	    return false;
	}
	repaint();
	return true;
    }

    private void setAlive(int i, int j) {
	oldgen[i+1][j+1] = 1;
	labels[i][j] = true;
    }

    /**
     * Run one step of the game.
     *
     * Essentially, just add up the values of the neighbouring cells.
     */
    public void step() {
	for (int i = 0; i < BOARD_SIZE; i++) {
	    for (int j = 0; j < BOARD_SIZE; j++) {
		newgen[i+1][j+1] = oldgen[i][j]
		    + oldgen[i+1][j]
		    + oldgen[i+2][j]
		    + oldgen[i][j+1]
		    + oldgen[i+2][j+1]
		    + oldgen[i][j+2]
		    + oldgen[i+1][j+2]
		    + oldgen[i+2][j+2];
	    }
	}
	// do edges
	for (int i = 0; i < BOARD_SIZE; i++) {
	    newgen[i+1][0] = oldgen[i][0]
		    + oldgen[i+2][0]
		    + oldgen[i][1]
		    + oldgen[i+1][1]
		    + oldgen[i+2][1];
	    newgen[i+1][BOARD_SIZE+1] = oldgen[i][BOARD_SIZE+1]
		    + oldgen[i+2][BOARD_SIZE+1]
		    + oldgen[i][BOARD_SIZE]
		    + oldgen[i+1][BOARD_SIZE]
		    + oldgen[i+2][BOARD_SIZE];
	}
	for (int j = 0; j < BOARD_SIZE; j++) {
		newgen[0][j+1] = oldgen[0][j]
		    + oldgen[0][j+2]
		    + oldgen[1][j]
		    + oldgen[1][j+1]
		    + oldgen[1][j+2];
		newgen[BOARD_SIZE+1][j+1] = oldgen[BOARD_SIZE+1][j]
		    + oldgen[BOARD_SIZE+1][j+2]
		    + oldgen[BOARD_SIZE][j]
		    + oldgen[BOARD_SIZE][j+1]
		    + oldgen[BOARD_SIZE][j+2];
	}
	// end edges
	// do corners
	newgen[0][0] = oldgen[0][1] + oldgen[1][1] + oldgen[1][0];
	newgen[0][BOARD_SIZE+1] = oldgen[0][BOARD_SIZE] + oldgen[1][BOARD_SIZE]
	    + oldgen[1][BOARD_SIZE+1];
	newgen[BOARD_SIZE+1][0] = oldgen[BOARD_SIZE][0] + oldgen[BOARD_SIZE][1]
	    + oldgen[BOARD_SIZE+1][1];
	newgen[BOARD_SIZE+1][BOARD_SIZE+1] = oldgen[BOARD_SIZE][BOARD_SIZE+1]
	    + oldgen[BOARD_SIZE+1][BOARD_SIZE] + oldgen[BOARD_SIZE][BOARD_SIZE];
	// end corners
	for (int i = 0; i < BOARD_SIZE+2; i++) {
	    for (int j = 0; j < BOARD_SIZE+2; j++) {
		if (oldgen[i][j] == 1) {
		    oldgen[i][j] = isAlive(newgen[i][j]) ? 1 : 0;
		} else {
		    oldgen[i][j] = isBorn(newgen[i][j]) ? 1 : 0;
		}
	    }
	}
	for (int i = 0; i < BOARD_SIZE; i++) {
	    for (int j = 0; j < BOARD_SIZE; j++) {
		labels[i][j] = oldgen[i+1][j+1] == 1;
	    }
	}
	repaint();
    }

    /*
     * isAlive() and isBorn() are abstracted to allow extension to universes
     * with different rules.
     */
    private boolean isAlive(int i) {
	return i == 2 || i == 3;
    }

    private boolean isBorn(int i) {
	return i == 3;
    }

    /**
     * Get the current live (foreground) color.
     *
     * @return The current Color of live cells.
     */
    public static Color getLiveColor() {
	return livecolor;
    }

    /**
     * Set the foreground color.
     *
     * @param newColor The new foreground color.
     */
    public void setfg(Color newColor) {
	if (newColor != null) {
	    livecolor = newColor;
	}
    }

    /**
     * Get the current dead (background) color.
     *
     * @return The current Color of dead cells.
     */
    public static Color getDeadColor() {
	return deadcolor;
    }

    /**
     * Set the background color.
     *
     * @param newColor The new background color.
     */
    public void setbg(Color newColor) {
	if (newColor != null) {
	    deadcolor = newColor;
	}
    }

    /**
     * Start the game.
     */
    public void startLoop() {
	if (timer == null) {
	    timer = new Timer(PctLife.INTERVAL, this);
	}
	timer.start();
    }

    /**
     * Pause the game if it's running, restart it if paused.
     */
    public void stopstart() {
	if (timer != null) {
	    if (timer.isRunning()) {
		timer.stop();
	    } else {
		timer.start();
	    }
	}
    }

    /**
     * Set the delay between steps in the game.
     *
     * @param i the desired delay in milliseconds.
     */
    public void setDelay(int i) {
	if (timer != null) {
	    timer.setDelay(i);
	}
    }

    @Override
    public void paint(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	Dimension d = getSize();
	g2.setPaint(deadcolor);
	g2.fill(new Rectangle2D.Double(0.0d, 0.0d, d.width, d.height));
	g2.setPaint(livecolor);
	double dw = d.width/((double) BOARD_SIZE);
	double dh = d.height/((double) BOARD_SIZE);
	double ddw = ((double) CELL_SIZE)*dw/(((double) CELL_SIZE) + 1.0d);
	double ddh = ((double) CELL_SIZE)*dh/(((double) CELL_SIZE) + 1.0d);
	for (int i = 0; i < BOARD_SIZE; i++) {
	    for (int j = 0; j < BOARD_SIZE; j++) {
		if (labels[i][j]) {
		    g2.fill(new Rectangle2D.Double(dw*i, dh*j, ddw, ddh));
		}
	    }
	}
    }

    public void actionPerformed(ActionEvent e) {
	step();
    }
}
