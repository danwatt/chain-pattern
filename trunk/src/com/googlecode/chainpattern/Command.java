package com.googlecode.chainpattern;

public interface Command<C> {
	public void execute(C context) throws Exception;
}
