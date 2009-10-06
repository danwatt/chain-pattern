package com.googlecode.chainpattern;

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
