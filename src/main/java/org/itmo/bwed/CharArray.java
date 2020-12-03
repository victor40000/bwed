package org.itmo.bwed;

import java.util.Arrays;

public class CharArray {

    private char[] array;

    public CharArray add(char c) {
        char[] newArray = new char[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = c;
        array = newArray;
        return new CharArray(array);
    }

    public char get(int i) {
        return array[i];
    }

    public CharArray getSubArray(int start, int end) {
        char[] result = new char[end - start];
        System.arraycopy(array, start, result, 0, end - start);
        return new CharArray(result);
    }

    public int size() {
        return array.length;
    }

    public CharArray() {
        this.array = new char[]{};
    }

    public CharArray(char[] array) {
        this.array = array;
    }

    public char[] getArray() {
        return array;
    }

    public void setArray(char[] array) {
        this.array = array;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharArray charArray = (CharArray) o;
        return Arrays.equals(array, charArray.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }
}
