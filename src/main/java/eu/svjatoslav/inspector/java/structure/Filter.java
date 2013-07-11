package eu.svjatoslav.inspector.java.structure;

import java.util.ArrayList;
import java.util.List;

import eu.svjatoslav.commons.string.WildCardMatcher;

public class Filter {

	private static final List<String> blacklistedClasses = new ArrayList<String>();

	private static final List<String> whitelistedClasses = new ArrayList<String>();

	public void blacklistClassPattern(final String pattern) {
		blacklistedClasses.add(pattern);
	}

	public boolean isClassShown(final String className) {
		for (final String pattern : blacklistedClasses)
			if (WildCardMatcher.match(className, pattern))
				return false;

		if (!whitelistedClasses.isEmpty()) {
			for (final String pattern : whitelistedClasses)
				if (WildCardMatcher.match(className, pattern))
					return true;
			return false;
		}

		return true;
	}

	public void whitelistClassPattern(final String pattern) {
		whitelistedClasses.add(pattern);
	}

}
