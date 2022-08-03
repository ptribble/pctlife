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
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A menu to allow colors to be chosen.
 */
public class PctColorMenu extends JMenu implements ActionListener {

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
     * @param board the board to set the colors for
     */
    public PctColorMenu(PctBoard board) {
	super("Colours");
	this.board = board;
	setMnemonic(KeyEvent.VK_C);
	fgItem = new JMenuItem("Foreground", KeyEvent.VK_F);
	fgItem.addActionListener(this);
	bgItem = new JMenuItem("Background", KeyEvent.VK_B);
	bgItem.addActionListener(this);
	add(fgItem);
	add(bgItem);
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == fgItem) {
	    board.setfg(JColorChooser.showDialog(this,
				"Choose cell colour",
				PctBoard.getLiveColor()));
	} else if (e.getSource() == bgItem) {
	    board.setbg(JColorChooser.showDialog(this,
				"Choose background colour",
				PctBoard.getDeadColor()));
	}
    }
}
