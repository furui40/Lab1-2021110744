package com.cn;
import java.io.*;
import java.util.*;

import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

import guru.nidi.graphviz.engine.GraphvizJdkEngine;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class textGraph {

    private static Map<String, Map<String, Integer>> graph = new HashMap<>();


    public static void main(String[] args) {
        // �� resource �ļ����ж�ȡ�ļ�����
        String fileName = "1.txt"; // ָ���ļ���
        try {
            // ��ȡ�ļ�������ͼ
            buildGraphFromFile(fileName);
        } catch (Exception e) {
            System.out.println("��ȡ�ļ�ʱ����" + e.getMessage());
        }


        // ��ʾ�˵�
        while (true) {
            System.out.println("\n��ѡ���ܣ�");
            System.out.println("1. չʾ����ͼ");
            System.out.println("2. ��ѯ�ŽӴ�");
            System.out.println("3. �����ŽӴ��������ı�");
            System.out.println("4. �������·��");
            System.out.println("5. �������");
            System.out.println("6. �˳�");
            Scanner scanner = new Scanner(System.in);
            int choice = 0;
            while (true) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // ���Ļ��з�
                    break; // �˳�ѭ��
                } else {
                    System.out.println("���벻�����������������룺");
                    scanner.nextLine(); // �������������
                }
            }

            switch (choice) {
                case 1:
                    Graphviz.useEngine(new GraphvizCmdLineEngine());
                    showDirectedGraph();
                    break;
                case 2:
                    System.out.println("�������������ʣ�");
                    String word1 = scanner.next();
                    String word2 = scanner.next();
                    if (!isValidInput(word1) || !isValidInput(word2)) {
                        System.out.println("����ĵ��ʲ���ͼ�У����������롣");
                        break; // ��ֹ����������ѭ������������
                    }
                    System.out.println(queryBridgeWords(word1, word2));
                    break;
                case 3:
                    System.out.println("������һ���ı���");
                    String inputText = scanner.nextLine();
                    if (!isValidInput(inputText)) {
                        System.out.println("������ı��а���ͼ�в����ڵĵ��ʣ����������롣");
                        break; // ��ֹ����������ѭ������������
                    }
                    System.out.println(generateNewText(inputText));
                    break;
                case 4:
                    System.out.println("������һ�����ʣ�");
                    word1 = scanner.next();
                    System.out.println("�Ƿ�Ҫ����ڶ������ʣ�(y/n): ");
                    String option;
                    do {
                        option = scanner.next().toLowerCase();
                        if (!option.equals("y") && !option.equals("n")) {
                            System.out.println("������ 'y' �� 'n'��");
                        }
                    } while (!option.equals("y") && !option.equals("n"));
                    if (option.equals("y")) {
                        System.out.println("������ڶ������ʣ�");
                        word2 = scanner.next();
                    } else {
                        word2 = null;
                    }

                    if (!isValidInput(word1) || (word2 != null && !isValidInput(word2))) {
                        System.out.println("����ĵ��ʲ���ͼ�У����������롣");
                        break; // ��ֹ����������ѭ������������
                    }
                    System.out.println(calcShortestPath(word1, word2));

                    break;
                case 5:
                    System.out.println(randomWalk());
                    break;
                case 6:
                    System.out.println("ллʹ�ã��ټ���");
                    return;
                default:
                    System.out.println("��Чѡ�����������롣");
            }
        }
    }
    public static boolean isValidInput(String input) {
        String[] words = input.split("\\s+"); // ʹ�ÿո�ָ��
        for (String word : words) {
            if (!graph.containsKey(word)) {
                return false;
            }
        }
        return true;
    }

    public static void showDirectedGraph() {
        // ��� graph �Ƿ�Ϊ null ��Ϊ��
        if (graph == null || graph.isEmpty()) {
            System.err.println("ͼ����Ϊ��");
            return;
        }

        Graph<String, DefaultEdge> jgraph = new DefaultDirectedWeightedGraph<>(DefaultEdge.class);

        // ��ӽڵ�
        for (String word : graph.keySet()) {
            if (word != null) {
                jgraph.addVertex(word);
            }
        }

        // ��ӱߺ�Ȩ��
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            String source = entry.getKey();
            if (source != null && jgraph.containsVertex(source)) {
                Map<String, Integer> neighbors = entry.getValue();
                if (neighbors != null) {
                    for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
                        String target = neighborEntry.getKey();
                        if (target != null && jgraph.containsVertex(target)) {
                            int weight = neighborEntry.getValue();
                            DefaultEdge edge = jgraph.addEdge(source, target);
                            jgraph.setEdgeWeight(edge, weight);
                        }
                    }
                }
            }
        }

        // ��ͼ������д����ʱ�ļ�
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("temp_graph.dot"))) {
            writer.write("strict digraph G {\n");
            for (DefaultEdge edge : jgraph.edgeSet()) {
                String source = jgraph.getEdgeSource(edge);
                String target = jgraph.getEdgeTarget(edge);
                int weight = (int) jgraph.getEdgeWeight(edge);
                writer.write(String.format("  \"%s\" -> \"%s\" [label=\"%d\"];\n", source, target, weight));
            }
            writer.write("}");
        } catch (IOException e) {
            System.err.println("д����ʱ�ļ�ʱ����: " + e.getMessage());
            return;
        }
        renderDotGraph("temp_graph.dot","graph.png");

    }
    public static void renderDotGraph(String dotFilePath, String outputFilePath) {
        try {
            // ָ��ʹ�� GraphvizJdkEngine ����
            Graphviz.useEngine(new GraphvizJdkEngine());

            // ��ȡ .dot �ļ�����ȾΪ PNG ͼƬ
            Graphviz.fromFile(new File(dotFilePath))
                    .render(Format.PNG)
                    .toFile(new File(outputFilePath));
            System.out.println("ͼƬ�ѳɹ�����Ϊ " + outputFilePath);
        } catch (IOException e) {
            System.err.println("����ͼƬʱ����: " + e.getMessage());
        }
    }

    public static String queryBridgeWords(String word1, String word2) {
        // ��ѯ�ŽӴ�
        // �������ĵ����Ƿ������ͼ��
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + (!graph.containsKey(word1) ? word1 : word2) + " in the graph!";
        }

        // �洢�ŽӴʵ��б�
        List<String> bridgeWords = new ArrayList<>();
        // ����word1���ھӽڵ�
        for (Map.Entry<String, Integer> entry : graph.get(word1).entrySet()) {
            String bridge = entry.getKey();
            // ����ŽӴʵ��ھӽڵ��а���word2������ӵ��ŽӴ��б���
            if (graph.containsKey(bridge) && graph.get(bridge).containsKey(word2)) {
                bridgeWords.add(bridge);
            }
        }

        // ����������ŽӴʣ��򷵻���Ӧ��ʾ��Ϣ
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            // �������һ�������ŽӴʣ��򷵻���Ӧ����ʾ��Ϣ
            String bridgeWordPhrase = bridgeWords.size() > 1 ? "are" : "is";
            return "The bridge words from " + word1 + " to " + word2 + " " + bridgeWordPhrase + ": " + String.join(", ", bridgeWords);
        }
    }





    public static String generateNewText(String inputText) {
        // �����ŽӴ��������ı�
        System.out.println("�����ŽӴ��������ı�...");

        // ����һ��StringBuilder�������ڹ������ı�
        StringBuilder newText = new StringBuilder();

        // �������ı����ո�ָ�Ϊ��������
        String[] words = inputText.split("\\s+");

        // �����������飬�����ŽӴʲ��������ı�
        for (int i = 0; i < words.length - 1; i++) {
            // ����ǰ������ӵ����ı��У�����ӿո�
            newText.append(words[i]).append(" ");

            // ��ȡ��ǰ���ʼ�����һ�����ʣ���ת��ΪСд
            String word1 = words[i].toLowerCase();
            String word2 = words[i + 1].toLowerCase();

            // ���ͼ���Ƿ������ǰ���ʼ�����һ������
            if (graph.containsKey(word1) && graph.containsKey(word2)) {
                // ��ȡ��ǰ���ʵ��ھӽڵ㼰��Ȩ����Ϣ
                Map<String, Integer> edges = graph.get(word1);

                // ����һ���б����ڴ洢�����������ŽӴ�
                List<String> selectedBridgeWords = new ArrayList<>();

                // ������ǰ���ʵ��ھӽڵ㣬�����Ƿ�����ŽӴ�
                for (String bridge : edges.keySet()) {
                    // ����ŽӴʵ��ھӽڵ��Ƿ������һ������
                    if (graph.containsKey(bridge) && graph.get(bridge).containsKey(word2)) {
                        // ������ŽӴʣ�������ӵ��б���
                        selectedBridgeWords.add(bridge);
                    }
                }

                // ������ڷ����������ŽӴ�
                if (!selectedBridgeWords.isEmpty()) {
                    // ���ѡ��һ���ŽӴʣ������뵽���ı���
                    Random random = new Random();
                    String selectedBridge = selectedBridgeWords.get(random.nextInt(selectedBridgeWords.size()));
                    newText.append(selectedBridge).append(" ");
                }
            }
        }

        // ������һ�����ʵ����ı���
        newText.append(words[words.length - 1]);

        // ��StringBuilder����ת��Ϊ�ַ�����������
        return newText.toString();
    }
    public static String calcShortestPath(String word1, String word2) {
        // �������·��
        if ((word2 == null && !graph.containsKey(word1)) || (word2 != null && (!graph.containsKey(word1) || !graph.containsKey(word2)))) {
            return "������������ʲ��ɴ";
        }
        if (word2 == null) { // 1������
            StringBuilder result = new StringBuilder();
            for (String targetWord : graph.keySet()) {
                if (!targetWord.equals(word1)) {
                    if (graph.containsKey(targetWord)) { // ���Ŀ�굥���Ƿ������ͼ��
                        String shortestPath = calcShortestPath(word1, targetWord);
                        result.append(shortestPath).append("\n");
                    } else {
                        result.append("Ŀ�굥�� ").append(targetWord).append(" ����ͼ��\n");
                    }
                }
            }
            return result.toString();
        }

        // ʹ��Dijkstra�㷨�������·��
        // ����һ�����ȶ��У����սڵ�ľ����������
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        // �洢ÿ���ڵ㵽��ʼ�ڵ�ľ���
        Map<String, Integer> distances = new HashMap<>();
        // �洢ÿ���ڵ�ĸ��ڵ㣬���ڹ������·��
        Map<String, List<List<String>>> paths = new HashMap<>();

        // ��ʼ�����룬�����нڵ�ľ�������Ϊ�����
        for (String word : graph.keySet()) {
            distances.put(word, Integer.MAX_VALUE);
            paths.put(word, new ArrayList<>());
        }
        // ����ʼ�ڵ�ľ�������Ϊ0������ӵ����ȶ�����
        distances.put(word1, 0);
        pq.add(new Node(word1, 0));
        paths.get(word1).add(Collections.singletonList(word1));

        // ����Dijkstra�㷨����ѭ����ֱ�����ȶ���Ϊ��
        while (!pq.isEmpty()) {
            Node current = pq.poll(); // �����ȶ�����ȡ��������С�Ľڵ�
            if (current.word.equals(word2)) { // �����ǰ�ڵ���Ŀ��ڵ㣬������ѭ��
                break;
            }
            // �����ǰ�ڵ�ľ��������֪����С���룬����Ե�ǰ�ڵ�
            if (current.distance > distances.get(current.word)) {
                continue;
            }
            // ������ǰ�ڵ�������ھӽڵ�
            Map<String, Integer> neighbors = graph.get(current.word);
            if (neighbors != null) {
                for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
                    String neighbor = neighborEntry.getKey();
                    int weight = neighborEntry.getValue();
                    if (!distances.containsKey(neighbor)) {
                        continue; // �����������ھ�����е��ھӽڵ�
                    }
                    int distanceThroughCurrent = distances.get(current.word) + weight; // ����ͨ����ǰ�ڵ㵽���ھӽڵ�ľ���
                    // ����µľ������֪�ľ���С������¾���͸��ڵ㣬�����ھӽڵ���ӵ����ȶ�����
                    if (distanceThroughCurrent < distances.get(neighbor)) {
                        distances.put(neighbor, distanceThroughCurrent);
                        pq.add(new Node(neighbor, distanceThroughCurrent));

                        // �������·����������µ����·��
                        paths.get(neighbor).clear();
                        for (List<String> path : paths.get(current.word)) {
                            List<String> newPath = new ArrayList<>(path);
                            newPath.add(neighbor);
                            paths.get(neighbor).add(newPath);
                        }
                    } else if (distanceThroughCurrent == distances.get(neighbor)) {
                        // ����µľ�������֪����С������ȣ�������µ�·��
                        for (List<String> path : paths.get(current.word)) {
                            List<String> newPath = new ArrayList<>(path);
                            newPath.add(neighbor);
                            paths.get(neighbor).add(newPath);
                        }
                    }
                }
            }
        }

        // ���Ŀ��ڵ��Ƿ�ɴ�
        if (!distances.containsKey(word2) || distances.get(word2) == Integer.MAX_VALUE) {
            return "������������ʲ��ɴ";
        }

        // �������·��
        StringBuilder result = new StringBuilder();
        result.append("���·������: ").append(distances.get(word2)).append("\n");

        List<List<String>> allPaths = paths.get(word2);
        if (!allPaths.isEmpty()) {
            result.append("���·��: ").append(String.join(" -> ", allPaths.get(0))).append("\n");
            if (allPaths.size() > 1) {
                result.append("��Ŀ�굥�ʵ��������·������ ").append(allPaths.size() - 1).append(" ��:\n");
                for (int i = 1; i < allPaths.size(); i++) {
                    result.append("·�� ").append(i).append(": ").append(String.join(" -> ", allPaths.get(i))).append("\n");
                }
            }
        }

        return result.toString();
    }





    // ����һ���ڵ��࣬���ڴ洢���ʼ��䵽��ʼ�ڵ�ľ���
    static class Node {
        String word; // ����
        int distance; // ����ʼ�ڵ�ľ���

        Node(String word, int distance) {
            this.word = word;
            this.distance = distance;
        }
    }



    public static String randomWalk() {
        // �������
        System.out.println("�������...");

        // ����һ��StringBuilder�������ڹ�������·��
        StringBuilder pathBuilder = new StringBuilder();
        // ����һ��Random�����������������
        Random random = new Random();
        // ����һ��Set���ϣ����ڴ洢�ѷ��ʹ��ı�
        Set<String> visitedEdges = new HashSet<>();
        // ��ȡһ�������ʼ�ڵ�
        String currentNode = getRandomNode();

        // ����һ��Scanner�������ڶ�ȡ�û�����
        Scanner scanner = new Scanner(System.in);

        // ��ʼ����ѭ��
        while (true) {
            // ����ǰ�ڵ���ӵ�����·����
            pathBuilder.append(currentNode).append(" ");

            // ��ȡ��ǰ�ڵ�������ھӽڵ㼰��Ȩ��
            Map<String, Integer> neighbors = graph.get(currentNode);
            // �����ǰ�ڵ�û���ھӽڵ�����ھӽڵ�Ϊ�գ����������
            if (neighbors == null || neighbors.isEmpty()) {
                break; // �����ǰ�ڵ�û�г��ߣ����������
            }

            // ����û�����
            System.out.println("��ǰ�ڵ�: " + currentNode);
            System.out.println("������ 'stop' ֹͣ���ߣ��� Enter ������...");
            String userInput = scanner.nextLine();
            if ("stop".equalsIgnoreCase(userInput)) {
                break;
            }

            // ���ѡ����һ���ڵ�
            List<String> nextNodes = new ArrayList<>();
            for (String neighbor : neighbors.keySet()) {
                int weight = neighbors.get(neighbor); // ��ȡ�ھӽڵ��Ȩ��
                // ���ھӽڵ����Ȩ����Ӷ�ε��б���
                for (int i = 0; i < weight; i++) {
                    nextNodes.add(neighbor);
                }
            }

            // �����ڽڵ������ѡ��һ����Ϊ��һ���ڵ�
            int randomIndex = random.nextInt(nextNodes.size());
            String nextNode = nextNodes.get(randomIndex);
            String edge = currentNode + "->" + nextNode;

            // ������Ѿ������ʹ������������
            if (visitedEdges.contains(edge)) {
                pathBuilder.append(nextNode);
                break;
            }

            // ��Ǳ�Ϊ�ѷ���
            visitedEdges.add(edge);

            // ���µ�ǰ�ڵ�Ϊ��һ���ڵ�
            currentNode = nextNode;
        }

        // ������·��д���ļ�
        String filePath = "random_walk.txt";
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println(pathBuilder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // ���ؽ��
        return "������߽����д���ļ� " + filePath;
    }

    // ������������ͼ�����ѡ��һ���ڵ���Ϊ��ʼ�ڵ�
    private static String getRandomNode() {
        // ��ȡͼ�����нڵ㹹�ɵ��б�
        List<String> nodes = new ArrayList<>(graph.keySet());
        // ����һ��Random�����������������
        Random random = new Random();
        // ����һ�������������ΧΪ�ڵ��б�Ĵ�С
        int randomIndex = random.nextInt(nodes.size());
        // �������ѡ��Ľڵ�
        return nodes.get(randomIndex);
    }



    private static void buildGraphFromFile(String fileName) {
        // ���ļ�����ͼ
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String prevWord = null; // ��һ�е����һ������
            String lastWord = null; // ��ǰ�е����һ������
            String line;
            // ���ж�ȡ�ļ�����
            while ((line = br.readLine()) != null) {
                // ������ĸ�ַ��滻Ϊ�ո�
                line = line.replaceAll("[^a-zA-Z ]", " ");
                // ��������ĸת��ΪСд
                line = line.toLowerCase();

                // ���ո�ָ��
                String[] words = line.split("\\s+");
                // �����һ�������һ�����ʣ����뵱ǰ�еĵ�һ������������
                if (prevWord != null && words.length > 0 && !words[0].isEmpty()) {
                    String word1 = prevWord;
                    String word2 = words[0];
                    updateGraph(word1, word2);
                }
                // ������ǰ�еĵ��ʶԣ�����ͼ�ı�Ȩ��
                for (int i = 0; i < words.length - 1; i++) {
                    String word1 = words[i];
                    String word2 = words[i + 1];
                    if (!word1.isEmpty() && !word2.isEmpty()) {
                        updateGraph(word1, word2);
                    }
                }
                // ������һ�е����һ������Ϊ��ǰ�е����һ������
                if (words.length > 0 && !words[words.length - 1].isEmpty()) {
                    prevWord = words[words.length - 1];
                    lastWord = prevWord; // ���浱ǰ�е����һ������
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ��ӡ������ͼ������Ȩ�أ�
        System.out.println("����������ͼ������Ȩ�أ���");
        // ����ͼ�еĽڵ㼰���ھӣ�����ӡ�ߵ�Ȩ��
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            // ����ͼ�е�ÿ���ڵ㼰���Ӧ�ı�Ȩ��ӳ��
            String word = entry.getKey(); // ��ȡ��ǰ�ڵ�
            Map<String, Integer> edgeMap = entry.getValue(); // ��ȡ��ǰ�ڵ�ı�Ȩ��ӳ��
            for (Map.Entry<String, Integer> edgeEntry : edgeMap.entrySet()) {
                // ������ǰ�ڵ��ÿ���߼����Ӧ��Ȩ��
                String neighbor = edgeEntry.getKey(); // ��ȡ���ڽڵ�
                int weight = edgeEntry.getValue(); // ��ȡ�ߵ�Ȩ��
                // ��ӡ��ǰ�ڵ㵽���ڽڵ�ı߼���Ȩ��
                System.out.println(word + " -> " + neighbor + ", Ȩ��: " + weight);
            }
        }
    }

    private static void updateGraph(String word1, String word2) {
        // ����ͼ�еı�Ȩ��
        graph.putIfAbsent(word1, new HashMap<>());
        if (word2 != null && !word2.isEmpty()) {
            Map<String, Integer> edges = graph.get(word1);
            edges.put(word2, edges.getOrDefault(word2, 0) + 1);
        }
    }

}