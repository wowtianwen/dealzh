package com.junhong.forum.entity;

import java.lang.reflect.Field;
import java.util.Comparator;

import org.primefaces.model.SortOrder;

public class ThreadSorter implements Comparator<ForumThread> {

	private String		sortField;

	private SortOrder	sortOrder;

	public ThreadSorter(String sortField, SortOrder sortOrder) {
		this.sortField = sortField;
		this.sortOrder = sortOrder;
	}

	public int compare(ForumThread thread1, ForumThread thread2) {
		try {
			Field field = getField(ForumThread.class, this.sortField);
			Object value1 = field.get(thread1);
			Object value2 = field.get(thread2);
			int value;
			if (value1 == null && value2 == null) {
				value = 0;

			} else if (value1 == null && value2 != null) {
				value = -1;

			} else if (value1 != null && value2 == null) {
				value = 1;

			} else {
				value = ((Comparable) value1).compareTo(value2);
			}
			return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

	}

	private Field getField(Class<?> clazz, String fieldName) {
		Class<?> tmpClass = clazz;
		Field result = null;
		do {
			for (Field field : tmpClass.getDeclaredFields()) {
				String candidateName = field.getName();
				if (!candidateName.equals(fieldName)) {
					continue;
				}
				field.setAccessible(true);
				result = field;
				return result;

			}
			tmpClass = tmpClass.getSuperclass();
		} while (clazz != null);
		return result;

	}

}
