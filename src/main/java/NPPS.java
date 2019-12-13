import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections15.Factory;

import java.util.*;
import java.util.function.Function;


/**
 * The Class NPPS.
 *
 * @param <V> the value type
 * @param <E> the element type
 */
public class NPPS<V, E> {

    /**
     * The map.
     */
    Map<Integer, List<V>> map = new HashMap<Integer, List<V>>(); // Структура для хранения слоев вершин

    /**
     * The attach rule.
     */
    private Function<Integer, Double> attachRule; // Правило предпочтительного связывания

    /**
     * The vertex factory.
     */
    private Factory<V> vertexFactory; // Способ создания узла

    /**
     * The edge factory.
     */
    private Factory<E> edgeFactory; // Способ создания ребра

    /**
     * The num edges to attach.
     */
    private double numEdgesToAttach[]; // Вероятности задающие распределение для приращения

    /**
     * The m rand.
     */
    private Random mRand = new Random();

    /**
     * Instantiates a new npps.
     *
     * @param vertexFactory     the vertex factory
     * @param edgeFactory       the edge factory
     * @param probEdgesToAttach the prob edges to attach
     * @param attachRule        the attach rule
     */
    public NPPS(Factory<V> vertexFactory, Factory<E> edgeFactory, double[] probEdgesToAttach,
                Function<Integer, Double> attachRule) { // Инициация параметров
        this.vertexFactory = vertexFactory;
        this.edgeFactory = edgeFactory;
        this.numEdgesToAttach = probEdgesToAttach;
        this.attachRule = attachRule;
    }

//////////////////////////////////////////////////////////////////////////////////////

    /**
     * Функция добавления вершины в слой.
     *
     * @param n the n
     * @param i the i
     */
    private void addToLayer(V n, int i) {
        List<V> list = map.get(i);
        if (list == null) {
            list = new LinkedList<V>();
            map.put(i, list);
        }
        if (!list.contains(n)) {
            list.add(n);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the layer.
     *
     * @return k Функция получения    слоя вершин
     * сначала выбирантся слой , а  затем в выбраном слое вершина
     * @code sum - выбор нужный слой
     * @code tr - выбираем вершину для присоединения
     */
    private int getLayer() {
        {
            int k = 0;
            double rand = mRand.nextDouble();
            double tr = 0;
            double sum = 0.0;
            for (int op : map.keySet())
                sum = sum + attachRule.apply(op) * map.get(op).size();
            for (int l : map.keySet()) {
                int A = map.get(l).size();
                tr = tr + ((double) A * attachRule.apply(l)) / sum;
                if (rand < tr) {
                    k = l;
                    break;
                }
            }
            return k;
        }
    }

    /**
     * Evolve.
     *
     * @param step  the step
     * @param graph the graph
     * @return graph
     */
    public Graph<V, E> evolve(int step, Graph<V, E> graph) { // функция генерации
        for (Iterator<V> iterator = graph.getVertices().iterator(); iterator.hasNext(); ) {// Заполняем слои
            V v = iterator.next();
            addToLayer(v, graph.degree(v)); // нвершины и ее степень
        }

        // итерационный процесс добавления приращений

        for (int i = 0; i < step; i++) { // step=10000
            V new_n = vertexFactory.create();// создаем узел приращения
            Set<V> list = new HashSet<V>(); // вспомогательный список для хранения выбранных вершин
            // разыгрываю случайную число ребер приращения по распределению numEdgesToAttach
            double s = 0.0;
            double r = mRand.nextDouble();
            int addEd = 0;
            for (int j = 0; j < numEdgesToAttach.length; j++) {
                s = s + numEdgesToAttach[j];
                if (s > r) {
                    addEd = j;
                    break;
                }
            }
            // цикл выбора вершин
            do {
                int k = getLayer(); // разыгрываю слой, после чего выбираю вершину
                V n = map.get(k).get(mRand.nextInt(map.get(k).size())); // из слоя - равновероятно
                list.add(n); // добавляю в вспомогательный список
            }

            while (list.size() != addEd); // пока не выбрано added вершин для присоединения
            graph.addVertex(new_n); // добавление новой вершины к графу
            for (V n : list) {// добавляю ребра и корректирую список слоев
                int tec = graph.degree(n);
                graph.addEdge(edgeFactory.create(), new_n, n);
                map.get(tec).remove(n);///// ???????????
                addToLayer(n, tec + 1);
            }
            addToLayer(new_n, addEd);
        }
        return graph;
    }

}
