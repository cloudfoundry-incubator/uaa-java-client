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
package org.cloudfoundry.identity.uaa.api.common.model.expr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Josh Ghiloni
 *
 */
abstract class BinaryOperation<L, R> extends UnaryOperation<L> {
	protected R right;

	protected String operator;

	private static final String ISO_STRING = "yyyy-MM-dd'T'HH:mm:ssXX";
	private static final SimpleDateFormat ISO = new SimpleDateFormat(ISO_STRING);

	/**
	 * @param left
	 * @param right
	 */
	BinaryOperation(L left, R right) {
		super(left);
		this.right = right;
	}

	R getRight() {
		return right;
	}


	@Override
	public String toString() {
		StringBuilder expr = new StringBuilder();
		expr.append(left).append(' ').append(operator).append(' ');

		if (right == null) {
			expr.append("null");
		}
		else if (right instanceof String) {
			expr.append('"').append(right).append('"');
		}
		else if (right instanceof Date) {
			expr.append('"').append(ISO.format((Date) right)).append('"');
		}
		else if (right instanceof Calendar) {
			// preserve timezone
			SimpleDateFormat sdf = new SimpleDateFormat(ISO_STRING);
			sdf.setTimeZone(((Calendar)right).getTimeZone());
			
			expr.append('"').append(sdf.format(((Calendar) right).getTime())).append('"');
		}
		else if ((right instanceof Number) || (right instanceof Boolean) || (right instanceof Operation)) {
			expr.append(right);
		}
		else {
			throw new IllegalArgumentException(String.format("Invalid type %s for RHS", right.getClass().getName()));
		}

		return expr.toString();
	}
}
