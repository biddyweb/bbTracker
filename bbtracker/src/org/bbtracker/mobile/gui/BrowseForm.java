package org.bbtracker.mobile.gui;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.bbtracker.mobile.BBTracker;

public class BrowseForm extends List implements CommandListener {
	private final String title;

	private String path;

	private Runnable callback;

	private final Command selectCommand;

	private final Command cancelCommand;

	public BrowseForm(final String title, final String path) {
		this(title, path, null);
	}

	public BrowseForm(final String title, final String path, final Runnable callback) {
		super(title, List.IMPLICIT);
		this.title = title;
		this.path = path.length() == 0 ? null : path;
		this.callback = callback;

		selectCommand = new Command("Select", Command.OK, 1);
		cancelCommand = new Command("Cancel", Command.CANCEL, 2);
		setSelectCommand(selectCommand);
		addCommand(cancelCommand);
		setCommandListener(this);

		updateContent();
	}

	public void setCallback(final Runnable callback) {
		this.callback = callback;
	}

	private void updateContent() {
		deleteAll();
		setTitle(path == null ? title : path + " - " + title);
		if (path == null) {
			final Enumeration roots = FileSystemRegistry.listRoots();
			while (roots.hasMoreElements()) {
				final String root = (String) roots.nextElement();
				append(root, null);
			}
		} else {
			try {
				final FileConnection connection = (FileConnection) Connector.open("file:///" + path);
				if (!connection.isDirectory()) {
					path = null;
					updateContent();
				}
				append("<select this directory>", null);
				append("..", null);
				final Enumeration list = connection.list();
				while (list.hasMoreElements()) {
					final String element = (String) list.nextElement();
					if (element.endsWith("/")) {
						append(element, null);
					}
				}
			} catch (final IOException e) {
				BBTracker.nonFatal(e, "getting file roots", null);
			}
		}
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void commandAction(final Command cmd, final Displayable current) {
		if (cmd == selectCommand) {
			if (path != null && getSelectedIndex() == 0) {
				// <SELECT> selected
				callback.run();
			} else {
				final String selected = getString(getSelectedIndex());
				if (selected.equals("..")) {
					final int slashIndex = path.lastIndexOf('/', path.length() - 2);
					if (slashIndex == -1) {
						path = null;
					} else {
						path = path.substring(0, slashIndex);
					}
					System.out.println("..: " + path);
				} else {
					path = path == null ? selected : path + selected;
					System.out.println(selected + ": " + path);
				}
				updateContent();
			}
		} else if (cmd == cancelCommand) {
			path = null;
			callback.run();
		}
	}
}