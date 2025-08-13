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
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * A menu to allow game speed to be selected.
 */
public final class PctSpeedMenu extends JMenu implements ActionListener {

    private static final long serialVersionUID = 1L;

    /** The board to apply the speed change to. */
    private final PctBoard board;
    /** The default update delay. */
    private final int delay;
    /** Menu Item for a slow speed. */
    private final JRadioButtonMenuItem slowSpeed;
    /** Menu Item for normal speed. */
    private final JRadioButtonMenuItem normSpeed;
    /** Menu Item for a fast speed. */
    private final JRadioButtonMenuItem fastSpeed;
    /** Menu Item for a faster speed. */
    private final JRadioButtonMenuItem fasterSpeed;
    /** Menu Item for the fastest speed. */
    private final JRadioButtonMenuItem fastestSpeed;

    /**
     * Create a menu to allow game speed to be selected for the given board.
     *
     * @param nboard the board to set the speed for
     * @param idelay the starting delay in milliseconds.
     */
    public PctSpeedMenu(final PctBoard nboard, final int idelay) {
	super("Speed");
	board = nboard;
	delay = idelay;

	setMnemonic(KeyEvent.VK_S);
	slowSpeed = new JRadioButtonMenuItem("Slow");
	slowSpeed.setMnemonic(KeyEvent.VK_S);
	slowSpeed.addActionListener(this);
	add(slowSpeed);
	normSpeed = new JRadioButtonMenuItem("Normal", true);
	normSpeed.setMnemonic(KeyEvent.VK_N);
	normSpeed.addActionListener(this);
	add(normSpeed);
	fastSpeed = new JRadioButtonMenuItem("Fast");
	fastSpeed.setMnemonic(KeyEvent.VK_F);
	fastSpeed.addActionListener(this);
	add(fastSpeed);
	fasterSpeed = new JRadioButtonMenuItem("Faster");
	fasterSpeed.setMnemonic(KeyEvent.VK_Z);
	fasterSpeed.addActionListener(this);
	add(fasterSpeed);
	fastestSpeed = new JRadioButtonMenuItem("Fastest");
	fastestSpeed.setMnemonic(KeyEvent.VK_I);
	fastestSpeed.addActionListener(this);
	add(fastestSpeed);

	final ButtonGroup bgroup = new ButtonGroup();
	bgroup.add(slowSpeed);
	bgroup.add(normSpeed);
	bgroup.add(fastSpeed);
	bgroup.add(fasterSpeed);
	bgroup.add(fastestSpeed);
    }

    @Override
    public void actionPerformed(final ActionEvent aev) {
	if (aev.getSource() == slowSpeed) {
	    board.setDelay(2 * delay);
	} else if (aev.getSource() == normSpeed) {
	    board.setDelay(delay);
	} else if (aev.getSource() == fastSpeed) {
	    board.setDelay(delay / 2);
	} else if (aev.getSource() == fasterSpeed) {
	    board.setDelay(delay / 8);
	} else if (aev.getSource() == fastestSpeed) {
	    board.setDelay(1);
	}
    }
}
