package CarpoolingApp;

import CarpoolingApp.DriverBehaviours.DriverFSM;
import CarpoolingApp.PassengerBehaviours.PassengerFSM;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MyAgent extends Agent {
    // Information of the potential ride
    private int departure, destination;
    // Agent id
    private int agentId;
    // Information about map
    private RouteHelper currentRouteHelper;

    // DRIVER BEHAVIOUR

    public ArrayList<Offer> offers = new ArrayList<>(); // All offers from passengers in format Pair(agentId, price)
    public ArrayList<Offer> goodOffers = new ArrayList<>(); // All good offers from passengers in format Pair(agentId, price)
    public Set<String> passengers = new HashSet<>(); // Set of our passengers

    public DriverFSM driverBehaviour; // Behaviour as a driver, who accept offers from passengers

    public void receiveOffer(Offer offer) {
        offers.stream().filter(currentOffer -> currentOffer.agentId.getName() == offer.agentId.getName())
                .forEach(currentOffer -> {
                    offers.remove(currentOffer);
                });
        offers.add(offer);
    }

    public List<Integer> driverRoute = new ArrayList<>();
    public int driverRouteLength;
    public int bestDriverPrice = 10000;

    // PASSENGER BEHAVIOUR

    //public DriverFSM driverBehaviour; // Behaviour as a passenger, who send offers to the drivers
    // List of available driver agents
    private AID[] availableDriverAgents;

    private int passengerPrice = 20; // Information about price agent pay as a passenger
    public PassengerFSM passengerBehaviour; // Behaviour as a passenger, who send offers to drivers

    // DRIVE ON MY OWN BEHAVIOUR

    // Calculate basic price for a driver if he'll go by himself (using RouteHelper)
    private int basicPrice;



    public void setPassengerPrice(int price) {
        passengerPrice = price;
    }

    public int getPassengerPrice() {
        return passengerPrice;
    }

    public int getBasicPrice(){
        return basicPrice;
    }

    public RouteHelper getCurrentRouteHelper() {
        return currentRouteHelper;
    }

    public int getAgentId() {
        return agentId;
    }

    public int getDeparture() { return departure; }

    public int getDestination() { return destination; }

    public ArrayList<Offer> getAllOffers() {
        return offers;
    }
    public void setAllOffers(ArrayList<Offer> allOffers) {offers = allOffers;}

    public ArrayList<Offer> getGoodOffers() {
        return goodOffers;
    }

    public void setDriverRoute (List<Integer> route) { driverRoute = route; }

    protected void setup() {
        Object[] args = getArguments();
        agentId = (int) args[0];
        departure = (int) args[1];
        destination = (int) args[2];
        currentRouteHelper = (RouteHelper) args[3];

        System.out.println("Hello! Driver-agent " + getAID().getLocalName() + " is ready. I want to drive from: " + departure +
                " to: " + destination + " with current petrol price: " + currentRouteHelper.getPetrolPrice());

        basicPrice = currentRouteHelper.getPetrolPrice() * currentRouteHelper.getDistance(departure, destination);

        // Add driver behaviour
        driverBehaviour = new DriverFSM(this);
        addBehaviour(driverBehaviour);

        passengerBehaviour = new PassengerFSM(this);
        addBehaviour(passengerBehaviour);

    }

    public void deregister(){
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            System.err.println("Yellow pages deregistration failed!");
            fe.printStackTrace();
        }
        System.out.println("---------------DECISION-----------------");
        System.out.println(getLocalName() + " route: " + driverRoute);
        System.out.println(getLocalName() + " passengers: " + passengers);
        System.out.println(getLocalName() + " best price: " + bestDriverPrice);
        System.out.println(getLocalName() + " route length: " + driverRouteLength);

        System.out.println("----------------------------------------");

        this.takeDown();
    }

    public void finish() {
        this.takeDown();
    }


    // Put agent clean-up operations here
    protected void takeDown() {
        // Printout a dismissal message
        System.out.println(getAID().getLocalName() + " terminating.");
    }

    public void findOptimalRoutes() {

    }


}
