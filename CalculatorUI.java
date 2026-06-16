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

    JTextField functionField, rangeField, stepField;
    JButton plotButton, saveButton;
    JSlider slider;
    JLabel sliderLabel;
    ChartPanel chartPanel;
    JFreeChart chart;

    Expression expression;
    XYSeries functionSeries;
    XYSeries pointSeries;

    public CalculatorUI() {

        setTitle("Graphical Calculator");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel(new GridLayout(2, 4));

        functionField = new JTextField("sin(x)");
        rangeField = new JTextField("-10,10");
        stepField = new JTextField("0.1");

        plotButton = new JButton("Plot Graph");
        saveButton = new JButton("Save Graph");

        topPanel.add(new JLabel("Function:"));
        topPanel.add(functionField);
        topPanel.add(new JLabel("Range (start,end):"));
        topPanel.add(rangeField);

        topPanel.add(new JLabel("Step:"));
        topPanel.add(stepField);
        topPanel.add(plotButton);
        topPanel.add(saveButton);

        add(topPanel, BorderLayout.NORTH);

        chart = ChartFactory.createXYLineChart("Graph", "X", "Y", null);
        chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        slider = new JSlider();
        sliderLabel = new JLabel("x = 0 , y = 0");

        bottomPanel.add(sliderLabel);
        bottomPanel.add(slider);
        add(bottomPanel, BorderLayout.SOUTH);

        plotButton.addActionListener(e -> plotGraph());
        saveButton.addActionListener(e -> saveGraph());

        slider.addChangeListener(e -> updatePoint());

        setVisible(true);
    }

    private void plotGraph() {
        try {
            String function = functionField.getText();
            String[] range = rangeField.getText().split(",");

            double start = Double.parseDouble(range[0]);
            double end = Double.parseDouble(range[1]);
            double step = Double.parseDouble(stepField.getText());

            slider.setMinimum((int) start);
            slider.setMaximum((int) end);
            slider.setValue((int) start);

            functionSeries = new XYSeries("f(x)");
            pointSeries = new XYSeries("Point");

            expression = new ExpressionBuilder(function)
                    .variable("x")
                    .build();

            FileWriter writer = new FileWriter("data.csv");
            writer.write("Function: " + function + "\n");
            writer.write("x,y\n");

            for (double x = start; x <= end; x += step) {
                expression.setVariable("x", x);
                double y = expression.evaluate();

                functionSeries.add(x, y);
                writer.write(x + "," + y + "\n");
            }

            writer.close();

            double x0 = slider.getValue();
            expression.setVariable("x", x0);
            double y0 = expression.evaluate();
            pointSeries.add(x0, y0);

            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(functionSeries);
            dataset.addSeries(pointSeries);

            chart = ChartFactory.createXYLineChart(
                    "Graph of " + function,
                    "X", "Y",
                    dataset
            );

            XYPlot plot = chart.getXYPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

            renderer.setSeriesLinesVisible(0, true);
            renderer.setSeriesShapesVisible(0, false);

            renderer.setSeriesLinesVisible(1, false);
            renderer.setSeriesShapesVisible(1, true);

            plot.setRenderer(renderer);

            chartPanel.setChart(chart);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Function or Input!");
        }
    }

    private void updatePoint() {
        try {
            if (expression == null || pointSeries == null) return;

            double x = slider.getValue();
            expression.setVariable("x", x);
            double y = expression.evaluate();

            pointSeries.clear();
            pointSeries.add(x, y);

            sliderLabel.setText("Fixed x = " + x + " , f(x) = " + String.format("%.4f", y));

        } catch (Exception e) {
        }
    }

    private void saveGraph() {
        try {
            ChartUtils.saveChartAsPNG(
                    new File("graph.png"),
                    chart,
                    800,
                    600
            );

            JOptionPane.showMessageDialog(this, "Graph saved!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving graph");
        }
    }
}
