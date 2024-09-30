package com.rhys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class CSVReader {
    //location of the data folder
    private static final String pathToData = "java/weatherapp/src/main/resources/com/rhys/data/";

    //Obtain the important statistics for a station in particular for a specific year.
    public String statsForYear(String stationName, String year) {
        //Get a list of all important statistics for every year.
        ArrayList<String> allYearsData = getYearlyStationStats(stationName);
        String lastYearResults = null;

        //If not empty, return the data relevant to the year passed in.
        if (!allYearsData.isEmpty()) {
            for (String data : allYearsData) {
                String data_year = data.substring(0, 4);
                if (data_year.compareTo(year) == 0) {
                    lastYearResults = data;
                }
            }
        }

        return lastYearResults;
    }

    //Get all the important information for each year for a specific station (year, tmax, tmin, totalfrostdays, totalrainfall, yearlyaverage_fd, yearlyaverage_rf, yearmonth_tmax, yearmonth_tmin)
    public ArrayList<String> getYearlyStationStats(String stationName) {
        //Get all data for the station.
        ArrayList<String> all_station_data = getAllDataForStation(stationName, "All years");
        //Get all the years.
        ArrayList<String> years = getAllYears(stationName);

        ArrayList<String> sub_data = new ArrayList<>();
        ArrayList<String> valuesToPlot = new ArrayList<>();

        for (String year : years) {
            if (!all_station_data.isEmpty()) {
                for (String data : all_station_data) {
                    //If the string of data is equal to the year in all years, add it to sub_data
                    if (year.compareTo(data.substring(0, 4)) == 0) {
                        sub_data.add(data);
                    }
                }
                //Once you have all the data for a particular year, get stats for that particular year from the sub_data.
                valuesToPlot.add(year + "," + getStatsForYearOrAllYears(sub_data));
            }
            //clear the sub_data and start again for the next year until no years are left.
            sub_data.clear();
        }

        return valuesToPlot;
    }

    //Returns a list of the yearly average statistics for the report.
    public ArrayList<String> getStatisticsAllStations() {
        ArrayList<String> stations = getStationFileNames();
        ArrayList<String> statistics = new ArrayList<>();
        ArrayList<String> all_station_data = new ArrayList<>();
        String line;

        for (String station : stations) {
            try {
                BufferedReader bre = new BufferedReader(new FileReader(pathToData + station));
                while ((line = bre.readLine()) != null) {
                    all_station_data.add(line);
                }
                if (all_station_data.size()>0) {
                    String important_stats = getStatsForYearOrAllYears(all_station_data);
                    all_station_data = new ArrayList<>();
                    statistics.add(important_stats);
                } else {
                    statistics.add("NaN");
                }
                bre.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return statistics;
    }

    //Works out the important statistics for the years of results passed in
    public String getStatsForYearOrAllYears(ArrayList<String> yearsOfResults) {
        float max_mean = 0.0f;
        float min_mean = Float.parseFloat(yearsOfResults.get(0).split(",")[3]);
        float af_days = 0;
        float total_rain = 0.0f;
        float number_of_days = 0;

        String md_min = "";
        String md_max = "";


        if (yearsOfResults.size() > 0) {
            String[] first_year = yearsOfResults.get(0).split(",");
            md_min = first_year[1] + "/" + first_year[0];
        }

        for (String years_results : yearsOfResults) {
            String[] monthResults = years_results.split(",");
            if (max_mean < Float.parseFloat(monthResults[2])) {
                max_mean = Float.parseFloat(monthResults[2]);
                md_max = monthResults[1] + "/" + monthResults[0];
            }

            if (min_mean > Float.parseFloat(monthResults[3])) {
                min_mean = Float.parseFloat(monthResults[3]);
                md_min = monthResults[1] + "/" + monthResults[0];
            }

            af_days += Integer.parseInt(monthResults[4]);
            total_rain += Float.parseFloat(monthResults[5]);
            //Make sure the number of days is relative to whether data stops anytime before the year ends
            number_of_days += monthToDay(monthResults[1]);
        }

        return "" + max_mean + "," +
                min_mean + "," +
                String.format("%.0f", af_days) + "," +
                //Format float to 1 decimal place.
                String.format("%.1f", total_rain) + "," +
                String.format("%.1f", af_days/(number_of_days/365)) + "," +
                String.format("%.2f", total_rain/(number_of_days/365)) + "," +
                md_max + "," +
                md_min;
    }

    //Obtain all data for a station from the respective CSV file for either one year or all years.
    public ArrayList<String> getAllDataForStation(String stationName, String year) {
        String line;
        ArrayList<String> allData = new ArrayList<>();
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader(pathToData + stationName + ".csv"));
            while ((line = buffRead.readLine()) != null) {
                // use substring to check the first year
                if (year.compareTo("All years") == 0) {
                    allData.add(line);
                }
                if (line.split(",")[0].compareTo(year)==0) {
                    allData.add(line);
                }
            }
            buffRead.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allData;
    }

    //Returns the years the csv has data for.
    public ArrayList<String> getAllYears(String stationName) {
        String line;
        ArrayList<String> years = new ArrayList<>();
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader(pathToData + stationName + ".csv"));
            while ((line = buffRead.readLine()) != null) {
                // use substring to obtain the year for a check
                String year = line.substring(0, 4);
                if (!years.contains(year)) {
                    years.add(year);
                }
            }
            buffRead.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return years;
    }

    //Return all the station names without respective file extensions
    public ArrayList<String> getStationNames() {
        ArrayList<String> cities = new ArrayList<>();
        for (String station_name : getStationFileNames()) {
            cities.add(stripExtension(station_name));
        }
        return cities;
    }

    //Get the file names of stations files with the csv extension.
    public ArrayList<String> getStationFileNames() {
        File folder = new File(pathToData);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> station_names = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (checkIfCSV(file)) {
                    String fileName = file.getName();
                    station_names.add(fileName);
                }
            }
        }
        return station_names;
    }

    //Return the number of CSV files within the data folder.
    public int getNumCSVFiles() {
        File folder = new File(pathToData);
        File[] listOfFiles = folder.listFiles();
        int count = 0;

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (checkIfCSV(file)) {
                    count++;
                }
            }
        }
        return count;
    }

    //Return the number of days in a specified month.
    private int monthToDay(String month) {
        int int_month = Integer.parseInt(month);
        int daysInMonth;
        if (int_month == 4 || int_month == 6 || int_month == 9 || int_month == 11) {
            daysInMonth = 30;
        } else if (int_month == 2) {
            daysInMonth = 28;
        } else {
            daysInMonth = 31;
        }
        return daysInMonth;
    }

    //Get rid of the file extension.
    public String stripExtension(String str) {
        if (str == null) {
            return null;
        }

        int pos = str.lastIndexOf(".");
        if (pos == -1) {
            return str;
        }

        return str.substring(0, pos);
    }

    //Check if file passed has a csv file extension
    public boolean checkIfCSV(File file) {
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        String fileExtension = fileName.substring(i+1);

        return fileExtension.compareTo("csv") == 0;
    }
}


