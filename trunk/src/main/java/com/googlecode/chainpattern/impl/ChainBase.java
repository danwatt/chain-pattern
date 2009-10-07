/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.googlecode.chainpattern.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import com.googlecode.chainpattern.Chain;
import com.googlecode.chainpattern.Command;
import com.googlecode.chainpattern.Filter;

/**
 * <p>
 * Convenience base class for {@link Chain} implementations.
 * </p>
 */
public class ChainBase<C> implements Chain<C> {

	/**
	 * <p>
	 * Flag indicating whether the configuration of our commands list has been
	 * frozen by a call to the <code>execute()</code> method.
	 * </p>
	 */
	private boolean frozen = false;

	/**
	 * <p>
	 * The list of {@link Command}s configured for this {@link Chain}, in the
	 * order in which they may delegate processing to the remainder of the
	 * {@link Chain}.
	 * </p>
	 */
	private final List<Command<C>> commands = new ArrayList<Command<C>>();

	/**
	 * <p>
	 * Construct a {@link Chain} with no configured {@link Command}s.
	 * </p>
	 */
	public ChainBase() {

	}

	/**
	 * See the {@link Chain} JavaDoc.
	 * 
	 * @param command
	 *            The {@link Command} to be added
	 * 
	 * @exception IllegalArgumentException
	 *                if <code>command</code> is <code>null</code>
	 * @exception IllegalStateException
	 *                if no further configuration is allowed
	 */

	public Chain<C> addCommand(Command<C> command) {
		if (null == command) {
			throw new IllegalArgumentException("Commands may not be null");
		}
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
	 * See the {@link Chain} JavaDoc.
	 * 
	 * @param context
	 *            The {@link Context} to be processed by this {@link Chain}
	 * 
	 * @throws Exception
	 *             if thrown by one of the {@link Command}s in this
	 *             {@link Chain} but not handled by a <code>postprocess()</code>
	 *             method of a {@link Filter}
	 * @throws IllegalArgumentException
	 *             if <code>context</code> is <code>null</code>
	 * 
	 * @return <code>true</code> if the processing of this {@link Context} has
	 *         been completed, or <code>false</code> if the processing of this
	 *         {@link Context} should be delegated to a subsequent
	 *         {@link Command} in an enclosing {@link Chain}
	 */
	public boolean execute(C context) throws Exception {
		this.frozen = true;
		boolean savedResult = CONTINUE_PROCESSING;
		ListIterator<Command<C>> li = this.commands.listIterator();
		Exception savedException = null;
		while (li.hasNext()) {
			Command<C> command = li.next();
			try {
				savedResult = command.execute(context);
			} catch (Exception toSave) {
				savedException = toSave;
				break;
			}
			if (Chain.PROCESSING_COMPLETE == savedResult) {
				break;
			}
		}
		boolean handled = false;
		while (li.hasPrevious()) {
			Command<C> command = li.previous();
			if (command instanceof Filter<?>) {
				Filter<C> f = (Filter<C>) command;
				try {
					handled |= f.postprocess(context, savedException);
				} catch (Exception ignoreMe) {

				}
			}
		}
		if (null != savedException && !handled) {
			throw savedException;
		} else {
			return savedResult;
		}
	}
}
