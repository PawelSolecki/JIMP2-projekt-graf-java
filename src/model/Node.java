package model;

public class Node {
    private final int index;
    private final int row;
    private final int column;

    public Node(int index, int row, int column) {
        this.index = index;
        this.row = row;
        this.column = column;
    }

    public int getIndex() {
        return index;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

}