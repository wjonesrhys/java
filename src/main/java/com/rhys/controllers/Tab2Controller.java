package com.rhys.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Window;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import com.rhys.CSVReader;

public class Tab2Controller {
    private MainController main;
    private CSVReader CSVReader;

    private int[] firstTimeCalled;

    private ObservableList<XYChart.Series<String, Float>> firstLineChartData;
    private ObservableList<XYChart.Series<String, Float>> secondLineChartData;
    private ObservableList<XYChart.Series<String, Float>> stackedBarChartData;
    private ObservableList<XYChart.Series<String, Float>> barChartData;

    private ArrayList<XYChart.Data<String, Float>> list1;
    private ArrayList<XYChart.Data<String, Float>> list2;
    private ArrayList<XYChart.Data<String, Float>> list3;
    private ArrayList<XYChart.Data<String, Float>> list4;

    @FXML private LineChart<String, Float> firstLineChart;
    @FXML private LineChart<String, Float> secondLineChart;
    @FXML private StackedBarChart<String, Float> stackedBarChart;
    @FXML private BarChart<String, Float> barChart;

    @FXML private ComboBox<String> comboSelect1;
    @FXML private ComboBox<String> comboSelect2;
    @FXML private ComboBox<String> comboSelect3;
    @FXML private ComboBox<String> comboSelect4;

    public void init(MainController mainController) {
        main = mainController;
        CSVReader = new CSVReader();

        firstLineChartData = FXCollections.observableArrayList();
        secondLineChartData = FXCollections.observableArrayList();
        stackedBarChartData = FXCollections.observableArrayList();
        barChartData = FXCollections.observableArrayList();

        //Prevent the charts from being animated, just looks smoother.
        setChartsNotAnimated();

        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();

        firstTimeCalled = new int[]{-1,-1,-1,-1};
    }

    //Initialise the charts to the 2019 data relevant to the station selected on Tab1.
    public void initialiseCharts(String stationName) {
        //Reset the first time called Array.
        for (int i=0; i<4; i++) {
            firstTimeCalled[i] = -1;
        }

        clearEverything(firstLineChart, firstLineChartData, list1, comboSelect1);
        clearEverything(secondLineChart, secondLineChartData, list2, comboSelect2);
        clearEverything(stackedBarChart, stackedBarChartData, list3, comboSelect3);
        clearEverything(barChart, barChartData, list4, comboSelect4);

        //Get the yearly statistics required for the station selected.
        ArrayList<String> values = CSVReader.getYearlyStationStats(stationName);

        //Separate the statistics result and then add the relevant data to each separate data set provided data exists for that station.
        if (!values.isEmpty()) {
            for (String value : values) {
                String[] coord = value.split(",");
                list1.add(new XYChart.Data<>(coord[0], Float.parseFloat(coord[1])));
                list2.add(new XYChart.Data<>(coord[0], Float.parseFloat(coord[2])));
                list3.add(new XYChart.Data<>(coord[0], Float.parseFloat(coord[3])));
                list4.add(new XYChart.Data<>(coord[0], Float.parseFloat(coord[4])));
            }

            firstLineChartData.add(new LineChart.Series<>(FXCollections.observableList(list1)));
            secondLineChartData.add(new LineChart.Series<>(FXCollections.observableList(list2)));
            stackedBarChartData.add(new LineChart.Series<>(FXCollections.observableList(list3)));
            barChartData.add(new LineChart.Series<>(FXCollections.observableList(list4)));

            firstLineChart.getData().setAll(firstLineChartData);
            secondLineChart.getData().setAll(secondLineChartData);
            stackedBarChart.getData().setAll(stackedBarChartData);
            barChart.getData().setAll(barChartData);

            ArrayList<String> stations = CSVReader.getAllYears(stationName);
            for (String station : stations) {
                comboSelect1.getItems().add(station);
                comboSelect2.getItems().add(station);
                comboSelect3.getItems().add(station);
                comboSelect4.getItems().add(station);
            }

            //Add the years there exists data for to the ComboBox's
            comboSelect1.getItems().add("All years");
            comboSelect2.getItems().add("All years");
            comboSelect3.getItems().add("All years");
            comboSelect4.getItems().add("All years");

            //Set relevant Labels for the axis.
            firstLineChart.getYAxis().setLabel("Temperature (℃)");
            firstLineChart.getXAxis().setLabel("Year");

            secondLineChart.getYAxis().setLabel("Temperature (℃)");
            secondLineChart.getXAxis().setLabel("Year");

            stackedBarChart.getYAxis().setLabel("Total Frost Days");
            stackedBarChart.getXAxis().setLabel("Year");

            barChart.getYAxis().setLabel("Total Rainfall (mm)");
            barChart.getXAxis().setLabel("Year");

            //Make sure the title is correct for each graph.
            firstLineChart.setTitle("Highest Max Mean Temperature vs. Years");
            secondLineChart.setTitle("Lowest Min Mean Temperature vs. Years");
            stackedBarChart.setTitle("Total Frost Days vs. Years");
            barChart.setTitle("Total Rainfall vs. Years");
        }

        //Hide the legend as it's only one set of data.
        firstLineChart.setLegendVisible(false);
        secondLineChart.setLegendVisible(false);
        stackedBarChart.setLegendVisible(false);
        barChart.setLegendVisible(false);
    }

