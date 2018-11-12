package CarpoolingApp.PassengerBehaviours;


import CarpoolingApp.ConversationHelper;
import CarpoolingApp.MyAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Calendar;
import java.util.List;

public class SendOffersBehaviour extends OneShotBehaviour{
    private MyAgent passengerAgent;
    PassengerFSM myParent;

    @Override
    public void action() {
        myParent = (PassengerFSM) getParent();
        passengerAgent = (MyAgent) myAgent;

        myParent.currentConversation = ConversationHelper.getNextID();

        List<AID> acceptableDrivers = myParent.acceptableDrivers;
        String convId =  myParent.currentConversation;

        acceptableDrivers.forEach(aid -> {
            ACLMessage startConversationMessage = new ACLMessage(ACLMessage.PROPOSE);
            startConversationMessage.addReceiver(aid);
            startConversationMessage.setConversationId(convId);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, 3000);
            startConversationMessage.setReplyByDate(calendar.getTime());

            startConversationMessage.setOntology("carpooling");
            startConversationMessage.setContent(passengerAgent.getDeparture() + "," + passengerAgent.getDestination()
                    + "," + passengerAgent.getPassengerPrice() + "," + passengerAgent.getAgentId());

            System.out.println(myParent.passengerAgent.getLocalName() + "send proposal to " + aid.getLocalName());

            passengerAgent.send(startConversationMessage);

        });


    }
}
