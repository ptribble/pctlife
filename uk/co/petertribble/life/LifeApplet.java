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

import javax.swing.JApplet;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.*;
import java.io.File;

/**
 * The applet for pctlife.
 */
public class LifeApplet extends JApplet implements ActionListener {

    private PctBoard board;

    /*
     * Menu buttons.
     */
    private JMenuItem newItem;
    private JMenuItem stopItem;

    /**
     * Construct a new PctLife applet. A random pattern will start the game.
     */
    public void init() {
	board = new PctBoard(128, 5);

	setContentPane(board);

	JMenu jmf = new JMenu("File");
	jmf.setMnemonic(KeyEvent.VK_F);
	newItem = new JMenuItem("New", KeyEvent.VK_N);
	newItem.addActionListener(this);
	jmf.add(newItem);
	stopItem = new JMenuItem("Stop/Start", KeyEvent.VK_S);
	stopItem.addActionListener(this);
	jmf.add(stopItem);

	JMenuBar jm = new JMenuBar();
	jm.add(jmf);
	jm.add(new PctColorMenu(board));
	jm.add(new PctSpeedMenu(board));
	setJMenuBar(jm);

	setSize(780,800);
	board.randomize();
	setVisible(true);
	board.startLoop();
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == newItem) {
	    board.randomize();
	}
	if (e.getSource() == stopItem) {
	    board.stopstart();
	}
    }
}
