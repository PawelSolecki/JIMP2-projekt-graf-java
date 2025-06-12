package model;

public class Node {
    private final int index;
    private final int row;
    private final int column;
    private int partition = 0; // Default partition is 0

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

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }
}