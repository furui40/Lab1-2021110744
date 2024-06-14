import static org.junit.Assert.assertEquals;

import com.cn.TextGraph;
import org.junit.BeforeClass;
import org.junit.Test;


public class Testw {
    private static final String GRAPH_FILE_PATH = "C:/Users/86188/Desktop/software/lab1/new/1.txt";
    private static TextGraph textGraph;

    @BeforeClass
    public static void setUp() {
        textGraph = new TextGraph();
        textGraph.buildGraphFromFile(GRAPH_FILE_PATH);
    }

    @Test
    public void testQueryBridgeWordsMultipleBridges() {
        String word1 = "to";
        String word2 = "out";
        String expectedResult = "The bridge words from " + word1 + " to " + word2 + " are: explore, seek";
        String result = textGraph.queryBridgeWords(word1, word2);
        System.out.println("expectedResult:"+expectedResult);
        System.out.println("result        :"+result);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testQueryBridgeWordsNoWord1() {
        String word1 = "notexist";
        String word2 = "worlds";
        String expectedResult = "No " + word1 + " in the graph!";
        String result = textGraph.queryBridgeWords(word1, word2);
        System.out.println("expectedResult:"+expectedResult);
        System.out.println("result        :"+result);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testQueryBridgeWordsNoWord2() {
        String word1 = "new";
        String word2 = "notexist";
        String expectedResult = "No " + word2 + " in the graph!";
        String result = textGraph.queryBridgeWords(word1, word2);
        System.out.println("expectedResult:"+expectedResult);
        System.out.println("result        :"+result);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testQueryBridgeWordsNoBridge() {
        String word1 = "new";
        String word2 = "life";
        String expectedResult = "No bridge words from " + word1 + " to " + word2 + "!";
        String result = textGraph.queryBridgeWords(word1, word2);
        System.out.println("expectedResult:"+expectedResult);
        System.out.println("result        :"+result);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testQueryBridgeWordsExist() {
        String word1 = "life";
        String word2 = "new";
        String expectedResult = "The bridge words from " + word1 + " to " + word2 + " is: and";
        String result = textGraph.queryBridgeWords(word1, word2);
        System.out.println("expectedResult:"+expectedResult);
        System.out.println("result        :"+result);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testQueryBridgeWordsNONeighbour() {
        String word1 = "civilizations";
        String word2 = "new";
        String expectedResult = "No bridge words from " + word1 + " to " + word2 + "!";
        String result = textGraph.queryBridgeWords(word1, word2);
        System.out.println("expectedResult:"+expectedResult);
        System.out.println("result        :"+result);
        assertEquals(expectedResult, result);
    }

}
