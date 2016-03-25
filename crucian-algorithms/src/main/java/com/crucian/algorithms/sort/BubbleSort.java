package com.crucian.algorithms.sort;

public class BubbleSort {
	public static void main(String[] args) {
		int[] elements = new int[] { -1, 0, 5, 3, 9, 10, 130, 11, 4, 6 };
		bubbleSort(elements);
		for (int i = 0; i < elements.length; i++) {
			System.out.print(elements[i] + ",");
		}
	}

	public static void bubbleSort(int[] elements) {
		boolean exchange;
		for (int i = 0; i < elements.length; i++) {
			int temp = 0;
			exchange = false;
			for (int j = elements.length - 1; j > i; j--) {
				if (elements[j] < elements[j - 1]) {
					temp = elements[j];
					elements[j] = elements[j - 1];
					elements[j - 1] = temp;
					exchange = true;
				}
			}
			if (!exchange) {
				break;
			}
		}
	}

}
