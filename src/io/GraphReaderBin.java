package io;

import model.Graph;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class GraphReaderBin implements GraphReader {
    private final GraphUtils graphUtils = new GraphUtils();

    @Override
    public List<Graph> readGraph(String filePath) throws Exception {

        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            byte[] signature = new byte[2];
            file.readFully(signature);

//            String result = new String(signature);
            file.seek(14);

            int numCols = file.readByte();
            List<Integer> vertices = readOneLEB128Table(file, file.getFilePointer() + 3);
            List<Integer> verticesRow = restoreFromDelta(readOneLEB128Table(file, file.getFilePointer()));
            List<Integer> connectedNodes = readOneLEB128Table(file, file.getFilePointer());
            List<Integer> firstElementInGroup = new ArrayList<>();

            while (file.getFilePointer() < file.length()) {
                List<Integer> deltas = readOneLEB128Table(file, file.getFilePointer());
                firstElementInGroup.addAll(restoreFromDelta(deltas));
            }
            firstElementInGroup.add(connectedNodes.size());

            return graphUtils.separateGraphs(graphUtils.build(numCols, vertices, verticesRow, connectedNodes, firstElementInGroup));

        } catch (Exception e) {
            throw new Exception("Error while reading graph: " + e.getMessage(), e);
        }
    }

    private List<Integer> readOneLEB128Table(RandomAccessFile file, long offset) throws IOException {
        file.seek(offset);
        List<Integer> result = new ArrayList<>();

        while (true) {
            long currentPos = file.getFilePointer();

            byte b1 = (byte) file.read();
            if (b1 == -1) break;

            byte b2 = (byte) file.read();
            if (b2 == -1) break;

            byte b3 = (byte) file.read();
            if (b3 == -1) break;

            if ((b1 == (byte) 0xE0) && (b2 == (byte) 0xC6) && (b3 == (byte) 0x5B)) {
                break;
            }

            file.seek(currentPos);

            int value = 0;
            int shift = 0;
            while (true) {
                int lebByte = file.read();
                if (lebByte == -1) break;
                value |= (lebByte & 0x7F) << shift;
                if ((lebByte & 0x80) == 0) break;
                shift += 7;
            }

            result.add(value);
        }

        return result;
    }

    public List<Integer> restoreFromDelta(List<Integer> deltas) {
        List<Integer> result = new ArrayList<>();
        if (deltas.isEmpty()) return result;

        int sum = 0;
        for (Integer delta : deltas) {
            sum += delta;
            result.add(sum);
        }
        return result;
    }
}
