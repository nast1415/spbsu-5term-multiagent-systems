package CarpoolingApp.DriverBehaviours;

import CarpoolingApp.Offer;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Calendar;


public class RejectProposalsBehaviour extends OneShotBehaviour {
    private DriverFSM myParent;

    @Override
    public void action() {

        myParent = (DriverFSM) getParent();


        // For every offer from offers list reply with reject
        for (Offer offer: myParent.driverAgent.offers) {

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
