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

import java.awt.event.*;
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
     * The update interval, 0.4s.
     */
    public static final int INTERVAL = 400;

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

    private static int boardSize = DEF_BOARD_SIZE;
    private static int cellSize = DEF_CELL_SIZE;
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

	addWindowListener(new winExit());

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
	jmb.add(new PctSpeedMenu(board));
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
	board.startLoop();
    }

    class winExit extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent wev) {
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
