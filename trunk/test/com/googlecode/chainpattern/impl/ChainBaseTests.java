package com.googlecode.chainpattern.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.verification.Times;

import com.googlecode.chainpattern.Chain;
import com.googlecode.chainpattern.Command;

public class ChainBaseTests {
	ChainBase<Boolean> chain;
	private Command<Boolean> command1;
	private Command<Boolean> command2;
	private Boolean context;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		chain = new ChainBase<Boolean>();
		command1 = mock(Command.class);
		command2 = mock(Command.class);
		context = true;
	}

	@Test
	public void noItemsInTheChainThenContinueProcessing() throws Exception {
		assertEquals(Chain.CONTINUE_PROCESSING, chain.execute(true));
	}

	@Test
	public void singleItemThatWantsToKeepGoing() throws Exception {
		chain.addCommand(command1);

		when(command1.execute(true)).thenReturn(Chain.CONTINUE_PROCESSING);
		assertEquals(Chain.CONTINUE_PROCESSING, chain.execute(context));

		verify(command1).execute(true);
	}

	@Test
	public void singleItemThatSaysItIsComplete() throws Exception {
		chain.addCommand(command1);

		when(command1.execute(true)).thenReturn(Chain.PROCESSING_COMPLETE);
		assertEquals(Chain.PROCESSING_COMPLETE, chain.execute(context));

		verify(command1).execute(true);
	}

	@Test
	public void twoItemsBothSayTheyCanContinueGoing() throws Exception {
		chain.addCommand(command1);
		chain.addCommand(command2);

		when(command1.execute(true)).thenReturn(Chain.CONTINUE_PROCESSING);
		when(command1.execute(true)).thenReturn(Chain.CONTINUE_PROCESSING);
		assertEquals(Chain.CONTINUE_PROCESSING, chain.execute(context));

		verify(command1).execute(true);
		verify(command2).execute(true);
	}

	@Test(expected = IllegalStateException.class)
	public void addCannotBeCalledAfterExecute() throws Exception {
		chain.addCommand(command1);

		when(command1.execute(true)).thenReturn(Chain.CONTINUE_PROCESSING);
		assertEquals(Chain.CONTINUE_PROCESSING, chain.execute(context));
		chain.addCommand(command2);
	}

	@Test(expected = Exception.class)
	public void singleCommandThrowsExceptionItGetsReThrown() throws Exception {
		chain.addCommand(command1);
		Exception e = new Exception();
		when(command1.execute(true)).thenThrow(e);
		chain.execute(context);
	}

	@Test
	public void twoCommandsOnlyTheFirstOneThrowsMakeSureThatTheSecondDoesntGetExecuted() throws Exception {
		chain.addCommand(command1);
		chain.addCommand(command2);
		Exception e = new Exception();
		when(command1.execute(true)).thenThrow(e);
		when(command2.execute(true)).thenReturn(Chain.CONTINUE_PROCESSING);
		try {
			chain.execute(context);
			fail("Should have thrown");
		} catch (Exception ex) {
			assertSame(e, ex);
		}

		verify(command2, new Times(0)).execute(true);
	}
}
