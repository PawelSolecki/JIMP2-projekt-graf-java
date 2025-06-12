package io;

import model.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GraphReaderCSRRG implements GraphReader {

    private final GraphUtils graphUtils = new GraphUtils();
    @Override
    public List<Graph> readGraph(String filePath) throws Exception {
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
            List<Integer> firstElementInGroup = new ArrayList<>();

            // koniec pliku lub pusta linia — przerwij
            while (true) {
                lineNum++;  // najpierw zwiększ lineNum jawnie
                line = nextLine(reader, lineNum);  // potem wywołaj nextLine

                if (line == null || line.trim().isEmpty()) {
                    break;
                }

                firstElementInGroup.addAll(parseSemicolonSeparatedInts(line, lineNum));
            }
            firstElementInGroup.add(connectedNodes.size());

            return graphUtils.separateGraphs(graphUtils.build(numCols, vertices, verticesRow, connectedNodes, firstElementInGroup));


        } catch (IOException | NumberFormatException e) {
            System.out.println("Error while reading graph: " + e.getMessage());
            throw new Exception("Error while reading graph: " + e.getMessage(), e);
        }
    }



    private String nextLine(BufferedReader reader, int lineNum) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
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

