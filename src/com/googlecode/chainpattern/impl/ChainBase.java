package com.googlecode.chainpattern.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.chainpattern.Chain;
import com.googlecode.chainpattern.Command;

public class ChainBase<C> implements Chain<C> {
	private boolean frozen = false;
	private final List<Command<C>> commands = new ArrayList<Command<C>>();

	public Chain<C> addCommand(Command<C> command) {
		if (frozen) {
			throw new IllegalStateException("A command may not be added to a chain once the chain has been executed");
		}
		commands.add(command);
		return this;
	}

	public List<Command<C>> getCopyOfCommands() {
		return Collections.unmodifiableList(commands);
	}

	/**
	 * Execute the processing represented by this Chain according to the
	 * following algorithm:
	 * <ul>
	 * <li>If there are no configured Commands in the Chain, return false.</li>
	 * <li>Call the execute() method of each Command configured on this chain,
	 * in the order they were added via calls to the addCommand() method, until
	 * the end of the configured Commands is encountered, or until one of the
	 * executed Commands returns true or throws and exception</li>
	 * <li>If the last Command whose execute() method was called threw and
	 * exception, rethrow that exception.</li>
	 * <li>Otherwise, return the value returned by the execute() method of the
	 * last Command that was executed. This will be true if the last Command
	 * indicated that processing of this Context has been completed, or false if
	 * none of the called Commands returned true.</li>
	 * </ul>
	 * 
	 * @return true if the processing of this Context has been completed, or
	 *         false if the processing of this Context should be delegated to a
	 *         subsequent Command in an enclosing Chain.
	 */
	public boolean execute(C context) throws Exception {
		frozen = true;
		for (Command<C> command : commands) {
			if (Chain.PROCESSING_COMPLETE == command.execute(context)) {
				return Chain.PROCESSING_COMPLETE;
			}
		}
		return Chain.CONTINUE_PROCESSING;
	}

}
