package vafilonov.msd.utils;

import java.util.Arrays;

public class CharAccumulator {

    private char[] array;

    private int size;

    public CharAccumulator() {
        this(16);
    }

    public CharAccumulator(int n) {
        array = new char[n];
        size = 0;
    }

    public void append(char[] arr) {
        append(arr, 0, arr.length);
    }

    public void append(char[] arr, int size) {
        append(arr, 0, size);
    }

    final char[] digitIndex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public void appendShort(int num) {
        if (num == 0) {
            append('0');
            return;
        }

        int upperBound = 100000;
        while (num > 0) {
            if (num / upperBound != 0) {
                append(digitIndex[num / upperBound]);
                num = num % upperBound;
            }
            upperBound /= 10;
        }

    }

    public void append(char ch) {
        if (size >= array.length - 1) {
            extend(1);
        }
        array[size++] = ch;
    }

    public void append(char[] arr, int offset, int length) {
        if (arr == null) {
            throw new NullPointerException();
        }
        if (arr.length == 0) {
            return;
        }
        if (offset >= arr.length) {
            throw new IllegalArgumentException("Offset bigger than length.");
        }

        int limit = Math.min(offset + length, arr.length);

        if (limit - offset > array.length - size) {
            extend(limit - offset);
        }

        for (int i = offset; i < limit; i++, size++) {
            array[size] = arr[i];
        }
    }

    public void clear() {
        size = 0;
        int newLen = Math.min(32, array.length);
        array = new char[newLen];
    }

    public void reset(int newSize) {
        array = new char[newSize];
        size = 0;
    }

    public void softReset() {
        size = 0;
    }

    private void extend(int insertionSize) {
        int needed = size + insertionSize;
        int extensionSize = size;
        int newCapacity = needed + extensionSize;
        array = Arrays.copyOf(array, newCapacity);
    }

    private void cut() {
        int newCapacity = array.length / 2;
        array = Arrays.copyOf(array, newCapacity);
    }

    public char[] array() {
        return array;
    }

    public int getSize() {
        return size;
    }

}
