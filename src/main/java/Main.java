import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        // Launch a complete platform on the 8888 port
        // create a default Profile
        Runtime rt = Runtime.instance();
        ProfileImpl p = new ProfileImpl(null, 8888, null);
        AgentContainer container = rt.createMainContainer(p);

        // Open file with information about roadMap and driver routes
        File inputFile = new File("C:/Users/Admin/Anastasia_documents/SPBU/mas/src/main/resources/in");
        try {
            Scanner sc = new Scanner(inputFile);
            // Read information about cities and roads
            int numberOfCities = sc.nextInt();
            int[][] currentRoadMap = new int[numberOfCities][numberOfCities];
            int maxInt = 10000;
            for (int i = 0; i < numberOfCities; i++) {
                for (int j = 0; j < numberOfCities; j++) {
                    currentRoadMap[i][j] = maxInt;
                }
            }

            for (int i = 0; i < numberOfCities; i++) {
                for (int j = 0; j < numberOfCities; j++) {
                    currentRoadMap[i][j] = sc.nextInt();
                }
            }
            // Create current map with roadMap and citiesAmount attributes
            Map currentMap = new Map(numberOfCities, currentRoadMap);

            // Read information about drivers, departure points and destinations
            int numberOfDrivers = sc.nextInt();

            // Create list for all routs (pairs: departure - destination)
            List<Pair<Integer, Integer>> allRoutes = new ArrayList<>(numberOfDrivers);

            for (int i = 0; i < numberOfDrivers; i++) {
                int departure = sc.nextInt();
                int destination = sc.nextInt();

                // For each driver create an agent with departure and destination attributes
                try {
                    Object[] agentArgs = new Object[2];
                    agentArgs[0] = departure;
                    agentArgs[1] = destination;
                    AgentController ag = container.createNewAgent("agent_" + (i + 1), "DriverAgent", agentArgs);
                    ag.start();
                    allRoutes.add(new Pair<>(departure, destination));

                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            }
            RouteHelper currentRouteHelper = new RouteHelper(numberOfCities, currentRoadMap, allRoutes);
            System.out.println(currentRouteHelper.getRoutes().toString());
            currentRouteHelper.getOptimalRoute(2);
        } catch (FileNotFoundException e) {
            System.out.println("Error! File not found!");
        }


    }
}
