package org.itmo.bwed;

//Element of array used for Burrows–Wheeler transform
public class BWElement implements Comparable<BWElement> {

    public static final int COMPARISON_SHIFT = 1000;

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

    public String getFullString() {
        return file.substring(shift) + file.substring(0, shift);
    }

    @Override
    public int compareTo(BWElement o) {
        String s1 = "";
        String s2 = "";
        int currentShift = COMPARISON_SHIFT;
        int shift2 = o.getShift();
        while(currentShift < file.length()) {
            if (shift + currentShift < file.length()) {
                s1 = (file.substring(shift, shift + currentShift));
            } else {
                s1 = (file.substring(shift) + file.substring(0, shift + currentShift - file.length()));
            }
            if (shift2 + currentShift < file.length()) {
                s2 = (file.substring(shift2, shift2 + currentShift));
            } else {
                s2 = (file.substring(shift2) + file.substring(0, shift2 + currentShift - file.length()));
            }
            currentShift += COMPARISON_SHIFT;
            if (s1.compareTo(s2) != 0) {
                return s1.compareTo(s2);
            }
        }
        return getFullString().compareTo(o.getFullString());
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
