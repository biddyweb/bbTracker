/*
 * Copyright 2007 Joachim Sauer
 * 
 * This file is part of bbTracker.
 * 
 * bbTracker is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * bbTracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bbtracker.mobile.gui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import org.bbtracker.mobile.BBTracker;
import org.bbtracker.mobile.Log;

public class AboutForm extends Form implements CommandListener {
	private final Command backCommand;

	private final Command logCommand;

	private final Command activeDebugCommand;

	private final Command deactiveDebugCommand;

	public AboutForm() {
		super("About " + BBTracker.getName());
		append(new StringItem("Version: ", BBTracker.getVersion()));
		append(new StringItem("License: ",
				"bbTracker is released under the GNU General Public License v2. See http://www.gnu.org/licenses/."));
		append("Icons have been taken (and sometimes modified) from the Tango Project (http://tango-project.org/) and the Human Icon Theme (Copyright 2004-2006 Canonical Ltd.). Both projects release their icons under the Creative Commons Attribution-ShareAlike 2.5 license. Any modifications I did on those icons are released under the same license.");
		backCommand = new Command("Back", Command.BACK, 0);
		logCommand = new Command("Show Log", Command.SCREEN, 1);
		activeDebugCommand = new Command("Log to File", Command.SCREEN, 2);
		deactiveDebugCommand = new Command("Don't log to File", Command.SCREEN, 2);
		addCommand(backCommand);
		addCommand(logCommand);
		updateDebugCommands();
		setCommandListener(this);
	}

	private void updateDebugCommands() {
		removeCommand(activeDebugCommand);
		removeCommand(deactiveDebugCommand);
		if (BBTracker.isFileUrlAvailable()) {
			addCommand(Log.isFileActive() ? deactiveDebugCommand : activeDebugCommand);
		}
	}

	public void commandAction(final Command command, final Displayable source) {
		if (command == backCommand) {
			BBTracker.getInstance().showMainCanvas();
		} else if (command == logCommand) {
			final Font f = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
			final Form logForm = new Form("Debug Log");
			final String[] l = Log.getLog();
			for (int i = 0; i < l.length; i++) {
				final StringItem si = new StringItem(null, l[i]);
				si.setFont(f);
				logForm.append(si);
			}
			// reuse backCommand and listener
			logForm.addCommand(backCommand);
			logForm.setCommandListener(this);
			BBTracker.getDisplay().setCurrent(logForm);
		} else if (command == deactiveDebugCommand) {
			Log.setFileActive(false);
			updateDebugCommands();
		} else if (command == activeDebugCommand) {
			Log.setFileActive(true);
			updateDebugCommands();
		}
	}

}
