package com.googlecode.chainpattern;

public interface Chain<C> extends Command<C> {
	public Chain<C> addCommand(Command<C> command);

}
