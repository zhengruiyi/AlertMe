package teamawesome.alertme.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import teamawesome.alertme.Utility.WeatherForecastData;


public class JSONWeatherParser {

    /* Important parts of JSON structure returned by Wunderground API call
    {
        "forecast": {
            "simpleforecast": {
                "forecastday": [            // [today, tomorrow, 2nd day, 3rd day]
                    {
                        "date": {
                            "day": 21,
                            "month": 4,
                            "year": 2015,
                            "monthname": "April",
                            "monthname_short": "Apr",
                            "weekday_short": "Tue",
                            "weekday": "Tuesday",
                        },
                        "high": {
                            "fahrenheit": "80",
                            "celsius": "27"
                        },
                        "low": {
                            "fahrenheit": "64",
                            "celsius": "18"
                        },
                        "icon_url": "http://icons.wxug.com/i/c/k/partlycloudy.gif",
                        "pop": 20,
                        "qpf_allday": {
                            "in": 0.06,
                            "mm": 2
                        },
                        "snow_allday": {
                            "in": 0.00,
                            "cm": 0
                        },
                        "maxwind": {
                            "mph": 15,
                            "kph": 24,
                            "dir": "SE",
                            "degrees": 136
                        },
                        "avehumidity": 57
                    }
                ]
            }
        }
    }
     */

    public static WeatherForecastData getWeather(String data) throws JSONException  {
        WeatherForecastData weatherForecast = new WeatherForecastData();

        // Create JSONObject from the data received from api call
        JSONObject jObj = new JSONObject(data);

        // Get weather info for tomorrow (refer to JSON structure above)
        JSONArray forecastDay = jObj.getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");
        JSONObject tomorrow = forecastDay.getJSONObject(1);


        // Temperature
        JSONObject highTemp = tomorrow.getJSONObject("high");
        weatherForecast.temperature.setMaxTemperatureF(highTemp.getInt("fahrenheit"));
        weatherForecast.temperature.setMaxTemperatureC(highTemp.getInt("celsius"));

        JSONObject lowTemp = tomorrow.getJSONObject("low");
        weatherForecast.temperature.setMinTemperatureF(lowTemp.getInt("fahrenheit"));
        weatherForecast.temperature.setMinTemperatureC(lowTemp.getInt("celsius"));


        // Precipitation
        weatherForecast.precipitation.setPercentageChance(tomorrow.getInt("pop"));

        JSONObject rainAmount = tomorrow.getJSONObject("qpf_allday");
        weatherForecast.precipitation.setRainAmountInches((float)rainAmount.getDouble("in"));
        weatherForecast.precipitation.setRainAmountMm((float)rainAmount.getDouble("mm"));

        JSONObject snowAmount = tomorrow.getJSONObject("snow_allday");
        weatherForecast.precipitation.setSnowAmountInches((float)snowAmount.getDouble("in"));
        weatherForecast.precipitation.setSnowAmountCm((float)snowAmount.getDouble("cm"));


        // Wind
        JSONObject maxWind = tomorrow.getJSONObject("maxwind");
        weatherForecast.wind.setMaxSpeedMph(maxWind.getInt("mph"));
        weatherForecast.wind.setMaxSpeedKph(maxWind.getInt("kph"));


        // Humidity
        weatherForecast.humidity.setHumidity(tomorrow.getInt("avehumidity"));

        return weatherForecast;
    }
}