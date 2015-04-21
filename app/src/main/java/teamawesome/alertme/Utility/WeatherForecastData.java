package teamawesome.alertme.Utility;

public class WeatherForecastData {

    public Temperature temperature;
    public Wind wind;
    public Precipitation precipitation;
    public Humidity humidity;


    public WeatherForecastData() {
        temperature = new Temperature();
        wind = new Wind();
        precipitation = new Precipitation();
        humidity = new Humidity();
    }


    private class Temperature {
        private float temperature;
        private float minTemperature;
        private float maxTemperature;

        public float getTemp() {
            return temperature;
        }
        public void setTemp(float temp) {
            temperature = temp;
        }
        public float getMinTemp() {
            return minTemperature;
        }
        public void setMinTemp(float minTemp) {
            minTemperature = minTemp;
        }
        public float getMaxTemp() {
            return maxTemperature;
        }
        public void setMaxTemp(float maxTemp) {
            maxTemperature = maxTemp;
        }
    }


    private class Wind {
        private float maxSpeed;

        public float getMaxSpeed() {
            return maxSpeed;
        }
        public void setMaxSpeed(float speed) {
            maxSpeed = speed;
        }
    }

    private class Precipitation {
        private float percentageChance;
        private float rainAmount;
        private float snowAmount;

        public float getPercentageChance() {
            return percentageChance;
        }
        public void setPercentageChance(float chance) {
            percentageChance = chance;
        }
        public float getRainAmount() {
            return rainAmount;
        }
        public void setRainAmount(float amount) {
            rainAmount = amount;
        }
        public float getSnowAmount() {
            return snowAmount;
        }
        public void setSnowAmount(float amount) {
            snowAmount = amount;
        }
    }


    private class Humidity {
        private int averageHumidity;

        public int getHumidity() {
            return averageHumidity;
        }
        public void setHumidity(int humidity) {
            averageHumidity = humidity;
        }
    }

}
