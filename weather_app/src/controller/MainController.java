package controller;

import javafx.fxml.FXML;
import controller.tab.Tab1Controller;
import controller.tab.Tab2Controller;
import controller.tab.Tab3Controller;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController {
    @FXML public Tab1Controller tab1Controller;
    @FXML public Tab2Controller tab2Controller;
    @FXML public Tab3Controller tab3Controller;

    @FXML private Tab secondTab;
    @FXML private Tab thirdTab;

    @FXML private TabPane tp;

    @FXML public void initialize() {
        tab1Controller.init(this);
        tab2Controller.init(this);
        tab3Controller.init(this);
    }

    //Call the initialise charts method in the tab 2 controller so that they can update when the selected station name changes.
    public void changeCharts(String newStationName) {
        tab2Controller.initialiseCharts(newStationName);
    }

    //Allow other controllers to access the station name that's currently selected.
    public String getStationName(){
        if (tab1Controller.cbStation.getSelectionModel().isEmpty()) {
            return null;
        } else {
            return tab1Controller.cbStation.getSelectionModel().getSelectedItem();
        }
    }

    //Call the update report method in tab 3.
    public void updateReport(){
        tab3Controller.updateReportView();
    }

    //Load a different tab.
    public void loadTab(int tabNumber) {
        if (tabNumber == 2) {
            tp.getSelectionModel().select(secondTab);
        }
        if (tabNumber == 3) {
            tp.getSelectionModel().select(thirdTab);
        }
    }
}