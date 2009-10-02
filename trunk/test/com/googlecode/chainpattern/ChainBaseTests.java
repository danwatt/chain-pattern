package com.googlecode.chainpattern;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.googlecode.chainpattern.ChainBase;
import com.googlecode.chainpattern.Command;

public class ChainBaseTests {
	@Test
	public void singleItem() throws Exception {
		ChainBase<Boolean> builder = new ChainBase<Boolean>();
		Command<Boolean> command = mock(Command.class);
		Boolean sb = true;

		builder.addCommand(command);

		builder.execute(sb);
	}
}
