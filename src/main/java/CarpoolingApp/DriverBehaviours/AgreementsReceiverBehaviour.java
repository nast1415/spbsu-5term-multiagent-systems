package CarpoolingApp.DriverBehaviours;


import CarpoolingApp.Offer;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AgreementsReceiverBehaviour extends SimpleBehaviour{
    private MessageTemplate template;
    private long timeOut, wakeupTime;
    private boolean finished;

    private DriverFSM myParent;
    private ArrayList<String> awaitingAgentsId = new ArrayList<>();
    private int returnCode; // We need return code to know, what state we should do next

    private ACLMessage msg;
    private int amountOfMessagesReceived;

    public ACLMessage getMessage() { return msg; }


    public AgreementsReceiverBehaviour(Agent a, int millis) {
        super(a);
        timeOut = millis;
    }

    public void onStart() {
        myParent = (DriverFSM) getParent();
        template = new MessageTemplate(aclMessage ->
                aclMessage.getPerformative() == (ACLMessage.AGREE)||
                        aclMessage.getPerformative() == (ACLMessage.CANCEL)&&
                                awaitingAgentsId.contains(aclMessage.getSender().getLocalName()));
        wakeupTime = (timeOut < 0 ? Long.MAX_VALUE :System.currentTimeMillis() + timeOut);
        amountOfMessagesReceived = 0;

        // Add all agents we want to get agreement from to the awaiting list
        awaitingAgentsId.addAll(myParent.driverAgent.goodOffers.stream().map(goodOffer -> goodOffer.agentId.toString())
                .collect(Collectors.toList()));
    }

    public boolean done () {
        return finished;
    }

    public void action()
    {
        returnCode = 1;

        awaitingAgentsId = new ArrayList<>();
        awaitingAgentsId.addAll(myParent.driverAgent.goodOffers.stream().map(goodOffer -> goodOffer
                .agentId.getLocalName()).collect(Collectors.toList()));

        if(awaitingAgentsId.size() == 0){
            finished = true;
            return;
        }

        msg = myAgent.receive(template);

        if( msg != null) {
            amountOfMessagesReceived++;
            awaitingAgentsId.stream().filter(id -> msg.getSender().toString().equals(id)).forEach(id -> {
                awaitingAgentsId.remove(id);
            });

            System.out.println(myParent.driverAgent.getLocalName() + " get AGREEMENT from " + msg.getSender().getLocalName());

            // Finished when we get all messages from all agents provided good offers
            finished = amountOfMessagesReceived == myParent.driverAgent.goodOffers.size();
            handle( msg );
            return;

        }
        long dt = wakeupTime - System.currentTimeMillis();
        if ( dt > 0 )
            block(dt);

        else if (!finished){
            finished = true;
            handle( msg );
        }
    }

    public void handle( ACLMessage msg) {
        if (msg == null) {
            // Time is out but we haven't receive all answers yet
            returnCode = 0;

            // Save ids of ignoring agent
            myParent.ignoringAgents = this.awaitingAgentsId;
            return;

        } else {
            if (msg.getPerformative() == ACLMessage.CANCEL){
                System.out.println(myParent.driverAgent.getLocalName() + " RECEIVED CANCEL from " + myParent.cancelledAgent.getLocalName());
                // If someone cancelled his proposal we save their ids to the cancelledAgents list
                myParent.cancelledAgent = msg.getSender();
                returnCode = 1;
                return;
            }
            else {

                if (finished) {
                    returnCode = 1;
                    return;
                }
            }
        }
    }

    public void reset() {
        msg = null;
        finished = false;
        super.reset();
    }

    public void reset(int dt) {
        timeOut= dt;
        reset();
    }
}
