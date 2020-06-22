package controller.tab;

import controller.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.*;

public class Tab3Controller {

    private MainController main;

    @FXML private TextArea txtAReport;

    //initialise the connection between this controller and the main.
    public void init(MainController mainController) {
        main = mainController;
    }

    //update the text area to contain the report.
    public void updateReportView() {
        String report = "";
        String line;
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader("./src/data/report/report.txt"));
            while ((line = buffRead.readLine()) != null) {
                report += line + "\n";
            }
            txtAReport.setText(report);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

