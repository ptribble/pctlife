/*
 * SPDX-License-Identifier: CDDL-1.0
 *
 * CDDL HEADER START
 *
 * This file and its contents are supplied under the terms of the
 * Common Development and Distribution License ("CDDL"), version 1.0.
 * You may only use this file in accordance with the terms of version
 * 1.0 of the CDDL.
 *
 * A full copy of the text of the CDDL should have accompanied this
 * source. A copy of the CDDL is also available via the Internet at
 * http://www.illumos.org/license/CDDL.
 *
 * CDDL HEADER END
 *
 * Copyright 2025 Peter Tribble
 *
 */

package uk.co.petertribble.life;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The main board for pctlife.
 */
public final class PctBoard extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    /** The initial filling factor. */
    private static final double FILL_FACTOR = 0.15d;
    /** The initial colour of live cells. */
    private static Color livecolor = Color.RED;
    /** The initial colour of dead cells. */
    private static Color deadcolor = Color.black;

    /** The current size in cells of the board. */
    private final int boardSize;
    /** The current size in cells of the middle of the board. */
    private final int boardMid;
    /** The current cell size in pixels. */
    private final int cellSize;
    /** The gap left between cells as a double. */
    private final double dCellGap;

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
     * An array of boolean[] to record the liveness of a cell.
     */
    private boolean[][] labels;

    /**
     * Construct a new PctBoard instance of the given size.
     *
     * @param nboardSize the size of the board
     * @param ncellSize the size of each cell
     * @param cellGap the gap left between cells
     */
    public PctBoard(final int nboardSize, final int ncellSize,
		    final int cellGap) {
	super();
	boardSize = nboardSize;
	cellSize = ncellSize;
	boardMid = boardSize / 2;
	dCellGap = cellGap;

	oldgen = new int[boardSize + 2][boardSize + 2];
	newgen = new int[boardSize + 2][boardSize + 2];
	labels = new boolean[boardSize][boardSize];

	final Dimension dboard = new Dimension(boardSize * (cellSize + cellGap),
					boardSize * (cellSize + cellGap));
	setSize(dboard);
	setMinimumSize(dboard);
	setPreferredSize(dboard);
	setBackground(Color.BLACK.brighter());
    }

    /**
     * Populate the board with random data.
     */
    public void randomize() {
	for (int i = 0; i < boardSize; i++) {
	    for (int j = 0; j < boardSize; j++) {
		oldgen[i + 1][j + 1] = Math.random() < FILL_FACTOR ? 1 : 0;
		labels[i][j] = oldgen[i + 1][j + 1] == 1;
	    }
	}
	repaint();
    }

    private void blank() {
	for (int i = 0; i < boardSize + 2; i++) {
	    for (int j = 0; j < boardSize + 2; j++) {
		oldgen[i][j] = 0;
	    }
	}
	for (int i = 0; i < boardSize; i++) {
	    for (int j = 0; j < boardSize; j++) {
		labels[i][j] = false;
	    }
	}
    }

    /**
     * Import a life pattern. If there's a problem, return false and the board
     * will be left in an indeterminate state.
     *
     * @param infile the File to read in
     *
     * @return false in the event of a problem
     */
    public boolean loadPattern(final File infile) {
	blank();
	if (!infile.exists()) {
	    return false;
	}
	int x = 0;
	int xblock = 0;
	int y = 0;
	try (BufferedReader input =  Files.newBufferedReader(infile.toPath())) {
	    String line;
	    try {
		line = input.readLine();
		if (line == null || !line.startsWith("#Life")) {
		    return false;
		}
		while ((line = input.readLine()) != null) {
		    if (line.startsWith("#P")) {
			final String[] ds = line.split("\\s+", 3);
			try {
			    x = boardMid + Integer.parseInt(ds[1]);
			    xblock = x;
			    y = boardMid + Integer.parseInt(ds[2]);
			} catch (NumberFormatException nfe) {
			    return false;
			}
		    } else if (!line.startsWith("#")) {
			// skip any other directives like #R and #N
			// read the pattern and populate the board
			for (int i = 0; i < line.length(); i++) {
			    final char c = line.charAt(i);
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

    private void setAlive(final int iii, final int jjj) {
	oldgen[iii + 1][jjj + 1] = 1;
	labels[iii][jjj] = true;
    }

    /**
     * Run one step of the game.
     *
     * Essentially, just add up the values of the neighbouring cells.
     */
    public void step() {
	for (int i = 0; i < boardSize; i++) {
	    for (int j = 0; j < boardSize; j++) {
		newgen[i + 1][j + 1] = oldgen[i][j]
		    + oldgen[i + 1][j]
		    + oldgen[i + 2][j]
		    + oldgen[i][j + 1]
		    + oldgen[i + 2][j + 1]
		    + oldgen[i][j + 2]
		    + oldgen[i + 1][j + 2]
		    + oldgen[i + 2][j + 2];
	    }
	}
	// do edges
	for (int i = 0; i < boardSize; i++) {
	    newgen[i + 1][0] = oldgen[i][0]
		    + oldgen[i + 2][0]
		    + oldgen[i][1]
		    + oldgen[i + 1][1]
		    + oldgen[i + 2][1];
	    newgen[i + 1][boardSize + 1] = oldgen[i][boardSize + 1]
		    + oldgen[i + 2][boardSize + 1]
		    + oldgen[i][boardSize]
		    + oldgen[i + 1][boardSize]
		    + oldgen[i + 2][boardSize];
	}
	for (int j = 0; j < boardSize; j++) {
		newgen[0][j + 1] = oldgen[0][j]
		    + oldgen[0][j + 2]
		    + oldgen[1][j]
		    + oldgen[1][j + 1]
		    + oldgen[1][j + 2];
		newgen[boardSize + 1][j + 1] = oldgen[boardSize + 1][j]
		    + oldgen[boardSize + 1][j + 2]
		    + oldgen[boardSize][j]
		    + oldgen[boardSize][j + 1]
		    + oldgen[boardSize][j + 2];
	}
	// end edges
	// do corners
	newgen[0][0] = oldgen[0][1] + oldgen[1][1] + oldgen[1][0];
	newgen[0][boardSize + 1] = oldgen[0][boardSize] + oldgen[1][boardSize]
	    + oldgen[1][boardSize + 1];
	newgen[boardSize + 1][0] = oldgen[boardSize][0] + oldgen[boardSize][1]
	    + oldgen[boardSize + 1][1];
	newgen[boardSize + 1][boardSize + 1] = oldgen[boardSize][boardSize + 1]
	    + oldgen[boardSize + 1][boardSize] + oldgen[boardSize][boardSize];
	// end corners
	for (int i = 0; i < boardSize + 2; i++) {
	    for (int j = 0; j < boardSize + 2; j++) {
		if (oldgen[i][j] == 1) {
		    oldgen[i][j] = isAlive(newgen[i][j]) ? 1 : 0;
		} else {
		    oldgen[i][j] = isBorn(newgen[i][j]) ? 1 : 0;
		}
	    }
	}
	for (int i = 0; i < boardSize; i++) {
	    for (int j = 0; j < boardSize; j++) {
		labels[i][j] = oldgen[i + 1][j + 1] == 1;
	    }
	}
	repaint();
    }

    /*
     * isAlive() and isBorn() are abstracted to allow extension to universes
     * with different rules.
     */
    private boolean isAlive(final int inear) {
	return inear == 2 || inear == 3;
    }

    private boolean isBorn(final int inear) {
	return inear == 3;
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
    public void setfg(final Color newColor) {
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
    public void setbg(final Color newColor) {
	if (newColor != null) {
	    deadcolor = newColor;
	}
    }

    /**
     * Start the game.
     *
     * @param idelay the starting delay in milliseconds.
     */
    public void startLoop(final int idelay) {
	if (timer == null) {
	    timer = new Timer(idelay, this);
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
     * @param idelay the desired delay in milliseconds.
     */
    public void setDelay(final int idelay) {
	if (timer != null) {
	    timer.setDelay(idelay);
	}
    }

    @Override
    public void paint(final Graphics g) {
	final Graphics2D gr2 = (Graphics2D) g;
	final Dimension d = getSize();
	gr2.setPaint(deadcolor);
	gr2.fill(new Rectangle2D.Double(0.0d, 0.0d, d.width, d.height));
	gr2.setPaint(livecolor);
	final double dww = d.width / ((double) boardSize);
	final double dhh = d.height / ((double) boardSize);
	final double ddw = cellSize * dww / (cellSize + dCellGap);
	final double ddh = cellSize * dhh / (cellSize + dCellGap);
	for (int i = 0; i < boardSize; i++) {
	    for (int j = 0; j < boardSize; j++) {
		if (labels[i][j]) {
		    gr2.fill(new Rectangle2D.Double(
					     dww * i, dhh * j, ddw, ddh));
		}
	    }
	}
    }

    @Override
    public void actionPerformed(final ActionEvent aev) {
	step();
    }
}
