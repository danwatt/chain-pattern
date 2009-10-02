package com.googlecode.chainpattern;

import java.util.ArrayList;
import java.util.List;

public class ChainBase<C> implements Chain<C> {
	private boolean frozen = false;
	private final List<Command<C>> commands = new ArrayList<Command<C>>();

	public Chain<C> addCommand(Command<C> command) {
		if (frozen) {
			// exception?
		}
		commands.add(command);
		return this;
	}

	public List<Command<C>> getCommands() {
		return commands;
	}

	public void execute(C context) throws Exception {
		frozen = true;
		for (Command<C> command : commands) {
			command.execute(context);
		}
	}

}
