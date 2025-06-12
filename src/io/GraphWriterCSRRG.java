package io;

import model.Graph;
import model.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GraphWriterCSRRG implements GraphWriter {

    /**
     * Zapisuje graf do pliku w następującej strukturze:
     * 1. Pierwsza linia: liczba kolumn (numCols)
     * 2. Druga linia: lista wartośc w polu "column" dla każdego węzła (vertices),
     * uporządkowanych wg rosnącego numeru wiersza.
     * 3. Trzecia linia: lista indeksów początkowych kolejnych wierszy (verticesRow),
     * gdzie pierwszy element to 0, a kolejne to skumulowana liczba węzłów do danej grupy.
     * 4. Czwarta linia: spłaszczona lista węzłów wykorzystywanych przy dodawaniu krawędzi (connectedNodes).
     * 5. Piąta linia (opcjonalnie): lista indeksów w connectedNodes, które wyznaczają początek grup krawędzi (firstElementInGroup).
     * <p>
     * W części dotyczącej krawędzi – dla każdego węzła posiadającego sąsiadów – tworzymy grupę,
     * gdzie zapisywany jest najpierw indeks węzła (kluczowej pozycji) oraz kolejno indeksy jego sąsiadów.
     * Podczas odczytu, metoda GraphReaderCSRRG iteruje po tych grupach i dodaje krawędź od
     * connectedNodes.get(początek grupy) do każdego elementu danej grupy.
     */
    @Override
    public void writeGraph( String filePath,Graph graph) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            // 1. Zapisz liczbę kolumn.
            writer.write(String.valueOf(graph.getNumCols()));
            writer.newLine();

            /*
             * 2. Przygotowanie list vertices i verticesRow.
             * Zakładamy, że graf przechowuje węzły w mapie nodesByIndex.
             * Aby uzyskać oryginalny porządek, grupujemy węzły według numeru wiersza.
             */
            Collection<Node> allNodes = graph.getNodes();
            // Przepisz do listy i posortuj wg rosnącego numeru wiersza, a w ramach jednego wiersza wg indeksu.
            List<Node> nodes = new ArrayList<>(allNodes);
            nodes.sort(Comparator.comparingInt(Node::getRow).thenComparingInt(Node::getIndex));

            // Grupujemy węzły wg numeru wiersza.
            Map<Integer, List<Node>> nodesByRow = new TreeMap<>();
            for (Node node : nodes) {
                nodesByRow.computeIfAbsent(node.getRow(), k -> new ArrayList<>()).add(node);
            }
            List<Integer> vertices = new ArrayList<>();      // lista kolumn (dla każdego węzła)
            List<Integer> verticesRow = new ArrayList<>();     // lista offsetów początku wierszy
            verticesRow.add(0);
            int runningCount = 0;
            for (Map.Entry<Integer, List<Node>> entry : nodesByRow.entrySet()) {
                // Można dodatkowo posortować w ramach jednego wiersza, np. wg indeksu lub kolumny.
                List<Node> rowNodes = entry.getValue();
                rowNodes.sort(Comparator.comparingInt(Node::getIndex));
                for (Node node : rowNodes) {
                    vertices.add(node.getColumn());
                    runningCount++;
                }
                verticesRow.add(runningCount);
            }

            // 3. Zapisz listy vertices i verticesRow (połączone średnikiem).
            writer.write(joinList(vertices));
            writer.newLine();
            writer.write(joinList(verticesRow));
            writer.newLine();

            /*
             * 4. Przygotowanie części odpowiadającej krawędziom:
             * Aby „odwrócić” działanie GraphBuildera, dla KAŻDEGO węzła posiadającego sąsiadów
             * tworzymy grupę – w postaci listy gdzie:
             *   - pierwszy element to indeks tego węzła (oznaczający źródło),
             *   - kolejne elementy to indeksy jego sąsiadów.
             *
             * Podczas odczytu, dla każdej grupy iterujemy od początkowego indeksu (odczytanego z firstElementInGroup)
             * aż do następnego – i dodajemy krawędź od źródła do każdego elementu z grupy.
             */
            // Przyjmujemy, że graf udostępnia metodę getNeighbors(Node) zwracającą listę sąsiadów.
            List<Node> nodesWithEdges = new ArrayList<>();
            for (Node node : nodes) {
                List<Node> neighbors = graph.getNeighbors(node);
                if (neighbors != null && !neighbors.isEmpty()) {
                    nodesWithEdges.add(node);
                }
            }
            // Ustalamy porządek według rosnącego indeksu.
            nodesWithEdges.sort(Comparator.comparingInt(Node::getIndex));

            List<Integer> connectedNodes = new ArrayList<>();
            List<Integer> groupBoundaries = new ArrayList<>(); // będzie zapisywane jako firstElementInGroup
            int flatIndex = 0;
            for (Node node : nodesWithEdges) {
                groupBoundaries.add(flatIndex);
                // Zapewniamy, że pierwszy element grupy to indeks samego węzła.
                connectedNodes.add(node.getIndex());
                flatIndex++;
                // Pobieramy i sortujemy sąsiadów dla spójności (np. wg indeksu).
                List<Node> neighbors = new ArrayList<>(graph.getNeighbors(node));
                neighbors.sort(Comparator.comparingInt(Node::getIndex));
                for (Node neighbor : neighbors) {
                    connectedNodes.add(neighbor.getIndex());
                    flatIndex++;
                }
            }
            // 5. Zapisz connectedNodes (część krawędzi) – linia czwarta.
            writer.write(joinList(connectedNodes));
            writer.newLine();
            // Zapisz pierwsze elementy grup (firstElementInGroup) – linia piąta.
            // UWAGA: Czytnik spodziewa się, że ostatnia wartość zostanie dołączona automatycznie (jako connectedNodes.size()).
            if (!groupBoundaries.isEmpty()) {
                writer.write(joinList(groupBoundaries));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas zapisu grafu: " + e.getMessage());
            throw new Exception("Błąd podczas zapisu grafu: " + e.getMessage(), e);
        }
    }

    // Pomocnicza metoda łącząca elementy listy rozdzielone określonym separatorem.
    private String joinList(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
}
