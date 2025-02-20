package org.iquantum.examples.quantum;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TaskMetrics {
    private String taskName;
    private double executionTime;
    private double cost;
    private double waitingTime;

    public TaskMetrics(String taskName, double executionTime, double cost, double waitingTime) {
        this.taskName = taskName;
        this.executionTime = executionTime;
        this.cost = cost;
        this.waitingTime = waitingTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public double getExecutionTime() {
        return executionTime;
    }

    public double getCost() {
        return cost;
    }

    public double getWaitingTime() {
        return waitingTime;
    }
}

class MetricsFetcher {
    private List<TaskMetrics> metricsList;

    public MetricsFetcher() {
        metricsList = new ArrayList<>();
    }

    public void fetchMetricsFromSimulation(String useCase) {
        for (int i = 1; i <= 10; i++) {
            double executionTime = Math.random() * 1000;
            double cost = Math.random() * 500;
            double waitingTime = Math.random() * 300;
            metricsList.add(new TaskMetrics(useCase + " - Task " + i, executionTime, cost, waitingTime));
        }
    }

    public List<TaskMetrics> getMetrics() {
        return metricsList;
    }
}

public class MetricsComparison extends Application {

    @Override
    public void start(Stage primaryStage) {
        MetricsFetcher fetcher8 = new MetricsFetcher();
        MetricsFetcher fetcher9 = new MetricsFetcher();

        fetcher8.fetchMetricsFromSimulation("iQuantumExample8");
        fetcher9.fetchMetricsFromSimulation("iQuantumExample9");

        // Execution Time Chart
        BarChart<String, Number> executionTimeChart = createExecutionTimeChart(fetcher8, fetcher9);
        Stage executionTimeStage = new Stage();
        executionTimeStage.setTitle("Execution Time Comparison");
        executionTimeStage.setScene(new Scene(executionTimeChart, 800, 600));
        executionTimeStage.show();

        // Cost Chart
        BarChart<String, Number> costChart = createCostChart(fetcher8, fetcher9);
        Stage costStage = new Stage();
        costStage.setTitle("Cost Comparison");
        costStage.setScene(new Scene(costChart, 800, 600));
        costStage.show();

        // Waiting Time Chart
        BarChart<String, Number> waitingTimeChart = createWaitingTimeChart(fetcher8, fetcher9);
        Stage waitingTimeStage = new Stage();
        waitingTimeStage.setTitle("Waiting Time Comparison");
        waitingTimeStage.setScene(new Scene(waitingTimeChart, 800, 600));
        waitingTimeStage.show();

        // Save charts as images
        saveChartAsImage(executionTimeChart, "C://Desktop//CN/execution_time_comparison2.png");
        saveChartAsImage(costChart, "C://Desktop//CN/cost_comparison2.png");
        saveChartAsImage(waitingTimeChart, "C://Desktop//CN/waiting_time_comparison3.png");
    }

    private BarChart<String, Number> createExecutionTimeChart(MetricsFetcher fetcher8, MetricsFetcher fetcher9) {
        NumberAxis yAxisExecutionTime = new NumberAxis();
        yAxisExecutionTime.setLabel("Execution Time (ms)");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tasks");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxisExecutionTime);
        chart.setTitle("Execution Time Comparison");

        XYChart.Series<String, Number> series8 = new XYChart.Series<>();
        series8.setName("FCFS - Execution Time");

        XYChart.Series<String, Number> series9 = new XYChart.Series<>();
        series9.setName("RoundRobin - Execution Time");

        for (int i = 0; i < fetcher8.getMetrics().size(); i++) {
            String combinedTaskName = "Task " + (i + 1);
            TaskMetrics metrics8 = fetcher8.getMetrics().get(i);
            TaskMetrics metrics9 = fetcher9.getMetrics().get(i);

            series8.getData().add(new XYChart.Data<>(combinedTaskName, metrics8.getExecutionTime()));
            series9.getData().add(new XYChart.Data<>(combinedTaskName, metrics9.getExecutionTime()));
        }

        chart.getData().addAll(series8, series9);
        return chart;
    }

    private BarChart<String, Number> createCostChart(MetricsFetcher fetcher8, MetricsFetcher fetcher9) {
        NumberAxis yAxisCost = new NumberAxis();
        yAxisCost.setLabel("Cost (Units)");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tasks");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxisCost);
        chart.setTitle("Cost Comparison");

        XYChart.Series<String, Number> series8 = new XYChart.Series<>();
        series8.setName("FCFS - Cost");

        XYChart.Series<String, Number> series9 = new XYChart.Series<>();
        series9.setName("RoundRobin - Cost");

        for (int i = 0; i < fetcher8.getMetrics().size(); i++) {
            String combinedTaskName = "Task " + (i + 1);
            TaskMetrics metrics8 = fetcher8.getMetrics().get(i);
            TaskMetrics metrics9 = fetcher9.getMetrics().get(i);

            series8.getData().add(new XYChart.Data<>(combinedTaskName, metrics8.getCost()));
            series9.getData().add(new XYChart.Data<>(combinedTaskName, metrics9.getCost()));
        }

        chart.getData().addAll(series8, series9);
        return chart;
    }

    private BarChart<String, Number> createWaitingTimeChart(MetricsFetcher fetcher8, MetricsFetcher fetcher9) {
        NumberAxis yAxisWaitingTime = new NumberAxis();
        yAxisWaitingTime.setLabel("Waiting Time (ms)");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tasks");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxisWaitingTime);
        chart.setTitle("Waiting Time Comparison");

        XYChart.Series<String, Number> series8 = new XYChart.Series<>();
        series8.setName("FCFS - Waiting Time");

        XYChart.Series<String, Number> series9 = new XYChart.Series<>();
        series9.setName("RoundRobin - Waiting Time");

        for (int i = 0; i < fetcher8.getMetrics().size(); i++) {
            String combinedTaskName = "Task " + (i + 1);
            TaskMetrics metrics8 = fetcher8.getMetrics().get(i);
            TaskMetrics metrics9 = fetcher9.getMetrics().get(i);

            series8.getData().add(new XYChart.Data<>(combinedTaskName, metrics8.getWaitingTime()));
            series9.getData().add(new XYChart.Data<>(combinedTaskName, metrics9.getWaitingTime()));
        }

        chart.getData().addAll(series8, series9);
        return chart;
    }

    private void saveChartAsImage(BarChart<String, Number> barChart, String filePath) {
        WritableImage image = barChart.snapshot(null, null);
        File file = new File(filePath);
        try {
            ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Chart saved as " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
