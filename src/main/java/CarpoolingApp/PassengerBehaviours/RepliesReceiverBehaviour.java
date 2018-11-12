package CarpoolingApp.PassengerBehaviours;


import CarpoolingApp.DriverBehaviours.DriverFSM;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RepliesReceiverBehaviour extends SimpleBehaviour{
    private long timeOut, wakeupTime;
    private boolean finished;

    private PassengerFSM myParent;

    private ACLMessage msg;
    private int returnCode;
    private int amountOfMessagesReceived;

    MessageTemplate template;

    public ACLMessage getMessage() { return msg; }

    public RepliesReceiverBehaviour(Agent a, int millis) {
        super(a);
        timeOut = millis;
    }

    public void onStart() {
        myParent = (PassengerFSM) getParent();
        amountOfMessagesReceived = 0;
        template = new MessageTemplate((MessageTemplate.MatchExpression) aclMessage ->
                (aclMessage.getPerformative() == (ACLMessage.ACCEPT_PROPOSAL) ||
                        aclMessage.getPerformative() == (ACLMessage.REJECT_PROPOSAL)) &&
                        aclMessage.getConversationId().equals(myParent.currentConversation));
        wakeupTime = (timeOut < 0 ? Long.MAX_VALUE : System.currentTimeMillis() + timeOut);
    }

    @Override
    public boolean done () {
        return finished || returnCode == -1;
    }

    @Override
    public void action()
    {
        returnCode = 0;

        msg = myAgent.receive(template);

        if(msg != null) {
            amountOfMessagesReceived++;
            // Have we got all answers (from all acceptable drivers)
            finished = amountOfMessagesReceived == myParent.acceptableDrivers.size();
            handle( msg );
            return;
        }
        long dt = wakeupTime - System.currentTimeMillis();
        if ( dt > 0 )
            block(dt);

        else if (!finished) {
            finished = true;
            handle( msg );
        }
    }

    public void handle(ACLMessage msg) {

        if (msg == null) {
            // Time is out, we haven't got all replies
            returnCode = 0;
            return;
        }

        // If we receive accept proposal
        if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
            System.out.println(myParent.passengerAgent.getLocalName() + " receive accept for his proposal from " + msg.getSender().getLocalName());
            finished = true;
            returnCode = 1;
            myParent.acceptedProposal = msg;
        } else { // reject proposal
            if (amountOfMessagesReceived == myParent.acceptableDrivers.size()) {
                // We receive all answers but we haven't got any accept
                returnCode = 0;
            }
        }

    }

    @Override
    public int onEnd() {
        return returnCode;
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
