import edu.uci.ics.jung.graph.Graph;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.function.Function;

public class TestJUnit extends RunItDegree {

    @Test
    public void testDegreeGraph() throws IOException {
        Function<Integer, Double> pa = (k) -> Math.log(k);
        double[] r = new double[]{0, 0, 1};// задание вероятностей для ребер в приращении
        NPPS<Integer, Integer> gen = new NPPS(vertexFactory, edgeFactory, r, pa);
        Graph<Integer, Integer> gr = gen.evolve(N, seed_graph(m));
        Assert.assertEquals(gr.getEdgeCount(), edgeNumber);
    }

    @Test
    public void testDegreeGraph2() throws IOException {
        Function<Integer, Double> pa = (k) -> Math.log(k);
        double[] r = new double[]{0, 0, 1};// задание вероятностей для ребер в приращении
        NPPS<Integer, Integer> gen = new NPPS(vertexFactory, edgeFactory, r, pa);
        Graph<Integer, Integer> gr = gen.evolve(N, seed_graph(m));
        Assert.assertEquals(gr.getVertexCount(), N);
    }
}
