package CarpoolingApp.PassengerBehaviours;


import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Calendar;

public class ReplyAcceptedOfferBehaviour extends OneShotBehaviour{
    @Override
    public void action() {
        PassengerFSM myParent = (PassengerFSM) getParent();

        ACLMessage reply = myParent.acceptedProposal.createReply();
        reply.setPerformative(ACLMessage.AGREE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, 3000);
        reply.setReplyByDate(calendar.getTime());

        reply.setContent("answ");

        System.out.println(myParent.passengerAgent.getLocalName() + " reply for accepted proposal from " +
                myParent.acceptedProposal.getSender().getLocalName() + " with AGREEMENT");

        myParent.getAgent().send(reply);

    }
}
