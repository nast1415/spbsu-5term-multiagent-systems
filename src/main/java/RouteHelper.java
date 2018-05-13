import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.min;

public class RouteHelper {
    private int[][] distances;
    private List<Pair<Integer, Integer>> routes;

    public RouteHelper(int citiesAmount, int[][] roadMap, List<Pair<Integer, Integer>> allRoutes) {
        distances = roadMap;
        routes = allRoutes;
        int maxInt = 10000;

        // Floyd–Warshall algorithm for finding shortest paths in a weighted graph
        for (int k = 0; k < citiesAmount; k++)
            for (int i = 0; i < citiesAmount; i++)
                for (int j = 0; j < citiesAmount; j++)
                    if (distances[i][k] < maxInt && distances[k][j] < maxInt)
                        distances[i][j] = min(distances[i][j], distances[i][k] + distances[k][j]);
    }

    public int getDistance(int departurePoint, int destinationPoint) {
        return distances[departurePoint][destinationPoint];
    }

    // Helper function for agent to get information about what route is better minimizing total distance traveled
    public ArrayList<Integer> getOptimalRoute(int agentNumber) {
        int numberOfRoutes = routes.size();
        int optimalDistanceLength = 10000;

        ArrayList<Integer> optimalPassengers = new ArrayList<>();

        // Create array for all agents numbers
        int[] agentsArray = new int[numberOfRoutes];
        for (int i = 0; i < numberOfRoutes; i++) {
            agentsArray[i] = i;
        }
        int numberOfAgents = agentsArray.length;

        // use bit operations to find different ways of grouping agents
        for (int mask = 0; mask < (1 << numberOfAgents); mask++) { // iterating through all masks
            if ((mask & (1 << (agentNumber - 1))) != 0) { // we don't want to choose ourselves
                List<Integer> currentPassengers = new ArrayList<>();
                for (int j = 0; j < numberOfAgents; j++) { // iterating through all agents' numbers
                    if ((mask & (1 << j)) != 0) { // check if current agent id matches the mask...
                        if (agentsArray[j] != agentNumber - 1) {
                            currentPassengers.add(agentsArray[j] + 1); // ... we add it to the passengers array
                        }
                    }
                }

                //System.out.println("Case:");
                //System.out.println(currentPassengers);
                //System.out.println("Order array for this case: ");

                // Use helper function calculateComplexDistance for finding optimal route from all possible
                ArrayList<Integer> bestResults = calculateComplexDistance(agentNumber, currentPassengers);
                System.out.println("For current passengers: " + currentPassengers + " best results are: " + bestResults);
                // First int of result is an optimal distance length
                int currentOptimalDistance = bestResults.get(0);

                // Compare current optimal distance with new calculated distance and if it is longer than replace it
                // with new calculated distance length
                if (currentOptimalDistance < optimalDistanceLength) {
                    optimalDistanceLength = currentOptimalDistance;
                    optimalPassengers = bestResults;
                }
            }
        }

        System.out.println("------------------------------------");
        System.out.println("For passenger with id=" + agentNumber + " best route length = " + optimalDistanceLength +
                ", and best passengers are: ");
        System.out.println(optimalPassengers);

        return optimalPassengers;
    }

    private ArrayList<Integer> calculateComplexDistance(int agentNumber, List<Integer> currentPassengers) {
        int numberOfPassengers = currentPassengers.size();
        String orderArray = "";
        for (Integer currentPassenger : currentPassengers) {
            orderArray += currentPassenger;
            orderArray += currentPassenger;
        }

        int currentDistance = 0;
        ArrayList<Integer> result = new ArrayList<>();

        // Handle the situation when order array is empty (option when all drivers go by themselves)
        if (orderArray.length() == 0) {
            for (Pair<Integer, Integer> route : routes) {
                currentDistance += distances[route.getF()][route.getS()];
            }
            result.add(currentDistance);
            return result;

        } else { // If order array is not empty we use calculateDistance function to find optimal route
            // Find all possible options for the transportation of passengers (using the idea of permutations)
            ArrayList<String> allPermutations = permutation(orderArray);
            return calculateDistance(agentNumber, allPermutations);
        }

    }

    public List<Pair<Integer, Integer>> getRoutes() {
        return routes;
    }

    // Helper function to get all permutations
    private ArrayList<String> permutation(String s) {
        // ArrayList for the result
        ArrayList<String> res = new ArrayList<>();
        // If input string's length is 1, return {s}
        if (s.length() == 1) {
            res.add(s);
        } else if (s.length() > 1) {
            int lastIndex = s.length() - 1;
            // Find out the last character
            String last = s.substring(lastIndex);
            // Rest of the string
            String rest = s.substring(0, lastIndex);
            // Perform permutation on the rest string and
            // merge with the last character
            res = merge(permutation(rest), last);
        }
        return res;
    }

