package CarpoolingApp.PassengerBehaviours;


import CarpoolingApp.DriverBehaviours.DriverFSM;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ConfirmationReceiverBehaviour extends SimpleBehaviour{
    private long timeOut, wakeupTime;
    private boolean finished;

    private PassengerFSM myParent;

    private ACLMessage msg;
    private int returnCode;

    MessageTemplate template;

    public ACLMessage getMessage() { return msg; }

    public ConfirmationReceiverBehaviour(Agent a, int millis) {
        super(a);
        timeOut = millis;
    }

    public void onStart() {
        myParent = (PassengerFSM) getParent();
        wakeupTime = (timeOut < 0 ? Long.MAX_VALUE : System.currentTimeMillis() + timeOut);

        // Wait for a confirmation from a concrete driver
        template = new MessageTemplate((MessageTemplate.MatchExpression) aclMessage ->
                (aclMessage.getPerformative() == (ACLMessage.AGREE) &&
                        aclMessage.getSender().getLocalName().equals(myParent.acceptedProposal.getSender().getLocalName())));
    }

    @Override
    public boolean done () {
        return finished;
    }

    @Override
    public void action()
    {
        msg = myAgent.blockingReceive(template, timeOut);

        if( msg != null) {
            finished = true;
            handle( msg );
            return;
        }
        long dt = wakeupTime - System.currentTimeMillis();
        if ( dt > 0 )
            block(dt);
        else {
            finished = true;
            handle( msg );
        }
    }

    public void handle(ACLMessage m) {

        if (m != null) {
            System.out.println(myParent.passengerAgent.getLocalName() + " receive FINAL CONFIRMATION from " + m.getSender().getLocalName());
        }
        // If time is out and we haven't got confirmation then return code is 0 else 1
        returnCode = (m == null) ? 0 : 1;
        System.out.println("Return code now is " + returnCode);
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
        timeOut = dt;
        reset();
    }
}
