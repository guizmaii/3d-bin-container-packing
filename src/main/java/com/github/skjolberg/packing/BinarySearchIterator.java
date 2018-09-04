package com.github.skjolberg.packing;

class BinarySearchIterator {

	private final int low;
	private final int high;
	private final int mid;
	
	BinarySearchIterator(int low, int high) {
        this.low = low;
        this.high = high;
        this.mid = low + (high - low) / 2;
    }

	BinarySearchIterator lower() {
	    return new BinarySearchIterator(low, mid - 1);
	}

	BinarySearchIterator higher() {
        return new BinarySearchIterator(mid + 1, high);
	}

	boolean hasNext() {
		return low <= high;
	}

    int getMid() {
        return mid;
    }
}