    //Make sure the charts aren't animated upon the Tab loading.
    private void setChartsNotAnimated() {
        firstLineChart.setAnimated(false);
        secondLineChart.setAnimated(false);
        stackedBarChart.setAnimated(false);
        barChart.setAnimated(false);
    }

    //Add data to a chart for the ComboBox selection
    @FXML
    private void addDataToChart(ActionEvent event) {
        String stationName = main.getStationName();
        Button btn = (Button) event.getSource();
        String year_selected;

        if (btn.getId().compareTo("btnAdd1")==0) {
            year_selected = comboSelect1.getSelectionModel().getSelectedItem();
            //Clear the data if the chart hasn't been added to yet.
            clearDataOnly(0);
            fillChart(stationName,2, year_selected, comboSelect1, firstLineChart, firstLineChartData);
        }

        if (btn.getId().compareTo("btnAdd2")==0) {
            year_selected = comboSelect2.getSelectionModel().getSelectedItem();
            clearDataOnly(1);
            fillChart(stationName,3, year_selected, comboSelect2, secondLineChart, secondLineChartData);
        }

        if (btn.getId().compareTo("btnAdd3")==0) {
            year_selected = comboSelect3.getSelectionModel().getSelectedItem();
            clearDataOnly(2);
            fillChart(stationName,4,  year_selected, comboSelect3, stackedBarChart, stackedBarChartData);
        }

        if (btn.getId().compareTo("btnAdd4")==0) {
            year_selected = comboSelect4.getSelectionModel().getSelectedItem();
            clearDataOnly(3);
            fillChart(stationName,5, year_selected, comboSelect4, barChart, barChartData);
        }
    }

    //Revert the charts to the state when the comboBox was selected.
    @FXML private void revertCharts() {
        String stationName = main.getStationName();
        initialiseCharts(stationName);
    }

    //Clear the Data for the Chart and the Axis and Title at the same time.
    @FXML
    private void clearChart(ActionEvent event) {
        Button btn = (Button) event.getSource();

        if (btn.getId().compareTo("btnClear1")==0) {
            firstLineChartData.clear();
            firstLineChart.getData().clear();
            clearAxis(firstLineChart);
        }

        if (btn.getId().compareTo("btnClear2")==0) {
            secondLineChartData.clear();
            secondLineChart.getData().clear();
            clearAxis(secondLineChart);
        }

        if (btn.getId().compareTo("btnClear3")==0) {
            stackedBarChartData.clear();
            stackedBarChart.getData().clear();
            clearAxis(stackedBarChart);
        }

        if (btn.getId().compareTo("btnClear4")==0) {
            barChartData.clear();
            barChart.getData().clear();
            clearAxis(barChart);
        }
    }

    //Fill the chart with data.
    private void fillChart(String stationName, int index, String year_selected, ComboBox<String> newBox, XYChart<String, Float> chart, ObservableList<XYChart.Series<String, Float>> observable_list){
        ArrayList<XYChart.Data<String, Float>> list;
        if (stationName != null && !newBox.getSelectionModel().isEmpty()) {
            //If they want to add all years to the graph
            if (year_selected.compareTo("All years")==0){
                //Get all the years available
                for (String year : CSVReader.getAllYears(stationName)){
                    //Check they aren't already on the chart.
                    if(!checkChartContains(year, observable_list)) {
                        //Get the data for the ComboBox selection.
                        list = getDataForYearsSelected(stationName, year, index);
                        //Make sure to add the new series to the relevant chart.
                        observable_list.add(new LineChart.Series<>(year, FXCollections.observableList(list)));
                    }
                }
            } else {
                //Get data for the lone selected year.
                list = getDataForYearsSelected(stationName, year_selected, index);
                //Get the current window and display an alert if there are issues
                Window current = chart.getScene().getWindow();
                checkValidity(current, year_selected, FXCollections.observableList(list), observable_list);
            }
            chart.getXAxis().setLabel("Month");
            if (index == 2) {
                chart.setTitle("Highest Max Mean Temperature vs. Months");
            }
            if (index == 3) {
                chart.setTitle("Lowest Min Mean Temperature vs. Months");
            }
            if (index == 4) {
                chart.setTitle("Total Frost Days vs. Months");
            }
            if (index == 5) {
                chart.setTitle("Total Rainfall vs. Months");
            }
        }
        chart.getData().setAll(observable_list);
    }

