package io;

import model.Graph;
import model.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphReaderCSRRG implements GraphReader {
    @Override
    public Graph readGraph(String filePath) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int lineNum = 0;

            // Pierwsza linia: liczba kolumn
            String line = nextLine(reader, ++lineNum);
            int numCols = parseSingleInt(line, lineNum);
            Graph graph = new Graph(numCols);

            // 2 linia: Wierzchołki
            line = nextLine(reader, ++lineNum);
            List<Integer> vertices = parseSemicolonSeparatedInts(line, lineNum);

            // 3 linia: Wskaźniki wierszy
            line = nextLine(reader, ++lineNum);
            List<Integer> verticesRow = parseSemicolonSeparatedInts(line, lineNum);

            // 4 linia: Grupy poloczonych wezlow
            line = nextLine(reader, ++lineNum);
            List<Integer> connectedNodes = parseSemicolonSeparatedInts(line, lineNum);

            // 5 linia: wskazniki na pierwszy element grupy
            line = nextLine(reader, ++lineNum);
            List<Integer> firstElementInGroup = parseSemicolonSeparatedInts(line, lineNum);
            firstElementInGroup.add(connectedNodes.size());



//            1;3;
//            1;5;3;2;6;0;3;6
//            0;2;2;4;5;5;7;10
            int indexNumber = 0;
            for (int i = 0; i < verticesRow.size() - 1; i++) {
                for (int j = verticesRow.get(i); j < verticesRow.get(i + 1); j++) {
                    graph.addNode(new Node(indexNumber, i, vertices.get(j)));
                    indexNumber++;

                }

            }

            for (int i = 0; i < firstElementInGroup.size() -1; i++) {
                for (int j = firstElementInGroup.get(i); j <firstElementInGroup.get(i+1) ; j++) {
                    graph.addEdge(connectedNodes.get(firstElementInGroup.get(i)), connectedNodes.get(j));
                }
            }

            return graph;

        } catch (IOException | NumberFormatException e) {
            throw new Exception("Error while reading graph: " + e.getMessage(), e);
        }
    }

    private String nextLine(BufferedReader reader, int lineNum) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Unexpected end of file at line " + lineNum);
        }
        return line.trim();
    }

    private int parseSingleInt(String line, int lineNum) {
        try {
            return Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid integer at line " + lineNum + ": " + line);
        }
    }

    private List<Integer> parseSemicolonSeparatedInts(String line, int lineNum) {
        if (line.trim().isEmpty()) return new ArrayList<>();
        String[] tokens = line.split(";");
        List<Integer> result = new ArrayList<>();

        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;

            try {
                int value = Integer.parseInt(token);
                result.add(value);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Invalid number at line " + lineNum + ": '" + token + "'");
            }
        }

        return result;
    }
}

