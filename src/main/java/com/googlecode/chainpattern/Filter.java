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

package com.googlecode.chainpattern;

/**
 * <p>
 * A {@link Filter} is a specialized {@link Command} that also expects the
 * {@link Chain} that is executing it to call the <code>postprocess()</code>
 * method if it called the <code>execute()</code> method. This promise must be
 * fulfilled in spite of any possible exceptions thrown by the
 * <code>execute()</code> method of this {@link Command}, or any subsequent
 * {@link Command} whose <code>execute()</code> method was called. The owning
 * {@link Chain} must call the <code>postprocess()</code> method of each
 * {@link Filter} in a {@link Chain} in reverse order of the invocation of their
 * <code>execute()</code> methods.
 * </p>
 * 
 * <p>
 * The most common use case for a {@link Filter}, as opposed to a
 * {@link Command}, is where potentially expensive resources must be acquired
 * and held until the processing of a particular request has been completed,
 * even if execution is delegated to a subsequent {@link Command} via the
 * <code>execute()</code> returning <code>false</code>. A {@link Filter} can
 * reliably release such resources in the <code>postprocess()</code> method,
 * which is guaranteed to be called by the owning {@link Chain}.
 * </p>
 * 
 */

public interface Filter<C> extends Command<C> {
	/**
	 * Execute any cleanup activities, such as releasing resources that were
	 * acquired during the execute() method of this Filter instance
	 * 
	 * @param context
	 *            The context to be processed by this Filter
	 * @param exception
	 *            The Exception (if any) that was thrown by the last Command
	 *            that was executed; otherwise null
	 * @return If a non-null exception was "handled" by this method (and
	 *         therefore need not be re-thrown), return true; otherwise, return
	 *         false
	 */
	public boolean postprocess(C context, Exception exception);

}
