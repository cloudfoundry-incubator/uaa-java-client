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

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * A class used to create <a href="http://www.simplecloud.info">SCIM</a> filter strings. All methods (except
 * {@link #build()} and {@link #showAll()}) return the object, so chaining can be employed. By default, conditions are
 * joined with a logical <code>and</code>, but can be joined with <code>or</code> by passing <code>false</code> to the
 * constructor. Operations can be joined in prefix manner; that is,
 * 
 * <pre>
 * builder.present(&quot;mykey&quot;).lessThan(&quot;mykey&quot;, 10).greaterThanOrEquals(&quot;mykey&quot;, 50).or().and().build()
 * </pre>
 * 
 * Will generate a filter string <code>"mykey pr and mykey lt 10 or mykey ge 50"</code>. To group using parentheses, use
 * the {@link #precedence()} operator. For example, the previous filter could look like this:
 * 
 * <pre>
 * builder.present(&quot;mykey&quot;).lessThan(&quot;mykey&quot;, 10).greaterThanOrEquals(&quot;mykey&quot;, 50).or().precedence().and().build()
 * </pre>
 * 
 * and would generate a filter string <code>"mykey pr and (mykey lt 10 or mykey ge 50)"</code>.
 * 
 * Note that all values are quoted according to SCIM standards (that is, Strings are wrapped in double quotes and Dates
 * are double quoted and rendered in ISO 8601 format)
 * 
 * @author Josh Ghiloni
 *
 */
public class FilterRequestBuilder {

	private boolean defaultAnd = true;

	private Stack<Operation> opStack = new Stack<Operation>();

	private List<String> attributes = null;

	private int start = 0;

	private int count = 0;

	private boolean built = false;

	/**
	 * Create a new builder, joining operations with "and"
	 */
	public FilterRequestBuilder() {
		this(true);
	}

	/**
	 * Create a new builder
	 * @param defaultAnd if true, join operations with "and", otherwise "or"
	 */
	public FilterRequestBuilder(boolean defaultAnd) {
		this.defaultAnd = defaultAnd;
	}

	/**
	 * Creates an operation that renders as <code>key eq val</code>
	 * @param key the key to check for comparison
	 * @param val the value to check for comparison
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder equals(String key, Object val) {
		verifyActive();
		opStack.push(new EqualsOperation(key, val));
		return this;
	}

	/**
	 * Creates an operation that renders as <code>key lt val</code>
	 * @param key the key to check for comparison
	 * @param val the value to check for comparison
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder lessThan(String key, Object val) {
		verifyActive();
		opStack.push(new LessThanOperation(key, val));
		return this;
	}

	/**
	 * Creates an operation that renders as <code>key gt val</code>
	 * @param key the key to check for comparison
	 * @param val the value to check for comparison
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder greaterThan(String key, Object val) {
		verifyActive();
		opStack.push(new GreaterThanOperation(key, val));
		return this;
	}

	/**
	 * Creates an operation that renders as <code>key le val</code>
	 * @param key the key to check for comparison
	 * @param val the value to check for comparison
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder lessThanOrEquals(String key, Object val) {
		verifyActive();
		opStack.push(new LessEqualOperation(key, val));
		return this;
	}

	/**
	 * Creates an operation that renders as <code>key ge val</code>
	 * @param key the key to check for comparison
	 * @param val the value to check for comparison
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder greaterThanOrEquals(String key, Object val) {
		verifyActive();
		opStack.push(new GreaterEqualOperation(key, val));
		return this;
	}

	/**
	 * Creates an operation that renders as <code>key sw "val"</code>
	 * @param key the key to check for comparison
	 * @param val the value to check for comparison (must be String)
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder startsWith(String key, String val) {
		verifyActive();
		opStack.push(new StartsWithOperator(key, val));
		return this;
	}

	/**
	 * Creates an operation that renders as <code>key co "val"</code>
	 * @param key the key to check for comparison
	 * @param val the value to check for comparison (must be String)
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder contains(String key, String val) {
		verifyActive();
		opStack.push(new ContainsOperator(key, val));
		return this;
	}

	/**
	 * Creates an operation that renders as <code>key pr</code>
	 * @param key the key to check for comparison
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder present(String key) {
		verifyActive();
		opStack.push(new PresentOperator(key));
		return this;
	}

	/**
	 * Pops the most recent two expressions off the expression stack and joins them as <code>expr1 and expr2</code>
	 * 
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if there are fewer than two expressions defined or if {@link #build()} has already
	 * been called
	 */
	public FilterRequestBuilder and() {
		verifyActive();

		if (opStack.size() < 2) {
			throw new IllegalStateException("need at least two operations to join");
		}

		Operation second = opStack.pop();
		Operation first = opStack.pop();

		opStack.push(new AndOperator(first, second));
		return this;
	}

	/**
	 * Pops the most recent two expressions off the expression stack and joins them as <code>expr1 or expr2</code>
	 * 
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if there are fewer than two expressions defined or if {@link #build()} has already
	 * been called
	 */
	public FilterRequestBuilder or() {
		verifyActive();

		if (opStack.size() < 2) {
			throw new IllegalStateException("need at least two operations to join");
		}

		Operation second = opStack.pop();
		Operation first = opStack.pop();

		opStack.push(new OrOperator(first, second));
		return this;
	}

	/**
	 * Pops the most current expression off the stack and wraps it in parentheses. Note that if it already is wrapped in
	 * parentheses, nothing will change.
	 * 
	 * @return the builder object (for chaining)
	 * @throws IllegalStateException if there are no expressions defined or if {@link #build()} has already been called
	 */
	public FilterRequestBuilder precedence() {
		verifyActive();
		if (opStack.isEmpty()) {
			throw new IllegalStateException("need an operation to set precedence");
		}

		Operation op = opStack.peek();

		if (!(op instanceof PrecedenceOperator)) {
			opStack.pop();
			opStack.push(new PrecedenceOperator(op));
		}

		return this;
	}

	/**
	 * Sets the attributes to be returned by this request
	 * 
	 * @param attributes the attributes to be returned. If null or empty, this method is a no-op
	 * @return The builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder attributes(String... attributes) {
		verifyActive();

		if (attributes != null && attributes.length > 0) {
			this.attributes = Arrays.asList(attributes);
		}

		return this;
	}

	/**
	 * Set the start index. Note that if this value is &lt;= 1, this is a no-op
	 * @param start The index
	 * @return The builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder start(int start) {
		verifyActive();
		this.start = start;
		return this;
	}

	/**
	 * Set the page size. Note that if this value is &lt;= 1, this is a no-op
	 * @param count The page size
	 * @return The builder object (for chaining)
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequestBuilder count(int count) {
		verifyActive();
		this.count = count;
		return this;
	}

	/**
	 * A convenience method to return a {@link FilterRequest} that allows all items to be returned
	 * @return a {@link FilterRequest}
	 */
	public static FilterRequest showAll() {
		return FilterRequest.SHOW_ALL;
	}

	/**
	 * Build the {@link FilterRequest} to be used by a SCIM API call. Once this method is called, subsequent method
	 * calls will result in an {@link IllegalStateException}
	 * 
	 * @return the {@link FilterRequest} built by this builder
	 * @throws IllegalStateException if {@link #build()} has already been called
	 */
	public FilterRequest build() {
		verifyActive();

		Operation filter = joinAll();
		built = true;

		return new FilterRequest(filter.toString(), attributes, start, count);
	}

	private Operation joinAll() {
		if (opStack.isEmpty()) {
			return NullOperation.INSTANCE;
		}

		if (opStack.size() == 1) {
			return opStack.pop();
		}

		while (opStack.size() > 1) {
			Operation second = opStack.pop();
			Operation first = opStack.pop();

			Operation joined = defaultAnd ? new AndOperator(first, second) : new OrOperator(first, second);
			opStack.push(joined);
		}

		return opStack.pop();
	}

	private void verifyActive() {
		if (built) {
			throw new IllegalStateException("Builder already built");
		}
	}
}