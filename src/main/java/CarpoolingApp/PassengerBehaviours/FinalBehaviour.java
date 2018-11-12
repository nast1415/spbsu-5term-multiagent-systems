package CarpoolingApp.PassengerBehaviours;


import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;

public class FinalBehaviour extends OneShotBehaviour{
    private PassengerFSM myParent;

    @Override
    public void action() {

        myParent = (PassengerFSM) getParent();

        List<AID> suitableDrivers = myParent.acceptableDrivers;
        String convId = myParent.currentConversation;

        suitableDrivers.forEach(aid -> {

            if (!aid.getLocalName().equals(myParent.acceptedProposal.getSender().getLocalName())) {

                System.out.println(myParent.passengerAgent.getLocalName() + " CANCEL his offer to " + aid.getLocalName());

                ACLMessage cancelMsg = new ACLMessage(ACLMessage.CANCEL ); // Cancel offer
                cancelMsg.addReceiver(aid);
                cancelMsg.setConversationId(convId);

                cancelMsg.setOntology("carpooling");
                cancelMsg.setContent("answ");

                myAgent.send(cancelMsg);

            }
        });

        System.out.println(myParent.passengerAgent.getLocalName() + " finished with passenger price = " + myParent.passengerAgent.getPassengerPrice());

        try {
            myParent.passengerAgent.removeBehaviour(myParent.passengerAgent.driverBehaviour);
            System.out.println(myParent.passengerAgent.getLocalName() + " decided NOT TO BE A DRIVER");
            myParent.passengerAgent.finish();
        }
        catch (Exception ex){

        }



    }
}
