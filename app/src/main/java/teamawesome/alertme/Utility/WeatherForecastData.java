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


    public class Temperature {
        private int minTemperatureF;
        private int maxTemperatureF;
        private int minTemperatureC;
        private int maxTemperatureC;

        public int getMinTemperatureF() {
            return minTemperatureF;
        }
        public void setMinTemperatureF(int minTemp) {
            minTemperatureF = minTemp;
        }
        public int getMaxTemperatureF() {
            return maxTemperatureF;
        }
        public void setMaxTemperatureF(int maxTemp) {
            maxTemperatureF = maxTemp;
        }

        public int getMinTemperatureC() {
            return minTemperatureC;
        }
        public void setMinTemperatureC(int minTemp) {
            minTemperatureC = minTemp;
        }
        public int getMaxTemperatureC() {
            return maxTemperatureC;
        }
        public void setMaxTemperatureC(int maxTemp) {
            maxTemperatureC = maxTemp;
        }
    }


    public class Precipitation {
        private int percentageChance;
        private float rainAmountInches;
        private float rainAmountMm;
        private float snowAmountInches;
        private float snowAmountCm;

        public int getPercentageChance() {
            return percentageChance;
        }
        public void setPercentageChance(int chance) {
            percentageChance = chance;
        }

        public float getRainAmountInches() {
            return rainAmountInches;
        }
        public void setRainAmountInches(float amount) {
            rainAmountInches = amount;
        }
        public float getRainAmountMm() {
            return rainAmountMm;
        }
        public void setRainAmountMm(float amount) {
            rainAmountMm = amount;
        }

        public float getSnowAmountInches() {
            return snowAmountInches;
        }
        public void setSnowAmountInches(float amount) {
            snowAmountInches = amount;
        }
        public float getSnowAmountCm() {
            return snowAmountCm;
        }
        public void setSnowAmountCm(float amount) {
            snowAmountCm = amount;
        }
    }


    public class Wind {
        private int maxSpeedMph;
        private int maxSpeedKph;

        public int getMaxSpeedMph() {
            return maxSpeedMph;
        }
        public void setMaxSpeedMph(int speed) {
            maxSpeedMph = speed;
        }
        public int getMaxSpeedKph() {
            return maxSpeedKph;
        }
        public void setMaxSpeedKph(int speed) {
            maxSpeedKph = speed;
        }
    }


    public class Humidity {
        private int averageHumidity;

        public int getHumidity() {
            return averageHumidity;
        }
        public void setHumidity(int humidity) {
            averageHumidity = humidity;
        }
    }

}
