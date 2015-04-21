package teamawesome.alertme.Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class WeatherHttpClient {

    public String getWeatherData(String location) {
        // Reference to http://www.wunderground.com/weather/api/d/docs?d=data/index&MR=1
        String apiKey = "f336925ac023b89b";
        String features = "forecast";
        String query = "TX/Austin";
        String format = "json";

        // Format is http://api.wunderground.com/api/API_KEY/FEATURES/q/QUERY.FORMAT
        String urlString = "http://api.wunderground.com/api/" + apiKey + "/" + features + "/q/" + query + "." + format;
        HttpURLConnection connection = null;
        InputStream input = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Read the response
            input = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null ) {
                buffer.append(line).append("\r\n");
            }

            input.close();
            connection.disconnect();
            return buffer.toString();

        } catch (IOException ioe) {
            ioe.printStackTrace();

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

        return null;
    }
}
