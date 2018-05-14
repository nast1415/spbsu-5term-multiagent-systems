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
        File inputFile = new File("C:/Users/Admin/Anastasia_documents/SPBU/mas/src/main/resources/in2");
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

            //Read information about current petrol price
            int petrolPrice = sc.nextInt();

            // Read information about drivers, departure points and destinations
            int numberOfDrivers = sc.nextInt();

            // Create list for all routs (pairs: departure - destination)
            ArrayList<Pair<Integer, Integer>> allRoutes = new ArrayList<>(numberOfDrivers);

            for (int i = 0; i < numberOfDrivers; i++) {
                int departure = sc.nextInt();
                int destination = sc.nextInt();
                allRoutes.add(new Pair<>(departure, destination));
            }

            // Create RouteHelper for agents
            RouteHelper currentRouteHelper = new RouteHelper(numberOfCities, currentRoadMap, allRoutes, petrolPrice);

            int agentNumber = 0;
            for (Pair<Integer, Integer> pair: allRoutes) {
                agentNumber++;
                int departure = pair.getF();
                int destination = pair.getS();

                // For each driver create an agent with departure and destination attributes
                try {
                    Object[] agentArgs = new Object[3];
                    agentArgs[0] = departure;
                    agentArgs[1] = destination;
                    agentArgs[2] = currentRouteHelper;
                    AgentController ag = container.createNewAgent("agent_" + (agentNumber), "DriverAgent", agentArgs);
                    ag.start();


                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(currentRouteHelper.getRoutes().toString());
            int currentAgent = 1;
            OptimalDestination<Integer, Integer, ArrayList<Integer>> optimalRoute = currentRouteHelper.getOptimalRoute(currentAgent);

            System.out.println("---------------------------");
            System.out.println("For passenger with id = " + currentAgent + " best summary route length = "
                    + optimalRoute.getSummaryRoutesLength() + ", and his complex route length = " + optimalRoute.getCurrentComplexRouteLength() +
                    " with passengers: " + optimalRoute.getPassengersIdList());
            System.out.println("---------------------------");
        } catch (FileNotFoundException e) {
            System.out.println("Error! File not found!");
        }

    }
}
