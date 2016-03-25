package com.crucian.algorithms.search;

public class FindSecondlyElement {

	public static void main(String[] args) {
		int[] elements = new int[] { -1, 0, 5, 3, 9, 10, 130, 11, 4, 6 };
		System.out.println(findSecondBiggest(elements));
	}

	private static int findSecondBiggest(int[] elements) {
		int max;
		int second;
		if (elements.length < 2) {
			return -1;
		}
		if (elements[0] > elements[1]) {
			max = elements[0];
			second = elements[1];
		} else {
			max = elements[1];
			second = elements[0];
		}

		for (int i = 2; i < elements.length; i++) {
			if (elements[i] > max) {
				second = max;
				max = elements[i];
			} else if (elements[i] > second) {
				second = elements[i];
			}
		}
		return second;
	}
}
