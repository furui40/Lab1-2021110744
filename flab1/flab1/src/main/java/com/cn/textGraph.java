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
        // 从 resource 文件夹中读取文件内容
        String fileName = "1.txt"; // 指定文件名
        try {
            // 读取文件并构建图
            buildGraphFromFile(fileName);
        } catch (Exception e) {
            System.out.println("读取文件时出错：" + e.getMessage());
        }


        // 显示菜单
        while (true) {
            System.out.println("\n请选择功能：");
            System.out.println("1. 展示有向图");
            System.out.println("2. 查询桥接词");
            System.out.println("3. 根据桥接词生成新文本");
            System.out.println("4. 计算最短路径");
            System.out.println("5. 随机游走");
            System.out.println("6. 退出");
            Scanner scanner = new Scanner(System.in);
            int choice = 0;
            while (true) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // 消耗换行符
                    break; // 退出循环
                } else {
                    System.out.println("输入不是整数，请重新输入：");
                    scanner.nextLine(); // 清除非整数输入
                }
            }

            switch (choice) {
                case 1:
                    Graphviz.useEngine(new GraphvizCmdLineEngine());
                    showDirectedGraph();
                    break;
                case 2:
                    System.out.println("请输入两个单词：");
                    String word1 = scanner.next();
                    String word2 = scanner.next();
                    if (!isValidInput(word1) || !isValidInput(word2)) {
                        System.out.println("输入的单词不在图中，请重新输入。");
                        break; // 终止程序或者添加循环以重新输入
                    }
                    System.out.println(queryBridgeWords(word1, word2));
                    break;
                case 3:
                    System.out.println("请输入一段文本：");
                    String inputText = scanner.nextLine();
                    if (!isValidInput(inputText)) {
                        System.out.println("输入的文本中包含图中不存在的单词，请重新输入。");
                        break; // 终止程序或者添加循环以重新输入
                    }
                    System.out.println(generateNewText(inputText));
                    break;
                case 4:
                    System.out.println("请输入一个单词：");
                    word1 = scanner.next();
                    System.out.println("是否要输入第二个单词？(y/n): ");
                    String option;
                    do {
                        option = scanner.next().toLowerCase();
                        if (!option.equals("y") && !option.equals("n")) {
                            System.out.println("请输入 'y' 或 'n'：");
                        }
                    } while (!option.equals("y") && !option.equals("n"));
                    if (option.equals("y")) {
                        System.out.println("请输入第二个单词：");
                        word2 = scanner.next();
                    } else {
                        word2 = null;
                    }

                    if (!isValidInput(word1) || (word2 != null && !isValidInput(word2))) {
                        System.out.println("输入的单词不在图中，请重新输入。");
                        break; // 终止程序或者添加循环以重新输入
                    }
                    System.out.println(calcShortestPath(word1, word2));

                    break;
                case 5:
                    System.out.println(randomWalk());
                    break;
                case 6:
                    System.out.println("谢谢使用，再见！");
                    return;
                default:
                    System.out.println("无效选择，请重新输入。");
            }
        }
    }
    public static boolean isValidInput(String input) {
        String[] words = input.split("\\s+"); // 使用空格分割单词
        for (String word : words) {
            if (!graph.containsKey(word)) {
                return false;
            }
        }
        return true;
    }

    public static void showDirectedGraph() {
        // 检查 graph 是否为 null 或为空
        if (graph == null || graph.isEmpty()) {
            System.err.println("图数据为空");
            return;
        }

        Graph<String, DefaultEdge> jgraph = new DefaultDirectedWeightedGraph<>(DefaultEdge.class);

        // 添加节点
        for (String word : graph.keySet()) {
            if (word != null) {
                jgraph.addVertex(word);
            }
        }

        // 添加边和权重
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

        // 将图形数据写入临时文件
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
            System.err.println("写入临时文件时出错: " + e.getMessage());
            return;
        }
        renderDotGraph("temp_graph.dot","graph.png");

    }
    public static void renderDotGraph(String dotFilePath, String outputFilePath) {
        try {
            // 指定使用 GraphvizJdkEngine 引擎
            Graphviz.useEngine(new GraphvizJdkEngine());

            // 读取 .dot 文件并渲染为 PNG 图片
            Graphviz.fromFile(new File(dotFilePath))
                    .render(Format.PNG)
                    .toFile(new File(outputFilePath));
            System.out.println("图片已成功保存为 " + outputFilePath);
        } catch (IOException e) {
            System.err.println("生成图片时出错: " + e.getMessage());
        }
    }

    public static String queryBridgeWords(String word1, String word2) {
        // 查询桥接词
        // 检查输入的单词是否存在于图中
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + (!graph.containsKey(word1) ? word1 : word2) + " in the graph!";
        }

        // 存储桥接词的列表
        List<String> bridgeWords = new ArrayList<>();
        // 遍历word1的邻居节点
        for (Map.Entry<String, Integer> entry : graph.get(word1).entrySet()) {
            String bridge = entry.getKey();
            // 如果桥接词的邻居节点中包含word2，则添加到桥接词列表中
            if (graph.containsKey(bridge) && graph.get(bridge).containsKey(word2)) {
                bridgeWords.add(bridge);
            }
        }

        // 如果不存在桥接词，则返回相应提示信息
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            // 如果存在一个或多个桥接词，则返回相应的提示信息
            String bridgeWordPhrase = bridgeWords.size() > 1 ? "are" : "is";
            return "The bridge words from " + word1 + " to " + word2 + " " + bridgeWordPhrase + ": " + String.join(", ", bridgeWords);
        }
    }





    public static String generateNewText(String inputText) {
        // 根据桥接词生成新文本
        System.out.println("根据桥接词生成新文本...");

        // 创建一个StringBuilder对象，用于构建新文本
        StringBuilder newText = new StringBuilder();

        // 将输入文本按空格分割为单词数组
        String[] words = inputText.split("\\s+");

        // 遍历单词数组，查找桥接词并构建新文本
        for (int i = 0; i < words.length - 1; i++) {
            // 将当前单词添加到新文本中，并添加空格
            newText.append(words[i]).append(" ");

            // 获取当前单词及其下一个单词，并转换为小写
            String word1 = words[i].toLowerCase();
            String word2 = words[i + 1].toLowerCase();

            // 检查图中是否包含当前单词及其下一个单词
            if (graph.containsKey(word1) && graph.containsKey(word2)) {
                // 获取当前单词的邻居节点及其权重信息
                Map<String, Integer> edges = graph.get(word1);

                // 创建一个列表，用于存储符合条件的桥接词
                List<String> selectedBridgeWords = new ArrayList<>();

                // 遍历当前单词的邻居节点，查找是否存在桥接词
                for (String bridge : edges.keySet()) {
                    // 检查桥接词的邻居节点是否包含下一个单词
                    if (graph.containsKey(bridge) && graph.get(bridge).containsKey(word2)) {
                        // 如果是桥接词，则将其添加到列表中
                        selectedBridgeWords.add(bridge);
                    }
                }

                // 如果存在符合条件的桥接词
                if (!selectedBridgeWords.isEmpty()) {
                    // 随机选择一个桥接词，并插入到新文本中
                    Random random = new Random();
                    String selectedBridge = selectedBridgeWords.get(random.nextInt(selectedBridgeWords.size()));
                    newText.append(selectedBridge).append(" ");
                }
            }
        }

        // 添加最后一个单词到新文本中
        newText.append(words[words.length - 1]);

        // 将StringBuilder对象转换为字符串，并返回
        return newText.toString();
    }
    public static String calcShortestPath(String word1, String word2) {
        // 计算最短路径
        if ((word2 == null && !graph.containsKey(word1)) || (word2 != null && (!graph.containsKey(word1) || !graph.containsKey(word2)))) {
            return "输入的两个单词不可达！";
        }
        if (word2 == null) { // 1个参数
            StringBuilder result = new StringBuilder();
            for (String targetWord : graph.keySet()) {
                if (!targetWord.equals(word1)) {
                    if (graph.containsKey(targetWord)) { // 检查目标单词是否存在于图中
                        String shortestPath = calcShortestPath(word1, targetWord);
                        result.append(shortestPath).append("\n");
                    } else {
                        result.append("目标单词 ").append(targetWord).append(" 不在图中\n");
                    }
                }
            }
            return result.toString();
        }

        // 使用Dijkstra算法计算最短路径
        // 创建一个优先队列，按照节点的距离进行排序
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        // 存储每个节点到起始节点的距离
        Map<String, Integer> distances = new HashMap<>();
        // 存储每个节点的父节点，用于构建最短路径
        Map<String, List<List<String>>> paths = new HashMap<>();

        // 初始化距离，将所有节点的距离设置为无穷大
        for (String word : graph.keySet()) {
            distances.put(word, Integer.MAX_VALUE);
            paths.put(word, new ArrayList<>());
        }
        // 将起始节点的距离设置为0，并添加到优先队列中
        distances.put(word1, 0);
        pq.add(new Node(word1, 0));
        paths.get(word1).add(Collections.singletonList(word1));

        // 进行Dijkstra算法的主循环，直到优先队列为空
        while (!pq.isEmpty()) {
            Node current = pq.poll(); // 从优先队列中取出距离最小的节点
            if (current.word.equals(word2)) { // 如果当前节点是目标节点，则跳出循环
                break;
            }
            // 如果当前节点的距离大于已知的最小距离，则忽略当前节点
            if (current.distance > distances.get(current.word)) {
                continue;
            }
            // 遍历当前节点的所有邻居节点
            Map<String, Integer> neighbors = graph.get(current.word);
            if (neighbors != null) {
                for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
                    String neighbor = neighborEntry.getKey();
                    int weight = neighborEntry.getValue();
                    if (!distances.containsKey(neighbor)) {
                        continue; // 跳过不存在于距离表中的邻居节点
                    }
                    int distanceThroughCurrent = distances.get(current.word) + weight; // 计算通过当前节点到达邻居节点的距离
                    // 如果新的距离比已知的距离小，则更新距离和父节点，并将邻居节点添加到优先队列中
                    if (distanceThroughCurrent < distances.get(neighbor)) {
                        distances.put(neighbor, distanceThroughCurrent);
                        pq.add(new Node(neighbor, distanceThroughCurrent));

                        // 清空现有路径，并添加新的最短路径
                        paths.get(neighbor).clear();
                        for (List<String> path : paths.get(current.word)) {
                            List<String> newPath = new ArrayList<>(path);
                            newPath.add(neighbor);
                            paths.get(neighbor).add(newPath);
                        }
                    } else if (distanceThroughCurrent == distances.get(neighbor)) {
                        // 如果新的距离与已知的最小距离相等，则添加新的路径
                        for (List<String> path : paths.get(current.word)) {
                            List<String> newPath = new ArrayList<>(path);
                            newPath.add(neighbor);
                            paths.get(neighbor).add(newPath);
                        }
                    }
                }
            }
        }

        // 检查目标节点是否可达
        if (!distances.containsKey(word2) || distances.get(word2) == Integer.MAX_VALUE) {
            return "输入的两个单词不可达！";
        }

        // 构建最短路径
        StringBuilder result = new StringBuilder();
        result.append("最短路径长度: ").append(distances.get(word2)).append("\n");

        List<List<String>> allPaths = paths.get(word2);
        if (!allPaths.isEmpty()) {
            result.append("最短路径: ").append(String.join(" -> ", allPaths.get(0))).append("\n");
            if (allPaths.size() > 1) {
                result.append("到目标单词的其他最短路径共有 ").append(allPaths.size() - 1).append(" 条:\n");
                for (int i = 1; i < allPaths.size(); i++) {
                    result.append("路径 ").append(i).append(": ").append(String.join(" -> ", allPaths.get(i))).append("\n");
                }
            }
        }

        return result.toString();
    }





    // 定义一个节点类，用于存储单词及其到起始节点的距离
    static class Node {
        String word; // 单词
        int distance; // 到起始节点的距离

        Node(String word, int distance) {
            this.word = word;
            this.distance = distance;
        }
    }



    public static String randomWalk() {
        // 随机游走
        System.out.println("随机游走...");

        // 创建一个StringBuilder对象，用于构建游走路径
        StringBuilder pathBuilder = new StringBuilder();
        // 创建一个Random对象，用于生成随机数
        Random random = new Random();
        // 创建一个Set集合，用于存储已访问过的边
        Set<String> visitedEdges = new HashSet<>();
        // 获取一个随机起始节点
        String currentNode = getRandomNode();

        // 创建一个Scanner对象，用于读取用户输入
        Scanner scanner = new Scanner(System.in);

        // 开始游走循环
        while (true) {
            // 将当前节点添加到游走路径中
            pathBuilder.append(currentNode).append(" ");

            // 获取当前节点的所有邻居节点及其权重
            Map<String, Integer> neighbors = graph.get(currentNode);
            // 如果当前节点没有邻居节点或者邻居节点为空，则结束游走
            if (neighbors == null || neighbors.isEmpty()) {
                break; // 如果当前节点没有出边，则结束游走
            }

            // 检查用户输入
            System.out.println("当前节点: " + currentNode);
            System.out.println("请输入 'stop' 停止游走，或按 Enter 键继续...");
            String userInput = scanner.nextLine();
            if ("stop".equalsIgnoreCase(userInput)) {
                break;
            }

            // 随机选择下一个节点
            List<String> nextNodes = new ArrayList<>();
            for (String neighbor : neighbors.keySet()) {
                int weight = neighbors.get(neighbor); // 获取邻居节点的权重
                // 将邻居节点根据权重添加多次到列表中
                for (int i = 0; i < weight; i++) {
                    nextNodes.add(neighbor);
                }
            }

            // 从相邻节点中随机选择一个作为下一个节点
            int randomIndex = random.nextInt(nextNodes.size());
            String nextNode = nextNodes.get(randomIndex);
            String edge = currentNode + "->" + nextNode;

            // 如果边已经被访问过，则结束游走
            if (visitedEdges.contains(edge)) {
                pathBuilder.append(nextNode);
                break;
            }

            // 标记边为已访问
            visitedEdges.add(edge);

            // 更新当前节点为下一个节点
            currentNode = nextNode;
        }

        // 将游走路径写入文件
        String filePath = "random_walk.txt";
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println(pathBuilder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 返回结果
        return "随机游走结果已写入文件 " + filePath;
    }

    // 辅助方法：从图中随机选择一个节点作为起始节点
    private static String getRandomNode() {
        // 获取图中所有节点构成的列表
        List<String> nodes = new ArrayList<>(graph.keySet());
        // 创建一个Random对象，用于生成随机数
        Random random = new Random();
        // 生成一个随机索引，范围为节点列表的大小
        int randomIndex = random.nextInt(nodes.size());
        // 返回随机选择的节点
        return nodes.get(randomIndex);
    }



    private static void buildGraphFromFile(String fileName) {
        // 从文件构建图
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String prevWord = null; // 上一行的最后一个单词
            String lastWord = null; // 当前行的最后一个单词
            String line;
            // 逐行读取文件内容
            while ((line = br.readLine()) != null) {
                // 将非字母字符替换为空格
                line = line.replaceAll("[^a-zA-Z ]", " ");
                // 将所有字母转换为小写
                line = line.toLowerCase();

                // 按空格分割单词
                String[] words = line.split("\\s+");
                // 如果上一行有最后一个单词，则与当前行的第一个单词相连接
                if (prevWord != null && words.length > 0 && !words[0].isEmpty()) {
                    String word1 = prevWord;
                    String word2 = words[0];
                    updateGraph(word1, word2);
                }
                // 遍历当前行的单词对，更新图的边权重
                for (int i = 0; i < words.length - 1; i++) {
                    String word1 = words[i];
                    String word2 = words[i + 1];
                    if (!word1.isEmpty() && !word2.isEmpty()) {
                        updateGraph(word1, word2);
                    }
                }
                // 更新上一行的最后一个单词为当前行的最后一个单词
                if (words.length > 0 && !words[words.length - 1].isEmpty()) {
                    prevWord = words[words.length - 1];
                    lastWord = prevWord; // 保存当前行的最后一个单词
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 打印构建的图（带边权重）
        System.out.println("构建的有向图（带边权重）：");
        // 遍历图中的节点及其邻居，并打印边的权重
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            // 遍历图中的每个节点及其对应的边权重映射
            String word = entry.getKey(); // 获取当前节点
            Map<String, Integer> edgeMap = entry.getValue(); // 获取当前节点的边权重映射
            for (Map.Entry<String, Integer> edgeEntry : edgeMap.entrySet()) {
                // 遍历当前节点的每条边及其对应的权重
                String neighbor = edgeEntry.getKey(); // 获取相邻节点
                int weight = edgeEntry.getValue(); // 获取边的权重
                // 打印当前节点到相邻节点的边及其权重
                System.out.println(word + " -> " + neighbor + ", 权重: " + weight);
            }
        }
    }

    private static void updateGraph(String word1, String word2) {
        // 更新图中的边权重
        graph.putIfAbsent(word1, new HashMap<>());
        if (word2 != null && !word2.isEmpty()) {
            Map<String, Integer> edges = graph.get(word1);
            edges.put(word2, edges.getOrDefault(word2, 0) + 1);
        }
    }

}