package org.itmo.bwed;

//Element of array used for Burrows–Wheeler transform
public class BWElement {

    public BWElement() {
    }

    public BWElement(char first, char last, int shift, CharArray beginning, String file) {
        this.first = first;
        this.last = last;
        this.shift = shift;
        this.beginning = beginning;
        this.file = file;
    }

    //first character of cycled string
    private char first;

    //last character of cycled string
    private char last;

    //number of cycle
    private int shift;

    //current string beginning used for string sorting
    private CharArray beginning;

    private String file;

    public String beginningToString() {
        return file.substring(shift) + file.substring(0, shift);
    }

    public char getFirst() {
        return first;
    }

    public void setFirst(char first) {
        this.first = first;
    }

    public char getLast() {
        return last;
    }

    public void setLast(char last) {
        this.last = last;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public CharArray getBeginning() {
        return beginning;
    }

    public void setBeginning(CharArray beginning) {
        this.beginning = beginning;
    }
}
