package CarpoolingApp.DriverBehaviours;


import CarpoolingApp.Offer;
import jade.core.behaviours.OneShotBehaviour;

public class HandleCancellationsBehaviour extends OneShotBehaviour{
    private DriverFSM myParent;

    @Override
    public void action() {
        myParent = (DriverFSM) getParent();

        // If someone cancelled offer
        if (myParent.cancelledAgent != null){
               myParent.driverAgent.offers.stream().filter(offer -> offer.agentId.toString()
                    .equals(myParent.cancelledAgent.toString())).forEach(offer -> {
                myParent.driverAgent.offers.remove(offer);
                myParent.driverAgent.goodOffers.remove(offer);
                System.out.println(myParent.driverAgent.getLocalName() + " REMOVE OFFER FROM " + myParent.cancelledAgent.getLocalName());
            });

        }
        // If someone didn't answer in a time
        else if (myParent.ignoringAgents != null){

            for (String ignoringAgentId: myParent.ignoringAgents) {
                myParent.driverAgent.offers.stream().filter(offer -> offer.agentId.toString()
                        .equals(ignoringAgentId)).forEach(offer -> {
                    myParent.driverAgent.offers.remove(offer);
                    myParent.driverAgent.goodOffers.remove(offer);
                });
            }
        }
        else {
        }
    }
}
