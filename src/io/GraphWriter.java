package io;

import model.Graph;

public interface GraphWriter {
    void writeGraph(String filePath, Graph graph) throws Exception;

}
