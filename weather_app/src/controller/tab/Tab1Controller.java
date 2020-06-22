package controller.tab;

import application.CSVReader;
import controller.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Tab1Controller {

    private MainController main;
    private CSVReader readerCSV;

    @FXML public ComboBox<String> cbStation;
    @FXML private Label lbMaxMean;
    @FXML private Label lbMinMean;
    @FXML private Label lbTotalFrost;
    @FXML private Label lbRainfall;
    @FXML private ImageView imgLogo;

    public void init(MainController mainController) {
        main = mainController;
    }

    @FXML
    private void initialize(){
        readerCSV = new CSVReader();

        //Update all the station names when the twab is loaded.
        cbStation.getItems().addAll(readerCSV.getStationNames());

        //Make sure the image is loaded.
        try {
            FileInputStream input = new FileInputStream("src/application/assets/images/weather.png");
            Image image = new Image(input);
            imgLogo.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Get the station name selected and update the labels with the required 2019 statistics.
    @FXML
    private void setLastYearStats() {
        String station = cbStation.getSelectionModel().getSelectedItem();
        String lastYearResults = readerCSV.statsForYear(station, "2019");

        if (lastYearResults == null) {
            lbMaxMean.setText("NaN");
            lbMinMean.setText("NaN");
            lbTotalFrost.setText("NaN");
            lbRainfall.setText("NaN");
        } else {
            String[] toUpdateTo = lastYearResults.split(",");
            lbMaxMean.setText(toUpdateTo[1]);
            lbMinMean.setText(toUpdateTo[2]);
            lbTotalFrost.setText(toUpdateTo[3]);
            lbRainfall.setText(toUpdateTo[4]);
        }

        //Make sure to update the charts also when the value changes.
        main.changeCharts(station);
    }

    //Load the graphs tab.
    @FXML private void viewGraphs() {
        main.loadTab(2);
    }

    //Create the report file and add the data to it.
    @FXML private void makeReport() {
        createReportFile();

        ArrayList<String> station_names = readerCSV.getStationNames();
        ArrayList<String> important_stats = readerCSV.getStatisticsAllStations();

        try {
            Writer output = new BufferedWriter(new FileWriter("src/data/report/report.txt",false));  //clears file every time
            appendHeader(output);

            for (int i=0; i<readerCSV.getNumCSVFiles(); i++) {
                String[] split_info = important_stats.get(i).split(",");
                if (split_info.length>1) {
                    output.append(makeRow(
                            Integer.toString(i+1),
                            station_names.get(i),
                            split_info[split_info.length-2],
                            split_info[split_info.length-1],
                            split_info[split_info.length-4],
                            split_info[split_info.length-3]
                    ));
                } else {
                    output.append(makeRow(
                            Integer.toString(i+1),
                            station_names.get(i),
                            "NaN",
                            "NaN",
                            "NaN",
                            "NaN"
                    ));
                }

            }
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Call the update method to add to the report to the TextArea in tab3.
        main.updateReport();
        //Load the tab once the text area has been updated.
        main.loadTab(3);
    }

    //Create the directory and file if either don't already exist.
    private void createReportFile() {
        File dir = new File("src/data/report");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File tmp = new File(dir, "report.txt");
            if (!tmp.exists()) {
                tmp.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Return an appendable and formatted row of data.
    private String makeRow(String c0, String c1, String c2, String c3, String c4, String c5) {
        String nameRow = "%-" + (maxNameLength()+1) + "s";
        return String.format("%-8s  " + nameRow + " %-16s  %-16s  %-20s  %-25s %n", c0, c1, c2, c3, c4, c5);
    }

    //Create and append the header to the report file.
    private void appendHeader(Writer output) {
        Date date = new Date();
        int count = 0;
        ArrayList<String> headers_to_print = new ArrayList<>();

        SimpleDateFormat todays_date = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = "Date Created: " + todays_date.format(date);

        headers_to_print.add(String.format("%-8s", "").replace(' ', '-'));
        headers_to_print.add(String.format("%-"+maxNameLength()+"s", "").replace(' ', '-'));
        headers_to_print.add(String.format("%-16s", "").replace(' ', '-'));
        headers_to_print.add(String.format("%-16s", "").replace(' ', '-'));
        headers_to_print.add(String.format("%-20s", "").replace(' ', '-'));
        headers_to_print.add(String.format("%-25s", "").replace(' ', '-'));

        for (String header : headers_to_print) {
            count += header.length() + 2;
        }

        try {
            String title = "Weather Data Report";
            output.append(String.format("%" + (count/2 + title.length()/2 - 2) + "s %n", title));
            output.append(String.format("%" + (count/2 + strDate.length()/2 - 2) + "s %n%n",strDate));

            String headers = makeRow("Number", "Station Name", "Highest (tmax)", "Lowest (tmin)", "Average Annual AF", "Average Annual Rainfall");
            output.append(headers);
            headers = "";
            for (String header : headers_to_print) {
                headers += (header + "  ");
            }
            output.append(headers).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Obtain the longest station names length.
    private int maxNameLength() {
        ArrayList<String> stations = readerCSV.getStationNames();
        int length = 0;
        for (String station : stations) {
            if (station.length() > length) {
                length = station.length();
            }
        }
        return length;
    }
}
