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
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A menu to allow colors to be chosen.
 */
public final class PctColorMenu extends JMenu implements ActionListener {

    private static final long serialVersionUID = 1L;

    /** The board to apply the color change to. */
    private final PctBoard board;

    /** Menu Item for selecting foreground color. */
    private final JMenuItem fgItem;
    /** Menu Item for selecting background color. */
    private final JMenuItem bgItem;

    /**
     * Create a menu to allow foreground and background colors to be
     * selected for the given board.
     *
     * @param nboard the board to set the colors for
     */
    public PctColorMenu(final PctBoard nboard) {
	super("Colours");
	board = nboard;
	setMnemonic(KeyEvent.VK_C);
	fgItem = new JMenuItem("Foreground", KeyEvent.VK_F);
	fgItem.addActionListener(this);
	bgItem = new JMenuItem("Background", KeyEvent.VK_B);
	bgItem.addActionListener(this);
	add(fgItem);
	add(bgItem);
    }

    @Override
    public void actionPerformed(final ActionEvent aev) {
	if (aev.getSource() == fgItem) {
	    board.setfg(JColorChooser.showDialog(this,
				"Choose cell colour",
				PctBoard.getLiveColor()));
	} else if (aev.getSource() == bgItem) {
	    board.setbg(JColorChooser.showDialog(this,
				"Choose background colour",
				PctBoard.getDeadColor()));
	}
    }
}
