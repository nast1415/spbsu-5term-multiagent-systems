public class Map {
    private int citiesAmount;
    private int[][] roadMap = new int[citiesAmount][citiesAmount];

    public Map(int currentCitiesAmount, int[][] currentRoadMap) {
        citiesAmount = currentCitiesAmount;
        roadMap = currentRoadMap;
    }

    public void setCitiesAmount (int currentCitiesAmount) {
        citiesAmount = currentCitiesAmount;
    }

    public void setRoadMap(int[][] currentRoadMap) {
        roadMap = currentRoadMap;
    }




}