    //Return a list containing the data for the ComboBox year selection.
    private ArrayList<XYChart.Data<String, Float>> getDataForYearsSelected(String stationName, String year_selected, int indexOfData) {
        //Obtain the data for year/all years selected.
        ArrayList<String> selectedData = CSVReader.getAllDataForStation(stationName, year_selected);

        //Create a new list to add the data to.
        ArrayList<XYChart.Data<String, Float>> list = new ArrayList<>();
        for (String data : selectedData) {
            String[] split_data = data.split(",");

            //Add the month and the relevant statistic based on the index passed in.
            list.add(new XYChart.Data<>(changeToMonth(split_data[1]), Float.parseFloat(split_data[indexOfData])));
        }
        return list;
    }

    //Clear the Data only and make sure the first time called values are changed if it's the first call.
    private void clearDataOnly(Integer number) {
        if ((firstTimeCalled[number] == -1) && (number == 0)) {
            firstLineChart.getData().clear();
            firstLineChartData.clear();
            firstTimeCalled[number] = 1;
            firstLineChart.setLegendVisible(true);
        }

        if ((firstTimeCalled[number] == -1) && (number == 1)) {
            secondLineChart.getData().clear();
            secondLineChartData.clear();
            firstTimeCalled[number] = 1;
            secondLineChart.setLegendVisible(true);
        }

        if ((firstTimeCalled[number] == -1) && (number == 2)) {
            stackedBarChart.getData().clear();
            stackedBarChartData.clear();
            firstTimeCalled[number] = 1;
            stackedBarChart.setLegendVisible(true);
        }

        if ((firstTimeCalled[number] == -1) && (number == 3)) {
            barChart.getData().clear();
            barChartData.clear();
            firstTimeCalled[number] = 1;
            barChart.setLegendVisible(true);
        }
    }

    //Remove the axis labels and the title.
    private void clearAxis(XYChart<String, Float> chart) {
        chart.getYAxis().setLabel("");
        chart.getXAxis().setLabel("");
        chart.setTitle("");
    }

    //Clear the Charts, ComboBox's and Data.
    private void clearEverything(XYChart<String,Float> chart,
                                 ObservableList<XYChart.Series<String, Float>> chartData,
                                 ArrayList<XYChart.Data<String, Float>> series_list,
                                 ComboBox<String> comboBox) {
        chart.getData().clear();
        chartData.clear();
        series_list.clear();
        comboBox.getItems().clear();
    }

    //Convert an integer to the name of the month and return it.
    private String changeToMonth(String number) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        return months[Integer.parseInt(number)-1];
    }

    private static void showAlert(Window owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    //Check the chart already contains a set of data.
    private boolean checkChartContains(String year_selected, ObservableList<XYChart.Series<String, Float>> chartToAddTo) {
        for (LineChart.Series<String, Float> series1 : chartToAddTo) {
            if(series1.getName().compareTo(year_selected)==0){
                return true;
            }
        }
        return false;
    }

    //Make sure that relevant alerts are shown if:
    //The data is already on the chart
    //There are already 3 sets or more on the chart.
    private void checkValidity(Window current, String year_selected, ObservableList<XYChart.Data<String,Float>> observableFloatList, ObservableList<XYChart.Series<String, Float>> chartToAddTo) {
        boolean alreadyContains = checkChartContains(year_selected, chartToAddTo);
        if (alreadyContains && chartToAddTo.size()>=3) {
            showAlert(current, "Unnecessary Extra Data", "The graph already contains 3 or more sets of data, you cannot add more.");
        } else if (chartToAddTo.size()>=3) {
            showAlert(current, "Unnecessary Extra Data", "The graph already contains 3 or more sets of data, you cannot add more.");
        } else if (alreadyContains) {
            showAlert(current, "Already Exists", "The graph already contains this data, it cannot be added again.");
        }

        //Add the data to the chart if it satisfies the conditions.
        if (!alreadyContains && chartToAddTo.size()<3) {
            chartToAddTo.add(new LineChart.Series<>(year_selected, observableFloatList));
        }
    }
}
