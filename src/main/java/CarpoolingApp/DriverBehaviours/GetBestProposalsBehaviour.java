package CarpoolingApp.DriverBehaviours;


import CarpoolingApp.Offer;
import CarpoolingApp.OptimalDestination;
import CarpoolingApp.RouteHelper;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GetBestProposalsBehaviour extends OneShotBehaviour {
    private int returnCode;
    private DriverFSM myParent;

    private int bestDriverPrice;

    @Override
    public int onEnd() {
        return isThisRouteBest();
    }

    @Override
    public void action() {
        myParent = (DriverFSM) getParent();
        if (myParent.driverAgent.offers.size() == 0) {
            System.out.println("Empty offers list for " + myParent.driverAgent.getLocalName());
        }
        myParent.driverAgent.goodOffers = getBestOffers();
        myParent.driverAgent.goodOffers = getBestOffers();
        if (myParent.driverAgent.goodOffers.size() > 0)
            System.out.println("Good offers list > 0 for " + myParent.driverAgent.getLocalName());
            System.out.println(myParent.driverAgent.getLocalName() + " have good offers from ");
            for (Offer offer : myParent.driverAgent.goodOffers) {
                System.out.println(offer.agentId.getLocalName());
            }
    }

    public ArrayList<Offer> getBestOffers() {

        ArrayList<Offer> allOffers = myParent.driverAgent.getAllOffers();
        System.out.println("ALL OFFERS: " + allOffers);

        // We going to find best offers using route helper
        RouteHelper routeHelper = myParent.driverAgent.getCurrentRouteHelper();

        OptimalDestination<Integer, Integer, ArrayList<Offer>, List<Integer>> bestResult = routeHelper.getOptimalRoute(allOffers,
                myParent.driverAgent.getAgentId());

        bestDriverPrice = bestResult.getSummaryPrice();
        myParent.driverAgent.driverRouteLength = bestResult.getCurrentComplexRouteLength();
        myParent.driverAgent.setDriverRoute(bestResult.getComplexRoute());
        myParent.driverAgent.bestDriverPrice = bestResult.getSummaryPrice();

        System.out.println("Get offers list");
        System.out.println(bestResult.getOffersIdList());

        Set<String> optimalPassengers = new HashSet<>();
        for (int i = 0; i < bestResult.getOffersIdList().size(); i++) {
            String agentName = bestResult.getOffersIdList().get(i).agentId.getLocalName();
            optimalPassengers.add(agentName);
        }
        myParent.driverAgent.passengers = optimalPassengers;
        return bestResult.getOffersIdList();
    }

    public int isThisRouteBest() {
        int passengerPrice = myParent.driverAgent.getPassengerPrice();
        int basicPrice = myParent.driverAgent.getBasicPrice();

        if ((bestDriverPrice < passengerPrice) && (bestDriverPrice < basicPrice)) {
            System.out.println("Success! Price as a driver is best for agent " + myParent.driverAgent.getLocalName());
            System.out.println("Best driver price = " + bestDriverPrice);
            System.out.println("Current passenger price = " + passengerPrice);
            System.out.println("Basic price = " + basicPrice);
            return 0;
        } else {
            System.out.println("Unfortunately price as a driver is not best for agent " + myParent.driverAgent.getLocalName());
            System.out.println("Best driver price = " + bestDriverPrice);
            System.out.println("Current passenger price = " + passengerPrice);
            System.out.println("Basic price = " + basicPrice);
            return 1;
        }
    }

}
