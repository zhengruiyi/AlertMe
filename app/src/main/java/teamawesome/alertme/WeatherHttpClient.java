package teamawesome.alertme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class WeatherHttpClient {

    public String getWeatherData(String location) {
        String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
        HttpURLConnection connection = null ;
        InputStream input = null;

        try {
            connection = (HttpURLConnection) (new URL(BASE_URL + location)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            // Read the response
            StringBuilder buffer = new StringBuilder();
            input = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String line = null;
            while (  (line = br.readLine()) != null )
                buffer.append(line).append("\r\n");

            input.close();
            connection.disconnect();
            return buffer.toString();

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }
}
