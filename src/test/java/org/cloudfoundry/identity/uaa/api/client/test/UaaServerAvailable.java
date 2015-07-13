/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cloudfoundry.identity.uaa.api.client.test;

import java.io.IOException;
import java.net.Socket;

import org.junit.Assume;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Thomas Darimont
 */
public class UaaServerAvailable implements TestRule {

	private static final boolean UAA_RUNNING;

	private static final String UAA_TEST_HOST = System.getProperty("test.uaa.server.hostname", "localhost");
	private static final int UAA_TEST_PORT = Integer.getInteger("test.uaa.server.port", 8080);

	static {

		boolean serverAvailable = false;
		try {
			Socket test = new Socket(UAA_TEST_HOST, UAA_TEST_PORT);
			serverAvailable = true;
			test.close();
		} catch (IOException e) {
			System.err.println("UAA is not running, skip these tests");
		}

		UAA_RUNNING = serverAvailable;
	}

	private static final Statement NOT_AVAILABLE_STATEMEMT = new Statement() {
		@Override
		public void evaluate() throws Throwable {
			Assume.assumeTrue(
					String.format("Skipping test due to UAA not being available @: %s:%s", UAA_TEST_HOST, UAA_TEST_PORT), false);
		}
	};

	@Override
	public Statement apply(final Statement base, Description description) {
		return UAA_RUNNING ? new Statement() {

			@Override
			public void evaluate() throws Throwable {
				base.evaluate();
			}
		} : NOT_AVAILABLE_STATEMEMT;
	}

}
