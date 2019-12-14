import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.collections15.Factory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Function;


// TODO: Auto-generated Javadoc

/**
 * * Реализовать графическое приложение, реализующее генерацию
 * графа с нелинейным правилом предпочтительного связывания,
 * а также вывести распределение связности вершин
 * с использованием библиотеки Jung.
 *
 * @author Sharipova Mainura
 * The Class RunItDegree.
 */
public class RunItDegree {

    /**
     * The m.
     */
    static int m = 0;

    /**
     * The n.
     */
    static int N = 0;

    /**
     * The edge number.
     */
    static int edgeNumber = 0;

    /**
     * The vertex factory.
     */
    static Factory<Integer> vertexFactory = new Factory<Integer>() { // фабрика для создания вершин
        int i = 0;

        public Integer create() {
            return new Integer(i++);
        }
    };

    /**
     * The edge factory.
     */
    static Factory<Integer> edgeFactory = new Factory<Integer>() { // фабрика для создания ребер
        int i = 0;

        public Integer create() {
            return new Integer(i++);
        }
    };

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("ГЕНЕРАЦИЯ ГРАФА  С НППС");

        RowLayout rowLayout = new RowLayout();
        rowLayout.marginLeft = 10;
        rowLayout.marginTop = 10;
        rowLayout.spacing = 15;
        shell.setLayout(rowLayout);


        Label label = new Label(shell, SWT.NONE);
        label.setText("Число  вершин графа  с НППС:");


        Spinner spinner = new Spinner(shell, SWT.BORDER);
        spinner.setMinimum(1);
        spinner.setMaximum(10000);
        N = spinner.getSelection();

        Font font = new Font(display, "Courier", 20, SWT.NORMAL);
        spinner.setFont(font);

        Label label2 = new Label(shell, SWT.NONE);
        label2.setText("Число  вершин графа затравки:");

        Spinner spinner2 = new Spinner(shell, SWT.BORDER);
        spinner2.setMinimum(-9);
        spinner2.setMaximum(-4);
        spinner2.setSelection(-5);

        Font font2 = new Font(display, "Courier", 20, SWT.NORMAL);
        spinner2.setFont(font2);
        m = spinner2.getSelection();

        // Button 1
        final Button button1 = new Button(shell, SWT.PUSH);
        button1.setText("Сгенерировать Граф НППС");

        Font font3 = new Font(display, "Courier", 10, SWT.NORMAL);
        button1.setFont(font3);


        button1.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {

                Function<Integer, Double> pa = (k) -> Math.log(k);
                double[] r = new double[]{0, 0, 1};

                NPPS<Integer, Integer> gen = new NPPS<Integer, Integer>(vertexFactory, edgeFactory, r, pa);
                Graph<Integer, Integer> gr = gen.evolve(spinner.getSelection(), seed_graph(spinner2.getSelection())); // генерируем граф из 10000 вершин

                frame(gr);
            }
        });

        shell.setSize(500, 350);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }


///////////////////////////////////////// функция для генерации графа-затравки

    /**
     * Seed graph.
     *
     * @param m the m
     * @return функция генерации графа затравки
     */
    static Graph<Integer, Integer> seed_graph(int m) {
        Graph<Integer, Integer> gr = new UndirectedSparseGraph<Integer, Integer>();
        for (int i = -1; i > m; i--) { // добавляем 7 узлов
            Integer n = new Integer(i); // пусть они идентифицируются 7ю отрицательными числами
            gr.addVertex(n);
        }
        int l = -1;
        Object[] mass = gr.getVertices().toArray(); // добавляем ребра между добавленными узлами
        for (int i = 0; i < mass.length - 1; i++)
            for (int j = i + 1; j < mass.length; j++)
                if (i != j)
                    gr.addEdge(new Integer(l--), (Integer) mass[i], (Integer) mass[j]);
        return gr;

    }

    /**
     * Gets the degree chart.
     *
     * @param gr the gr
     * @return гистограммы распределения связоности вершин графа
     */

    private static ChartPanel getDegreeChart(Graph<Integer, Integer> gr) {
        // TODO Auto-generated method stub
        XYSeries series1 = new XYSeries("Степени связности вершин графа с НППС");
        int max = 0;

        int k = 0;
        for (Integer v : gr.getVertices()) {
            k = (int) (gr.getVertexCount() / 2 * (m + 1) * Math.log(N));
            k = (int) ((int) gr.degree(v) / 2 * (3) * Math.log(gr.degree(v)));

            if (k > max) max = k;
        }
        k = 0;
        double[] mass = new double[max + 1];
        for (Integer v : gr.getVertices()) {
            k = (int) ((int) gr.degree(v) / 2 * (3) * Math.log(gr.degree(v)));
            mass[k] = mass[k] + 1.0;
            //  System.out.println(mass[k]);

        }
		/*for(Integer v:gr.getVertices())
		{
			int k = gr.degree(v);

			if(k>max)max=k;
		}

		int[] mass=new int[max+1];

		for(Integer v:gr.getVertices()){
			int k = gr.degree(v);
			mass[ k]=mass[k]+1;

		}*/
        for (int i = 0; i < mass.length; i++) {
            series1.add(i, mass[i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        JFreeChart chart = ChartFactory.createHistogram("NPPS", "x", "y", dataset, PlotOrientation.VERTICAL, true, false, false);

        return new ChartPanel(chart);
    }

    /**
     * Creates the chart panel 11.
     *
     * @param gr the gr
     * @return the j panel
     */
    protected static JPanel createChartPanel11(Graph<Integer, Integer> gr) {

        final ChartPanel chartPanel = getDegreeChart(gr);

        final JTextArea jTextPaneConsole11 = new JTextArea();
        jTextPaneConsole11.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 15));
        jTextPaneConsole11.setRows(10);

        for (Integer vertex : gr.getVertices()) {
            jTextPaneConsole11.append("Вершина " + vertex + "  ее степень связности  : " + gr.degree(vertex) + "\n");
        }
        edgeNumber = gr.getEdgeCount();
        jTextPaneConsole11.append(" Число ребер в графе =" + edgeNumber + "\n");


        Box controls = Box.createVerticalBox();
        controls.add(jTextPaneConsole11);


        JScrollPane jSPane = new JScrollPane(jTextPaneConsole11);
        controls.add(jSPane);

        JScrollPane jScrollPane2 = new JScrollPane(chartPanel);

        JPanel pan = new JPanel(new BorderLayout());
        pan.add(jScrollPane2, BorderLayout.NORTH);
        pan.add(controls);
        return pan;

    }

    /**
     * Frame.
     *
     * @param gr the gr
     * @return функция отрисовки графа с НППС
     */
    public static void frame(Graph<Integer, Integer> gr) {

        Layout<Integer, String> layout = new KKLayout(gr);
        layout.setSize(new Dimension(850, 850));
        VisualizationViewer<Integer, String> vv = new VisualizationViewer<Integer, String>(layout);
        vv.setPreferredSize(new Dimension(850, 850));
// Show vertex and edge labels
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

        JFrame frame = new JFrame("NPPS Degrees");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(createChartPanel11(gr));
        frame.pack();
        frame.setVisible(true);
        JFrame frame11 = new JFrame("NPPS ");
        frame11.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame11.getContentPane().add(vv);
        frame11.pack();
        frame11.setVisible(true);
    }

    /**
     * Test degree graph.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
}


// mvn exec:java -Dexec.mainClass="com.app.NPGraph"