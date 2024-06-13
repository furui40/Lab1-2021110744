import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import com.cn.TextGraph;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Testb {
    private static final String GRAPH_FILE_PATH = "C:/Users/86188/Desktop/software/lab1/new/1.txt";
    private static Map<String, Map<String, Integer>> graph = new HashMap<>();

    @Test
    public void testShortestPath1(){
        //两个节点存在最短路径
        TextGraph textGraph = new TextGraph();
        textGraph.buildGraphFromFile(GRAPH_FILE_PATH);
        String result = textGraph.calcShortestPath("here", "a");
        System.out.println(result);
        assertThat(result, containsString("2"));
        assertThat(result, containsString("here -> only -> a"));
    }
    @Test
    public void testShortestPath2(){
        //两个节点不存在最短路径
        TextGraph textGraph = new TextGraph();
        textGraph.buildGraphFromFile(GRAPH_FILE_PATH);
        String result = textGraph.calcShortestPath("us", "can");
        System.out.println(result);
        assertThat(result, containsString("Cannot reach"));
    }
    @Test
    public void testShortestPath3(){
        //存在多条最短路径
        TextGraph textGraph = new TextGraph();
        textGraph.buildGraphFromFile(GRAPH_FILE_PATH);
        String result = textGraph.calcShortestPath("a", "analysis");
        System.out.println(result);
        assertThat(result, containsString("other path"));
        assertThat(result, containsString("a -> thorough -> analysis"));
        assertThat(result, containsString("a -> complete -> analysis"));
    }
    @Test
    public void testShortestPath4(){
        //第一个节点到其他所有节点的最短路径
        TextGraph textGraph = new TextGraph();
        textGraph.buildGraphFromFile(GRAPH_FILE_PATH);
        String result = textGraph.calcShortestPath("a", null);
        System.out.println(result);
        assertThat(result, containsString("other path"));
        assertThat(result, containsString("a -> sample -> text"));
        assertThat(result, containsString("a -> thorough -> analysis"));
        assertThat(result, containsString("a -> complete -> analysis"));
    }
    @Test
    public void testShortestPath5(){
        //第一个字符串为空
        TextGraph textGraph = new TextGraph();
        textGraph.buildGraphFromFile(GRAPH_FILE_PATH);
        String result = textGraph.calcShortestPath(null, "a");
        System.out.println(result);
        assertThat(result, containsString("Cannot reach"));
    }
    @Test
    public void testShortestPath6(){
        //节点不在图中
        TextGraph textGraph = new TextGraph();
        textGraph.buildGraphFromFile(GRAPH_FILE_PATH);
        String result = textGraph.calcShortestPath("b", "c");
        System.out.println(result);
        assertThat(result, containsString("Cannot reach"));
    }
    @Test
    public void testShortestPath7(){
        //图为空
        TextGraph textGraph = new TextGraph();
        graph.clear();
        String result = textGraph.calcShortestPath("here", "a");
        System.out.println(result);
        assertThat(result, containsString("Graph not exists"));
    }
    @Test
    public void testShortestPath8(){
        //输入非字符串类型
        //错误的传参类型在静态编译时就不会通过，该项测试无效
//        TextGraph textGraph = new TextGraph();
//        textGraph.buildGraphFromFile(GRAPH_FILE_PATH);
//        int a = 1;
//        int b = 2;
//        String result = textGraph.calcShortestPath(a, b);
//        System.out.println(result);
//        assertThat(result, containsString("Cannot reach"));
    }
    @Test
    public void testShortestPath9(){
        //两个字符串均为空
        TextGraph textGraph = new TextGraph();
        textGraph.buildGraphFromFile(GRAPH_FILE_PATH);
        String w1 = null;
        String result = textGraph.calcShortestPath(w1, w1);
        System.out.println(result);
        assertThat(result, containsString("Cannot reach"));
    }
}