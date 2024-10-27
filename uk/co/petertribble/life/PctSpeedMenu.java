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

    /** Menu Item for a slow speed. */
    private final JRadioButtonMenuItem slowSpeed;
    /** Menu Item for normal speed. */
    private final JRadioButtonMenuItem normSpeed;
    /** Menu Item for a fast speed. */
    private final JRadioButtonMenuItem fastSpeed;
    /** Menu Item for a zoom speed. */
    private final JRadioButtonMenuItem zoomSpeed;
    /** Menu Item for an insane speed. */
    private final JRadioButtonMenuItem insaneSpeed;

    /**
     * Create a menu to allow game speed to be selected for the given board.
     *
     * @param nboard the board to set the speed for
     */
    public PctSpeedMenu(final PctBoard nboard) {
	super("Speed");
	board = nboard;

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
	zoomSpeed = new JRadioButtonMenuItem("Zoom");
	zoomSpeed.setMnemonic(KeyEvent.VK_Z);
	zoomSpeed.addActionListener(this);
	add(zoomSpeed);
	insaneSpeed = new JRadioButtonMenuItem("Insane");
	insaneSpeed.setMnemonic(KeyEvent.VK_I);
	insaneSpeed.addActionListener(this);
	add(insaneSpeed);

	final ButtonGroup bgroup = new ButtonGroup();
	bgroup.add(slowSpeed);
	bgroup.add(normSpeed);
	bgroup.add(fastSpeed);
	bgroup.add(zoomSpeed);
	bgroup.add(insaneSpeed);
    }

    @Override
    public void actionPerformed(final ActionEvent aev) {
	if (aev.getSource() == slowSpeed) {
	    board.setDelay(2*PctLife.INTERVAL);
	} else if (aev.getSource() == normSpeed) {
	    board.setDelay(PctLife.INTERVAL);
	} else if (aev.getSource() == fastSpeed) {
	    board.setDelay(PctLife.INTERVAL/2);
	} else if (aev.getSource() == zoomSpeed) {
	    board.setDelay(PctLife.INTERVAL/8);
	} else if (aev.getSource() == insaneSpeed) {
	    board.setDelay(1);
	}
    }
}
