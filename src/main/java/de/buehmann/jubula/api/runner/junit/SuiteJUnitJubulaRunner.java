package de.buehmann.jubula.api.runner.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import de.buehmann.jubula.api.runner.JubulaRunner;

/**
 * The JUnit Jubula runner, which can be used in JUnit annotation {@code @}
 * {@link org.junit.runner.RunWith}.
 * 
 * It needs an annotation like
 * {@link de.buehmann.jubula.api.runner.annotations.ClassAUT} to configure the
 * AUT. Optionally use {@code @}
 * {@link de.buehmann.jubula.api.runner.annotations.OnTestFailure} to change the
 * default behavior, if an assert method fails.
 */
public class SuiteJUnitJubulaRunner extends BlockJUnit4ClassRunner {

	private final JubulaRunner jubulaRunner;

	/**
	 * @param testClass
	 *            The JUnit test class.
	 * @throws InitializationError
	 */
	public SuiteJUnitJubulaRunner(final Class<?> testClass) throws InitializationError {
		super(testClass);
		jubulaRunner = new JubulaRunner(testClass);
	}

	@Override
	public void run(final RunNotifier notifier) {
		new JUnitRunListener(jubulaRunner);
		jubulaRunner.start();
		super.run(notifier);
		jubulaRunner.stop();
	}

}
