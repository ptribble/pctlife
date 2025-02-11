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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * The main driver frame for PctLife.
 */
public final class PctLife extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    /** The board to be shown. */
    private PctBoard board;

    /**
     * The starting update interval, in milliseconds, 0.4s.
     */
    private static final int INTERVAL = 400;
    /**
     * The default size in cells of the life board. This is the visible size,
     * there will always be an invisible 1-cell strip around the border.
     */
    private static final int DEF_BOARD_SIZE = 128;
    /**
     * The minimum size in cells of the life board. This is the visible size,
     * there will always be an invisible 1-cell strip around the border.
     */
    private static final int MIN_BOARD_SIZE = 32;

    /**
     * The default size, in pixels, of each cell.
     */
    private static final int DEF_CELL_SIZE = 5;
    /**
     * The default gap between cells, in pixels.
     */
    private static final int DEF_CELL_GAP = 1;
    /**
     * The minimum size, in pixels, of each cell.
     */
    private static final int MIN_CELL_SIZE = 1;
    /**
     * The maximum gap between cells, in pixels.
     */
    private static final int MAX_CELL_GAP = 3;

    /**
     * Menu button to exit the application.
     */
    private JMenuItem exitItem;
    /**
     * Menu button to start a new game.
     */
    private JMenuItem newItem;
    /**
     * Menu button to stop the current game.
     */
    private JMenuItem stopItem;
    /**
     * The size in cells of the life board.
     */
    private static int boardSize = DEF_BOARD_SIZE;
    /**
     * The size, in pixels, of each cell.
     */
    private static int cellSize = DEF_CELL_SIZE;
    /**
     * The gap between cells, in pixels.
     */
    private static int cellGap = DEF_CELL_GAP;

    /**
     * Construct a new PctLife instance, starting with a random pattern.
     */
    public PctLife() {
	this((File) null);
    }

    /**
     * Construct a new PctLife instance. If a file is supplied, it will be
     * loaded, else a random pattern will start the game.
     *
     * @param infile the file to load the initial pattern from
     */
    public PctLife(final File infile) {
	super("PctLife");
	board = new PctBoard(boardSize, cellSize, cellGap);

	addWindowListener(new WindowExit());

	setContentPane(board);

	// FIXME add Load item
	final JMenu jmf = new JMenu("File");
	jmf.setMnemonic(KeyEvent.VK_F);
	newItem = new JMenuItem("New", KeyEvent.VK_N);
	newItem.addActionListener(this);
	exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
	exitItem.addActionListener(this);
	stopItem = new JMenuItem("Stop/Start", KeyEvent.VK_S);
	stopItem.addActionListener(this);
	jmf.add(newItem);
	jmf.add(stopItem);
	jmf.addSeparator();
	jmf.add(exitItem);

	final JMenuBar jmb = new JMenuBar();
	jmb.add(jmf);
	jmb.add(new PctColorMenu(board));
	jmb.add(new PctSpeedMenu(board, INTERVAL));
	setJMenuBar(jmb);

	setIconImage(new ImageIcon(this.getClass().getClassLoader().
			getResource("pixmaps/pctlife.png")).getImage());

	pack();
	if (infile == null) {
	    board.randomize();
	} else {
	    if (!board.loadPattern(infile)) {
		bailOut("Failed to load pattern");
	    }
	}
	setVisible(true);
	board.startLoop(INTERVAL);
    }

    class WindowExit extends WindowAdapter {
	@Override
	public void windowClosing(final WindowEvent wev) {
	    System.exit(0);
	}
    }

    @Override
    public void actionPerformed(final ActionEvent aev) {
	if (aev.getSource() == newItem) {
	    board.randomize();
	} else if (aev.getSource() == stopItem) {
	    board.stopstart();
	} else if (aev.getSource() == exitItem) {
	    System.exit(0);
	}
    }

    private static void bailOut(final String msg) {
	System.err.println(msg); //NOPMD
	System.exit(1);
    }

    /**
     * Create a new Life game. The supported flags are:
     * -b The board size
     * -g The gap between cells
     * -s The size of each cell
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
	if (args.length > 0) {
	    int i = 0; //NOPMD
	    while (i < args.length) {
		if ("-s".equals(args[i])) {
		    ++i;
		    if (i < args.length) {
			try {
			    cellSize = Integer.parseInt(args[i]);
			} catch (NumberFormatException ex) {
			    bailOut("Invalid cell size!");
			}
			if (cellSize < MIN_CELL_SIZE) {
			    bailOut("Cell size too small!");
			}
		    } else {
			bailOut("Expecting an argument to -s!");
		    }
		} else if ("-b".equals(args[i])) {
		    ++i;
		    if (i < args.length) {
			try {
			    boardSize = Integer.parseInt(args[i]);
			} catch (NumberFormatException ex) {
			    bailOut("Invalid board size!");
			}
			if (boardSize < MIN_BOARD_SIZE) {
			    bailOut("Board size too small!");
			}
		    } else {
			bailOut("Expecting an argument to -b!");
		    }
		} else if ("-g".equals(args[i])) {
		    ++i;
		    if (i < args.length) {
			try {
			    cellGap = Integer.parseInt(args[i]);
			} catch (NumberFormatException ex) {
			    bailOut("Invalid call gap!");
			}
			if (cellGap < 0) {
			    bailOut("Cell gap too small!");
			}
			if (cellGap > MAX_CELL_GAP) {
			    bailOut("Cell gap too large!");
			}
		    } else {
			bailOut("Expecting an argument to -g!");
		    }
		} else {
		    break;
		}
		++i;
	    }
	    if (i < args.length) {
		final File fin = new File(args[i]);
		if (fin.exists()) {
		    new PctLife(fin);
		} else {
		    bailOut("File " + fin + " does not exist!");
		}
	    } else {
		new PctLife();
	    }
	} else {
	    new PctLife();
	}
    }
}
