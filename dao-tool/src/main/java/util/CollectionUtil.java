package util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import db.Column;

public class CollectionUtil {

	public static void include(final List<Column> source, final String[] filters) {
		CollectionUtils.filter(source, new Predicate() {
			public boolean evaluate(Object input) {
				return Arrays.asList(filters).contains(
						((Column) input).getName());
			}
		});
	}
	
	public static void exclude(final List<Column> source, final String[] filters) {
		CollectionUtils.filter(source, new Predicate() {
			public boolean evaluate(Object input) {
				return !Arrays.asList(filters).contains(
						((Column) input).getName());
			}
		});
	}	

}
