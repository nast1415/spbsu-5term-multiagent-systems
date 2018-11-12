package CarpoolingApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Integer.min;

public class RouteHelper {
    private int[][] distances;
    private List<Pair<Integer, Integer>> routes;

    private int petrolPrice;

    public RouteHelper(int citiesAmount, int[][] roadMap, List<Pair<Integer, Integer>> allRoutes,
                       int currentPetrolPrice) {
        distances = roadMap;
        routes = allRoutes;
        petrolPrice = currentPetrolPrice;
        int maxInt = 10000;

        // Floyd–Warshall algorithm for finding shortest paths in1 a weighted graph
        for (int k = 0; k < citiesAmount; k++)
            for (int i = 0; i < citiesAmount; i++)
                for (int j = 0; j < citiesAmount; j++)
                    if (distances[i][k] < maxInt && distances[k][j] < maxInt)
                        distances[i][j] = min(distances[i][j], distances[i][k] + distances[k][j]);
    }

    public int getDistance(int departurePoint, int destinationPoint) {
        return distances[departurePoint][destinationPoint];
    }

    public int getPetrolPrice() {
        return petrolPrice;
    }

    // Helper function for agent to get information about what route is better minimizing total distance traveled
    public OptimalDestination<Integer, Integer, ArrayList<Offer>, List<Integer>> getOptimalRoute(ArrayList<Offer> allOffers,
                                                                                                 int agentNumber) {
        // Set optimal price to big value
        int optimalPrice = 10000;

        List<Integer> optimalRoute = new ArrayList<>();

        // Basic optimal current route for given agent is his route
        int optimalCurrentDistance = distances[routes.get(agentNumber - 1).getF()][routes.get(agentNumber - 1).getS()];

        // Now optimal price for us is basic price
        optimalPrice = optimalCurrentDistance * petrolPrice;

        ArrayList<Offer> optimalOffers = new ArrayList<>();

        int numberOfOffers = allOffers.size();
        System.out.println("Number of offers: " + numberOfOffers);

        // Use bit operations to find different ways of grouping offers
        for (int mask = 0; mask < (1 << numberOfOffers); mask++) { // iterating through all masks
            System.out.println("The biggest mask: " + (1 << numberOfOffers));
            System.out.println("Current mask: " + mask);

            ArrayList<Offer> currentOffers = new ArrayList<>();

            for (int j = 0; j < numberOfOffers; j++) { // iterating through all offers' numbers
                if ((mask & (1 << j)) != 0) { // check if current offer id matches the mask...
                    currentOffers.add(allOffers.get(j)); // we add it to the currentOffers array
                }
            }
            //System.out.println("OUR CURRENT OFFERS LIST IS: " + currentOffers);
            // Use helper function calculateComplexDistance for finding optimal offer from all possible
            OptimalDestination<Integer, Integer, ArrayList<Offer>, List<Integer>> bestResults =
                    calculateComplexDistance(agentNumber, currentOffers);

            // First int of result is an optimal price
            int summaryOptimalPrice = bestResults.getSummaryPrice();

            // Compare current optimal price with new calculated price and if it is smaller than replace it
            // with new calculated price
            if (summaryOptimalPrice < optimalPrice) {
                optimalPrice = summaryOptimalPrice;
                optimalCurrentDistance = bestResults.getCurrentComplexRouteLength();
                optimalOffers = bestResults.getOffersIdList();
                optimalRoute = bestResults.getComplexRoute();
            }
        }
        return new OptimalDestination<>(optimalPrice, optimalCurrentDistance, optimalOffers, optimalRoute);
    }

