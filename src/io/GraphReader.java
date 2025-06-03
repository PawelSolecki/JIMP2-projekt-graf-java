package io;

import model.Graph;

public interface GraphReader {

    Graph readGraph(String filePath) throws Exception;
}