    // Merge function for permutation function
    private ArrayList<String> merge(ArrayList<String> list, String c) {
        Set<String> permutations = new HashSet<>();
        ArrayList<String> res = new ArrayList<>();
        // Loop through all the string in the list
        for (String s : list) {
            // For each string, insert the last character to all possible positions
            // and add them to the new list
            for (int i = 0; i <= s.length(); ++i) {
                String ps = new StringBuffer(s).insert(i, c).toString();
                permutations.add(ps);
            }
        }
        res.addAll(permutations);
        return res;
    }

    private ArrayList<Integer> calculateDistance(int agentNumber, ArrayList<String> allPermutations) {
        int numberOfRoutes = routes.size();
        int optimalDistance = 10000;
        String optimalPermutation = "";

        int departure = routes.get(agentNumber - 1).getF();
        int destination = routes.get(agentNumber - 1).getS();

        int currentDistance;

        // Iterate through all possible permutations and find optimal route with minimal distance length
        for (String s : allPermutations) {
            // Create arrayList for our current complex route
            List<Integer> complexRoute = new ArrayList<>();
            currentDistance = 0;
            // First departure point is our agent's departure point
            int nextDeparture = departure;
            // Add this point to our current route
            complexRoute.add(nextDeparture);
            int nextDestination;

            // Create int array to check if we have already been at the departure point
            int[] checkDeparture = new int[numberOfRoutes];
            for (int i = 0; i < numberOfRoutes; i++) {
                if (i != agentNumber - 1) {
                    checkDeparture[i] = 0;
                } else {
                    // For our agent id set this value to 1
                    checkDeparture[i] = 1;
                }
            }
            // For all agents id in current permutation find departure or destination point and find summary route length
            for (int i = 0; i < s.length(); i++) {
                // Find next agent's id according to the current permutation
                int nextAgentNumber = Integer.parseInt(String.valueOf(s.charAt(i)));
                // If we haven't been to departure point for this agent then we need to go to his departure point
                if (checkDeparture[nextAgentNumber - 1] != 1) {
                    nextDestination = routes.get(Integer.parseInt(String.valueOf(s.charAt(i))) - 1).getF();
                    checkDeparture[nextAgentNumber - 1] = 1;
                } else {
                    // If we have already been to departure point for this agent then we need to go to his destination
                    nextDestination = routes.get(Integer.parseInt(String.valueOf(s.charAt(i))) - 1).getS();
                }

                // Add current distance between nextDeparture and nextDestination to the currentDistance
                currentDistance += distances[nextDeparture][nextDestination];
                //System.out.println("Distance between : " + nextDeparture + " and " + nextDestination + " = " +
                        //distances[nextDeparture][nextDestination]);
                //System.out.println("Current distance now is: " + currentDistance);

                // Our next departure point is our current destination
                nextDeparture = nextDestination;
                // Add this city point to the route
                complexRoute.add(nextDeparture);
            }
            nextDestination = destination;
            // Add final destination point to the route
            complexRoute.add(nextDestination);
            //System.out.println("Next destination: " + nextDestination);
            currentDistance += distances[nextDeparture][nextDestination];

            //System.out.println("Distance length with all passengers: " + currentDistance);
            //System.out.println("Distance between them: " + distances[nextDeparture][nextDestination]);
            //System.out.println("Current distance now is: " + currentDistance);

            // We need to add routes of other passengers (not mentioned in current permutation) to our currentDistance
            // (it means that they are going by themselves)
            for (int i = 0; i < numberOfRoutes; i++) {
                if (checkDeparture[i] == 0) {
                    currentDistance += distances[routes.get(i).getF()][routes.get(i).getS()];
                }
            }

            // Recalculate optimal distance for this set of passengers
            if (optimalDistance > currentDistance) {
                optimalDistance = currentDistance;
                // If it is better, than set optimalPermutation the value of current permutation
                optimalPermutation = s;

                //System.out.println("New optimal way! Optimal distance now is: " + optimalDistance +
                       // " and optimal permutation now is " + optimalPermutation);
            }
        }

        // Print the results
        //System.out.println("Best distance is: ");
        //System.out.println(optimalDistance);
        //System.out.println("For this agents are taken:");
        //System.out.println(optimalPermutation);

        // Prepare results for return
        int numberOfPassengers = optimalPermutation.length();
        ArrayList<Integer> passengersList = new ArrayList<>();
        Set<Integer> passengersSet = new HashSet<>();
        passengersList.add(optimalDistance);
        for (int i = 0; i < numberOfPassengers; i++) {
            passengersSet.add(Integer.parseInt(String.valueOf(optimalPermutation.charAt(i))));
        }
        passengersList.addAll(passengersSet);

        return passengersList;
    }
}