    private OptimalDestination<Integer, Integer, ArrayList<Offer>, List<Integer>> calculateComplexDistance
            (int agentNumber, ArrayList<Offer> currentOffers) {
        int numberOfOffers = currentOffers.size();

        String orderArray = "";
        for (int i = 0; i < numberOfOffers; i++) {
            orderArray += i;
            orderArray += i;
        }

        int currentDistance = 0;

        // Handle the situation when order array is empty (option when all drivers go by themselves)
        if (orderArray.length() == 0) {
            for (Pair<Integer, Integer> route : routes) {
                currentDistance += distances[route.getF()][route.getS()];
            }
            ArrayList<Offer> offers = new ArrayList<>();
            List<Integer> optimalRoute = new ArrayList<>();
            optimalRoute.add(routes.get(agentNumber - 1).getF());
            optimalRoute.add(routes.get(agentNumber - 1).getS());
            return new OptimalDestination<>(currentDistance * petrolPrice, currentDistance, offers, optimalRoute);

        } else { // If order array is not empty we use calculateDistance function to find optimal route
            // Find all possible options for the transportation of passengers (using the idea of permutations)
            ArrayList<String> allPermutations = permutation(orderArray);
            return calculateDistance(agentNumber, allPermutations, currentOffers);
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
        // Loop through all the string in1 the list
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

    private OptimalDestination<Integer, Integer, ArrayList<Offer>, List<Integer>> calculateDistance
            (int agentNumber, ArrayList<String> allPermutations, ArrayList<Offer> currentOffers) {

        //int numberOfRoutes = routes.size();
        int numberOfOffers = currentOffers.size();
        String optimalPermutation = "";
        int optimalPrice = 10000;
        int optimalCurrentDistance = 0;

        int departure = routes.get(agentNumber - 1).getF();
        int destination = routes.get(agentNumber - 1).getS();

        int currentDistance;
        int currentOptimalPrice;

        // Create arrayList for our current complex route
        List<Integer> complexRoute;
        List<Integer> optimalRoute = new ArrayList<>();

        ArrayList<Offer> bestOffers = new ArrayList<>();
        // Iterate through all possible permutations and find optimal route with minimal distance length
        for (String s : allPermutations) {
            currentDistance = 0;
            currentOptimalPrice = 0;
            complexRoute = new ArrayList<>();

            // First departure point is our agent's departure point
            int nextDeparture = departure;
            complexRoute.add(nextDeparture);
            int nextDestination;

            // Create int array to check if we have already been at the departure point
            int[] checkDeparture = new int[numberOfOffers];
            for (int i = 0; i < numberOfOffers; i++) {
                checkDeparture[i] = 0;
            }
            // For all agents id in1 current permutation find departure or destination point and find summary route length
            for (int i = 0; i < s.length(); i++) {
                // Find next agent's id according to the current permutation
                int nextOfferId = Integer.parseInt(String.valueOf(s.charAt(i)));
                // If we haven't been to departure point for this agent then we need to go to his departure point
                if (checkDeparture[nextOfferId] != 1) {

                    nextDestination = currentOffers.get(nextOfferId).departure;
                    checkDeparture[nextOfferId] = 1;
                } else {
                    // If we have already been to departure point for this agent then we need to go to his destination
                    nextDestination = currentOffers.get(nextOfferId).destination;
                }

                // Add current distance between nextDeparture and nextDestination to the currentDistance
                currentDistance += distances[nextDeparture][nextDestination];

                // Our next departure point is our current destination
                if (nextDeparture != nextDestination) {
                    nextDeparture = nextDestination;
                    // Add this city point to the route
                    complexRoute.add(nextDeparture);
                }
            }

            nextDestination = destination;
            if (nextDeparture != nextDestination) {
                complexRoute.add(nextDestination);
            }
            //System.out.println("Next destination: " + nextDestination);
            currentDistance += distances[nextDeparture][nextDestination];
            int currentComplexDistance = currentDistance;
            currentOptimalPrice += currentDistance * petrolPrice;

            for (int i = 0; i < numberOfOffers; i++) {
                currentOptimalPrice -= currentOffers.get(i).price;
            }

            // Recalculate optimal distance for this set of passengers
            if (currentOptimalPrice < optimalPrice) {
                optimalPrice = currentOptimalPrice;
                // If it is better, than set optimalPermutation the value of current permutation
                optimalPermutation = s;
                optimalCurrentDistance = currentComplexDistance;
                optimalRoute = complexRoute;
                bestOffers = currentOffers;
                //System.out.println("BEST OFFERS: " + bestOffers);
            }


        }

        return new OptimalDestination<>(optimalPrice, optimalCurrentDistance, bestOffers, optimalRoute);
    }
}

