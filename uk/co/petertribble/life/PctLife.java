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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.*;
import java.io.File;

/**
 * The main driver frame for pctlife.
 */
public class PctLife extends JFrame implements ActionListener {

    private PctBoard board;

    /*
     * The update interval, 0.4s.
     */
    public static final int INTERVAL = 400;

    /*
     * The default size of the life board. This is the visible size, there
     * will always be an invisible 1-cell strip around the border.
     */
    private static final int DEFAULT_BOARD_SIZE = 128;
    private static final int MIN_BOARD_SIZE = 32;

    /*
     * The default size, in pixels, of each cell.
     */
    private static final int DEFAULT_CELL_SIZE = 5;
    private static final int MIN_CELL_SIZE = 1;

    /*
     * Menu buttons.
     */
    private JMenuItem exitItem;
    private JMenuItem newItem;
    private JMenuItem stopItem;

    private static int BOARD_SIZE = DEFAULT_BOARD_SIZE; 
    private static int CELL_SIZE = DEFAULT_CELL_SIZE;

    /**
     * Construct a new PctLife instance.
     */
    public PctLife() {
	this((File) null);
    }

    /**
     * Construct a new PctLife instance. If a file is supplied, it will be
     * loaded, else a random pattern will start the game.
     *
     * @param f the file to load the initial pattern from
     */
    public PctLife(File f) {
	super("PctLife");
	board = new PctBoard(BOARD_SIZE, CELL_SIZE);

	addWindowListener(new winExit());

	setContentPane(board);

	// FIXME add Load item
	JMenu jmf = new JMenu("File");
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

	JMenuBar jm = new JMenuBar();
	jm.add(jmf);
	jm.add(new PctColorMenu(board));
	jm.add(new PctSpeedMenu(board));
	setJMenuBar(jm);

	setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource("pixmaps/pctlife.png")).getImage());

	pack();
	if (f == null) {
	    board.randomize();
	} else {
	    board.loadPattern(f);
	}
	setVisible(true);
	board.startLoop();
    }

    class winExit extends WindowAdapter {
	public void windowClosing(WindowEvent we) {
	    System.exit(0);
	}
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == newItem) {
	    board.randomize();
	} else if (e.getSource() == stopItem) {
	    board.stopstart();
	} else if (e.getSource() == exitItem) {
	    System.exit(0);
	}
    }

    /**
     * Create a new Life game.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
	if (args.length > 0) {
	    int i = 0;
	    while (i < args.length) {
		if (args[i].equals("-s")) {
		    ++i;
		    if (i < args.length) {
			try {
			    CELL_SIZE = Integer.parseInt(args[i]);
			} catch (NumberFormatException ex) {
			    System.err.println("Invalid cell size!");
			    System.exit(1);
			}
			if (CELL_SIZE < MIN_CELL_SIZE) {
			    System.err.println("Cell size too small!");
			    System.exit(1);
			}
		    } else {
			System.err.println("Expecting an argument to -s!");
			System.exit(1);
		    }
		} else if (args[i].equals("-b")) {
		    ++i;
		    if (i < args.length) {
			try {
			    BOARD_SIZE = Integer.parseInt(args[i]);
			} catch (NumberFormatException ex) {
			    System.err.println("Invalid board size!");
			    System.exit(1);
			}
			if (BOARD_SIZE < MIN_BOARD_SIZE) {
			    System.err.println("Board size too small!");
			    System.exit(1);
			}
		    } else {
			System.err.println("Expecting an argument to -b!");
			System.exit(1);
		    }
		} else {
		    break;
		}
		++i;
	    }
	    if (i < args.length) {
		File fin = new File(args[i]);
		if (fin.exists()) {
		    new PctLife(fin);
		} else {
		    System.err.println("File " + fin + " does not exist!");
		    System.exit(1);
		}
	    } else {
		new PctLife();
	    }
	} else {
	    new PctLife();
	}
    }
}
