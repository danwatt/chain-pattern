package com.googlecode.chainpattern.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.verification.Times;

import com.googlecode.chainpattern.Chain;
import com.googlecode.chainpattern.Command;
import com.googlecode.chainpattern.Filter;

public class ChainBaseTests {
	ChainBase<Boolean> chain;
	private Command<Boolean> command1;
	private Command<Boolean> command2;
	private Filter<Boolean> filterCommand1;
	private Filter<Boolean> filterCommand2;
	private Boolean context;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		chain = new ChainBase<Boolean>();
		command1 = mock(Command.class);
		command2 = mock(Command.class);
		filterCommand1 = mock(Filter.class);
		filterCommand2 = mock(Filter.class);
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

	@Test
	public void singleFilterWithNoExceptions() throws Exception {
		chain.addCommand(filterCommand1);
		when(filterCommand1.execute(true)).thenReturn(Chain.CONTINUE_PROCESSING);
		when(filterCommand1.postprocess(true, null)).thenReturn(false);

		chain.execute(true);

		verify(filterCommand1).execute(true);
		verify(filterCommand1).postprocess(true, null);
	}

	@Test
	public void filterExceptionsAreDiscarded() throws Exception {
		chain.addCommand(filterCommand1);
		when(filterCommand1.execute(true)).thenReturn(Chain.CONTINUE_PROCESSING);
		when(filterCommand1.postprocess(true, null)).thenThrow(new RuntimeException());

		assertEquals(Chain.CONTINUE_PROCESSING, chain.execute(true));

		verify(filterCommand1).execute(true);
		verify(filterCommand1).postprocess(true, null);
	}

	@Test
	public void handledExceptionsResultsPassBackTheResultOfLastStageToBeProcessed() throws Exception {
		chain.addCommand(filterCommand1);
		Exception e = new Exception();
		when(filterCommand1.execute(true)).thenThrow(e);
		when(filterCommand1.postprocess(true, e)).thenReturn(true);

		assertEquals(Chain.CONTINUE_PROCESSING, chain.execute(true));

		verify(filterCommand1).execute(true);
		verify(filterCommand1).postprocess(true, e);
	}

	@Test(expected = Exception.class)
	public void exceptionnotHandledByPostProcessSoRethrow() throws Exception {
		chain.addCommand(filterCommand1);
		Exception e = new Exception();
		when(filterCommand1.execute(true)).thenThrow(e);
		when(filterCommand1.postprocess(true, e)).thenReturn(false);

		chain.execute(true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullCommandsAreNotAllowed() throws Exception {
		chain.addCommand(null);
	}

	@Test
	public void copyOfCommands() {
		chain.addCommand(command1);
		assertEquals(1, chain.getCopyOfCommands().size());
		assertTrue(chain.getCopyOfCommands().contains(command1));
	}

	@Test(expected = Exception.class)
	public void returnedCopyOfCommandsIsImmutable() {
		chain.addCommand(command1);
		chain.getCopyOfCommands().add(command2);
	}

}
