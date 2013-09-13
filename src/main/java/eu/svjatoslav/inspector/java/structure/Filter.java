package eu.svjatoslav.inspector.java.structure;

import java.util.ArrayList;
import java.util.List;

import eu.svjatoslav.commons.string.WildCardMatcher;

public class Filter {

	/**
	 * This class implements filter of classes that will be included or excluded
	 * from resulting graph.
	 * 
	 * Filtering is done by lists of whitelist and blacklist patterns using
	 * wildcards.
	 * 
	 * Filtering logic is such that if at least single whitelist entry is
	 * defined then every class that is not whitelisted is automatically
	 * excluded from graph.
	 * 
	 * Otherwise every class in included in graph that is not blacklisted.
	 */

	private static final List<String> blacklistClassPatterns = new ArrayList<String>();

	private static final List<String> whitelistClassPatterns = new ArrayList<String>();

	public void blacklistClassPattern(final String pattern) {
		blacklistClassPatterns.add(pattern);
	}

	public boolean isClassShown(final String className) {
		for (final String pattern : blacklistClassPatterns)
			if (WildCardMatcher.match(className, pattern))
				return false;

		if (!whitelistClassPatterns.isEmpty()) {
			for (final String pattern : whitelistClassPatterns)
				if (WildCardMatcher.match(className, pattern))
					return true;
			return false;
		}

		return true;
	}

	public void whitelistClassPattern(final String pattern) {
		whitelistClassPatterns.add(pattern);
	}

}
