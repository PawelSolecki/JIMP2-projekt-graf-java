package io;

import model.Graph;

import java.util.List;

public interface GraphReader {

    List<Graph> readGraph(String filePath) throws Exception;
}
