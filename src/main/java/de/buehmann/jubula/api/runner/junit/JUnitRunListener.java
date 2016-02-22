package de.buehmann.jubula.api.runner.junit;

import org.eclipse.jubula.client.Result;
import org.eclipse.jubula.client.exceptions.CheckFailedException;
import org.eclipse.jubula.client.exceptions.ExecutionException;
import org.eclipse.jubula.client.exceptions.ExecutionExceptionHandler;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.MessageParam;
import org.junit.Assert;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import de.buehmann.jubula.api.runner.JubulaRunner;

public class JUnitRunListener extends RunListener implements ExecutionExceptionHandler {

	private final JubulaRunner jubulaRunner;

	public JUnitRunListener(final JubulaRunner jubulaRunner) {
		this.jubulaRunner = jubulaRunner;
		jubulaRunner.setExceptionHandler(this);
	}

	@Override
	public void testAssumptionFailure(final Failure failure) {
		jubulaRunner.doTestFailureAction();
	}

	@Override
	public void testFailure(final Failure failure) {
		jubulaRunner.doTestFailureAction();
	}

	@Override
	public void handle(final ExecutionException e) throws ExecutionException {
		if (e instanceof CheckFailedException) {
			final CheckFailedException fail = (CheckFailedException) e;
			final Result<?> result = fail.getResult();
			final Object payloadObject = result.getPayload();
			final String payload;
			if (payloadObject instanceof String) {
				payload = (String) payloadObject;
			} else {
				payload = "";
			}
			String message = payload;
			String expected = null;
			final CAP capObject = result.getCAP();
			if (payload.endsWith("text equals") && capObject instanceof MessageCap) {
				final MessageCap cap = (MessageCap) capObject;
				message += " at " + cap.getCi().getComponentName();
				if (!cap.getMessageParams().isEmpty()) {
					final MessageParam messageParam = cap.getMessageParams().get(0);
					if (String.class.getCanonicalName().equals(messageParam.getType())) {
						expected = messageParam.getValue();
					}
				}
			}
			if (null != expected) {
				if (payload.endsWith("text equals")) {
					Assert.assertEquals(message, expected, fail.getActualValue());
				} else if (payload.endsWith("text not equals")) {
					Assert.assertNotEquals(message, expected, fail.getActualValue());
				}
			}
			Assert.fail("Actual value not expected for " + message + ": \"" + fail.getActualValue() + "\"");
		}
	}

}
