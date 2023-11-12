package com.example.mpcadmin.graphClass;

public class Node {
    public long id_writing;
    public int id_previous;
    public String text_writing;

    public Node(long id, Integer id_previous, String text_writing) {
        this.id_writing = id;
        this.id_previous = id_previous;
        this.text_writing = text_writing;
    }
}
