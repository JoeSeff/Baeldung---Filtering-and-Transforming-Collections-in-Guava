package com.joeseff;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Joeseff
 * @created 16/05/2020 18:26
 *
 * @link https://www.baeldung.com/guava-filter-and-transform-a-collection
 *
 */
public class CollectionTest {

	/**
	 * Get items with "a" in them
	 */
	@Test
	public void whenFilterWithIterablesThenFiltered() {
		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		Iterable<String> result = Iterables.filter(names, Predicates.containsPattern("a"));

		assertThat(result, containsInAnyOrder("Jane", "Adam"));
	}

	/**
	 * Get items with "a" in them
	 */
	@Test
	public void whenFilterWithCollections2ThenFiltered() {
		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		Collection<String> result = Collections2.filter(names, Predicates.containsPattern("a"));

		assertThat(result, hasSize(2));
		assertThat(result, containsInAnyOrder("Jane", "Adam"));

		result.add("Anna");
		assertThat(names, hasSize(5));
	}

	/**
	 * Get items with "a" in them
	 */
	@Test(expected = IllegalArgumentException.class)
	public void givenFilteredCollectionWhenAddingInvalidElementThenIAE() {
		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		Collection<String> result = Collections2.filter(names, Predicates.containsPattern("a"));

		result.add("Elvis");
	}

	/**
	 * Write custom filter predicate
	 */
	@Test
	public void whenFilterCollectionWithCustomPredicateThenFiltered() {
		Predicate<String> predicate = input -> input.startsWith("A") || input.startsWith("J");
		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		Collection<String> result = Collections2.filter(names, predicate);

		assertThat(result, hasSize(3));
		assertThat(result, containsInAnyOrder("John", "Jane", "Adam"));
	}

	/**
	 * Combine multiple predicates
	 */
	@Test
	public void whenFilterUsingMultiplePredicatesThenFiltered() {
		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		Collection<String> result = Collections2.filter(names,
				Predicates.or(
						Predicates.containsPattern("J"),
						Predicates.not(Predicates.containsPattern("a"))
				)
		);

		assertThat(result, hasSize(3));
		assertThat(result, containsInAnyOrder("John", "Jane", "Tom"));
	}

	/**
	 * Remove null values while filtering collection
	 */
	@Test
	public void whenRemoveNullFromCollectionThenRemoved() {
		List<String> names = Lists.newArrayList("John", null, "Jane", null, "Adam", "Tom");
		Collection<String> result = Collections2.filter(names, Predicates.notNull());

		assertThat(result, hasSize(4));
		assertThat(result, containsInAnyOrder("John", "Jane", "Adam", "Tom"));
	}

	/**
	 * Check if all Elements in a Collection Match a Condition
	 */
	@Test
	public void whenCheckingAllElementsMatchAConditionThenCorrect() {
		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");

		boolean result = Iterables.all(names, Predicates.containsPattern("n|m"));
		assertTrue(result);

		result = Iterables.all(names, Predicates.containsPattern("a"));
		assertFalse(result);
	}

	/**
	 * Transform list of Strings to List of Integers
	 */
	@Test
	public void whenTransformWithIterablesThenTransformed() {
		Function<String, Integer> function = input -> input.length();

		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		Iterable<Integer> result = Iterables.transform(names, function);

		assertThat(result, containsInAnyOrder(4, 4, 4, 3));
	}

	/**
	 * Transform list of Strings to List of Integers
	 */
	@Test
	public void whenTransformWithCollections2ThenTransformed() {
		Function<String, Integer> function = s -> s != null ? s.length() : 0;

		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		Collection<Integer> result = Collections2.transform(names, function);

		assertThat(result, hasSize(4));
		assertThat(result, containsInAnyOrder(4, 4, 4, 3));

		result.remove(3);
		assertThat(names, hasSize(3));
	}

	/**
	 * Create Function from Predicate
	 */
	@Test
	public void whenCreatingAFunctionFromAPredicateThenCorrect() {
		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		Collection<Boolean> results = Collections2.transform(names,
				Functions.forPredicate(Predicates.containsPattern("m")));

		assertThat(results, hasSize(4));
		assertThat(results, containsInAnyOrder(false, false, true, true));
	}

	/**
	 * Composition of two Functions
	 */
	@Test
	public void whenTransformUsingComposedFunctionThenTransformed() {
		Function<String, Integer> func1 = String::length;
		Function<Integer, Boolean> func2 = input -> input % 2 == 0;

		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		Collection<Boolean> results = Collections2.transform(names, Functions.compose(func2, func1));

		assertThat(results, hasSize(4));
		assertThat(results, contains(true, true, true, false));
	}

	/**
	 * Combine Filtering and Transforming
	 */
	@Test
	public void whenFilteringAndTransformingCollectionThenCorrect() {
		Predicate<String> predicate = input -> input != null ? input.startsWith("A") || input.startsWith("T") : null;
		Function<String, Integer> func = String::length;

		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		Collection<Integer> results = FluentIterable.from(names)
				.filter(predicate)
				.transform(func)
				.toList();

		assertThat(results, hasSize(2));
		assertThat(results, containsInAnyOrder(4, 3));
	}
}
