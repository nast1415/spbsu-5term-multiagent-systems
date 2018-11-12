package CarpoolingApp.DriverBehaviours;

import CarpoolingApp.Offer;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AcceptProposalsBehaviour extends OneShotBehaviour {
    private DriverFSM myParent;

    private List<Offer> badOffers;

    @Override
    public void action() {
        badOffers = new ArrayList<>();
        myParent = (DriverFSM) getParent();

        // For every good offer reply with accept
        for (Offer offer: myParent.driverAgent.getGoodOffers()) {

            ACLMessage reply = offer.message.createReply();
            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, 3000);
            reply.setReplyByDate(calendar.getTime());

            reply.setContent("answ");

            myParent.getAgent().send(reply);
            System.out.println(myParent.driverAgent.getLocalName() + " send accept message to agent" + offer.id);
        }

        // Delete good offers from pool
        for (Offer offer: myParent.driverAgent.getAllOffers()) {
            badOffers.add(offer);
        }

        for (int i = 0; i < badOffers.size(); i++) {

        }

        for (Offer goodOffer: myParent.driverAgent.getGoodOffers()) {
            for (int i = 0; i < badOffers.size(); i++) {
                if (goodOffer.agentId.getName().equals(badOffers.get(i).agentId.getName())){
                    badOffers.remove(badOffers.get(i));
                }
            }
        }

        // For all bad offers reply with reject
        for (Offer offer: badOffers) {
            ACLMessage reply = offer.message.createReply();
            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, 3000);
            reply.setReplyByDate(calendar.getTime());

            reply.setContent("answ");

            myParent.getAgent().send(reply);
        }

        myParent.driverAgent.offers.clear();


    }
}
