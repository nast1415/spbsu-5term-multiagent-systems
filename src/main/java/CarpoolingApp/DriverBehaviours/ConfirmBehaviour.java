package CarpoolingApp.DriverBehaviours;


import CarpoolingApp.Offer;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConfirmBehaviour extends OneShotBehaviour{
    private DriverFSM myParent;

    @Override
    public void action() {

        ArrayList<Offer> badOffers = new ArrayList<>();
        myParent = (DriverFSM) getParent();

        // For each best offer generate agreement
        if(myParent.driverAgent.goodOffers.size() != 0){
            for (Offer offer: myParent.driverAgent.goodOffers) {

                ACLMessage reply = offer.message.createReply();
                reply.setPerformative(ACLMessage.AGREE);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MILLISECOND, 3000);
                reply.setReplyByDate(calendar.getTime());

                reply.setContent("answ");

                System.out.println(myParent.driverAgent.getLocalName() + " send FINAL CONFIRMATION to " + offer.agentId.getLocalName());

                myParent.getAgent().send(reply);
            }

            // Delete all best offers from the pool
            badOffers.addAll(myParent.driverAgent.offers);
            for (Offer goodOffer:
                    myParent.driverAgent.goodOffers) {
                for (Offer badOffer: badOffers) {
                    if (goodOffer.agentId.getName().equals(badOffer.agentId.getName())){
                        badOffers.remove(badOffer);
                    }
                }
            }

            // For all other offers generate reject
            for (Offer offer: badOffers) {
                ACLMessage reply = offer.message.createReply();
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MILLISECOND, 3000);
                reply.setReplyByDate(calendar.getTime());

                reply.setContent("not_available");

                myParent.getAgent().send(reply);
            }


            myParent.driverAgent.offers.clear();

            String s = "";
            for (Offer goodOffer: myParent.driverAgent.goodOffers) {
                s += goodOffer.message.getSender().getLocalName();
                s += " ";
            }
        }

        else {
        }

        myParent.driverAgent.deregister();
        myParent.driverAgent.removeBehaviour(myParent.driverAgent.passengerBehaviour);
        myParent.driverAgent.removeBehaviour(myParent.driverAgent.driverBehaviour);

    }
}
