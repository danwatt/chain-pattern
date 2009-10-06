package com.googlecode.chainpattern;

public interface Command<C> {
	public static final boolean PROCESSING_COMPLETE = true;
	public static final boolean CONTINUE_PROCESSING = false;

	public boolean execute(C context) throws Exception;
}
