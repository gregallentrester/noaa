package net.greg.examples.salient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public final class NOAAService {


  public static void main(String[] args) {
    new NOAAService().accept(args);

  }

  private void accept(String[] args) {

//locate(args);
//*
    String forecasts = forecast(locate(args));

    System.err.println(forecasts);

    try (
      BufferedWriter publisher =
          new BufferedWriter(
            new FileWriter(REPRESENTATION))) {

      publisher.write(forecasts);
    }
    catch(IOException e) { e.printStackTrace(); }

  //  */
  }


  public static String forecast(String location) {

    boolean accum = false;
    String line = "";
    StringBuilder forecasts =
      new StringBuilder(PREAMBLE);

    try {

      Process process = Runtime.getRuntime().exec(new String[] {
        CURL, location });

      BufferedReader br =
        new BufferedReader(
          new InputStreamReader(
            process.getInputStream()));

      while (null != (line = br.readLine())) {

        if (line.contains(PERIODS_TOKEN)) {
          accum = true;
        }

        if (accum &&
            ! line.contains(NUMBER_LBL) &&
            ! line.contains(PERIODS_TOKEN)) {

          line = line.replace(']', ' ');
          line = line.replace('[', ' ');

          line = line.replace('}', ' ');
          line = line.replace('{', ' ');

          line = line.replace(')', ' ');
          line = line.replace('(', ' ');

          line = line.replace(',', ' ');
          line = line.replace('"', ' ');

          if (line.contains(NAME_TAG)) {
            line = line.substring(line.indexOf(":")+1);
            forecasts.append(HR + "\n<li><b><br/>" + line.trim() + "</b>");
          }
          else if (line.contains(ICON_LBL)) {
            line = line.substring(line.indexOf(":")+1);

            forecasts.append("\n<li><img src='" + line.trim());
            forecasts.append(STYLE);
          }
          else {
            forecasts.append("\n<li>" + line.trim());
          }
        }
      }
    }
    catch (IOException e) { e.printStackTrace(); }

    return forecasts.toString();
  }


  /**
   * Detect NOAA locale via this endpoint.
   *
   * @param args coordinates via CLI
   * @return the NOAA forecast endpoint
   */
  private String locate(String[] args) {

    String answer = "";

    ENDPOINT_POINTS.append(args[0]);


    try {

      Process process = Runtime.getRuntime().exec(new String[] {
        CURL, ENDPOINT_POINTS.toString() });

      BufferedReader br =
        new BufferedReader(
          new InputStreamReader(
            process.getInputStream()));

      while (null != (answer = br.readLine())) {

        if (answer.contains(FORECAST_KEY)) {

          answer =
            answer.substring(
              answer.indexOf(HTTPS_TOKEN),
              answer.lastIndexOf("\""));

          System.err.println(GRN + answer + NC);

          return answer;
        }
      }
    }
    catch (IOException e) { e.printStackTrace(); }

    return answer;
  }


  private static final String REPRESENTATION =
    "NOAAForecast.HTML";

  private static String CURL = "curl";

  private static StringBuilder ENDPOINT_POINTS =
    new StringBuilder("https://api.weather.gov/points/");

  private static final String FORECAST_KEY =  "\"forecast\":";
  private static final String HTTPS_TOKEN = "https:";

  private static final String PERIODS_TOKEN = "\"periods\": [";

  private static final String NUMBER_LBL = "number";
  private static final String NAME_TAG = "name";
  private static final String ICON_LBL = "icon";

  private static final String PREAMBLE =
    "<!DOCTYPE html><html><ul style='list-style:none;'>";

  private static final String STYLE =
    "' width='30' height='30' style='border-radius: 50%;'/>";

  private static final String HR =
    "<hr style='border:0; height:1px;width:95%;margin-top:1.4em;"+
    "background-image:-webkit-linear-gradient(left,#f0f0f0,#8c8b8b,#f0f0f0);"+
    "background-image:-moz-linear-gradient(left,#f0f0f0,#8c8b8b,#f0f0f0);"+
    "background-image:-ms-linear-gradient(left,#f0f0f0,#8c8b8b,#f0f0f0);"+
    "background-image:-o-linear-gradient(left,#f0f0f0,#8c8b8b,#f0f0f0);'/>";

  public static final String GRN = "\u001B[32m";
  public static final String NC = "\u001B[0m";
}
