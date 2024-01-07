package util;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

public class Util {
	public static boolean isNullOrEmpty(Collection<String> src) {
		if (src == null || src.size() == 0) return true;
		
		Collection<String> filteredSrc = src.stream().filter(s -> !Strings.isNullOrEmpty(s)).collect(Collectors.toList());
		if (filteredSrc.size() == 0) return true;
		
		if (src.size() != filteredSrc.size()) return true;
		
		return false;
	}
}
