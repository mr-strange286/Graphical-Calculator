import javax.swing.*;
import java.awt.*;
import java.io.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CalculatorUI extends JFrame {

    DefaultListModel<String> historyModel;
    JList<String> historyList;

    java.util.ArrayList<JCheckBox> functionChecks = new java.util.ArrayList<>();
    java.util.ArrayList<String> functionNames = new java.util.ArrayList<>();
    JPanel functionPanel = new JPanel();

    JTextField functionField, rangeField, stepField;
    JButton plotButton, saveButton, themeButton;

    JSlider slider;
    JLabel sliderLabel;

    ChartPanel chartPanel;
    JFreeChart chart;

    boolean darkMode = false;

    Expression expression;

    public CalculatorUI() {

        setTitle("Graphical Calculator");
        setSize(900, 600);
        setLayout(new BorderLayout());
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.Y_AXIS));
        add(functionPanel, BorderLayout.EAST);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ================= TOP PANEL =================
        JPanel topPanel = new JPanel(new GridLayout(2, 4));

        functionField = new JTextField("sin(x)");
        rangeField = new JTextField("-10,10");
        stepField = new JTextField("0.1");

        plotButton = new JButton("Plot Graph");
        saveButton = new JButton("Save Graph");
        themeButton = new JButton("Dark Mode");

        topPanel.add(new JLabel("Function:"));
        topPanel.add(functionField);
        topPanel.add(new JLabel("Range:"));
        topPanel.add(rangeField);
        topPanel.add(new JLabel("Step:"));
        topPanel.add(stepField);
        topPanel.add(plotButton);
        topPanel.add(saveButton);

        add(topPanel, BorderLayout.NORTH);

        // ================= CHART =================
        chart = ChartFactory.createXYLineChart("Graph", "X", "Y", null);
        chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);

        // ================= SLIDER =================
        JPanel bottomPanel = new JPanel();

        slider = new JSlider();
        sliderLabel = new JLabel("x = 0 , y = 0");

        bottomPanel.add(sliderLabel);
        bottomPanel.add(slider);

        add(bottomPanel, BorderLayout.SOUTH);

        // ================= HISTORY =================
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);

        historyList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = historyList.getSelectedValue();
                    if (selected == null) return;

                    String[] parts = selected.split(" : ");
                    if (parts.length < 2) return;

                    functionField.setText(parts[1]);
                    plotGraph();
                }
            }
        });

        JScrollPane historyScroll = new JScrollPane(historyList);
        historyScroll.setPreferredSize(new Dimension(180, 0));
        add(historyScroll, BorderLayout.WEST);

        // ================= EVENTS =================
        plotButton.addActionListener(e -> plotGraph());
        saveButton.addActionListener(e -> saveGraph());
        themeButton.addActionListener(e -> toggleTheme());
        slider.addChangeListener(e -> updatePoint());

        setVisible(true);
    }

    // ================= PLOT GRAPH =================
    private void plotGraph() {
        try {

            String input = functionField.getText();
            String[] functions = input.split(",");

            historyModel.addElement("PLOT : " + input);

            String[] range = rangeField.getText().split(",");
            double start = Double.parseDouble(range[0]);
            double end = Double.parseDouble(range[1]);
            double step = Double.parseDouble(stepField.getText());

            slider.setMinimum((int) start);
            slider.setMaximum((int) end);


            XYSeriesCollection dataset = new XYSeriesCollection();

            Color[] colors = {
                    Color.RED, Color.BLUE, Color.GREEN,
                    Color.ORANGE, Color.MAGENTA, Color.CYAN
            };
            
            
            // only update panel if first time OR mismatch
            if (functionChecks.size() != functions.length) {

                functionPanel.removeAll();
                functionChecks.clear();
                functionNames.clear();

                for (String func : functions) {
                    func = func.trim();

                    JCheckBox box = new JCheckBox(func, true);

                    functionChecks.add(box);
                    functionNames.add(func);
                    functionPanel.add(box);
                }

                functionPanel.revalidate();
                functionPanel.repaint();
            }

            for (int i = 0; i < functionNames.size(); i++) {

                if (!functionChecks.get(i).isSelected()) continue;

                String func = functionNames.get(i);

                Expression expression = new ExpressionBuilder(func)
                        .variable("x")
                        .build();

                XYSeries series = new XYSeries(func);

                for (double x = start; x <= end; x += step) {
                    expression.setVariable("x", x);
                    double y = expression.evaluate();
                    series.add(x, y);
                }

                dataset.addSeries(series);
            }

            chart = ChartFactory.createXYLineChart(
                    "Graph",
                    "X",
                    "Y",
                    dataset
            );

            XYPlot plot = chart.getXYPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

            for (int i = 0; i < dataset.getSeriesCount(); i++) {
                renderer.setSeriesLinesVisible(i, true);
                renderer.setSeriesShapesVisible(i, false);
                renderer.setSeriesPaint(i, colors[i % colors.length]);
            }

            plot.setRenderer(renderer);
            chartPanel.setChart(chart);

            functionPanel.revalidate();
            functionPanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input!");
        }
    }

    // ================= SLIDER POINT =================
    private void updatePoint() {
        try {
            if (expression == null) return;

            double x = slider.getValue();
            expression.setVariable("x", x);
            double y = expression.evaluate();

            sliderLabel.setText("x = " + x + " , y = " + String.format("%.4f", y));

        } catch (Exception e) {}
    }

    // ================= SAVE GRAPH =================
    private void saveGraph() {
        try {
            ChartUtils.saveChartAsPNG(
                    new File("graph.png"),
                    chart,
                    800,
                    600
            );

            JOptionPane.showMessageDialog(this, "Saved!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving graph");
        }
    }

    // ================= DARK MODE =================
    private void toggleTheme() {

        darkMode = !darkMode;

        Color bg = darkMode ? Color.DARK_GRAY : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;

        getContentPane().setBackground(bg);

        functionField.setBackground(bg);
        rangeField.setBackground(bg);
        stepField.setBackground(bg);

        functionField.setForeground(fg);
        rangeField.setForeground(fg);
        stepField.setForeground(fg);

        sliderLabel.setForeground(fg);
    }
}