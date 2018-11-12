package CarpoolingApp.DriverBehaviours;

import CarpoolingApp.Offer;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.MessageTemplate;

import jade.lang.acl.ACLMessage;
import javafx.util.Pair;

import java.util.ArrayList;

public class ProposalsReceiverBehaviour extends SimpleBehaviour {
    private DriverFSM myParent;

    private MessageTemplate template;
    private long timeOut, wakeupTime;
    private boolean finished;

    private ACLMessage msg;

    public ACLMessage getMessage() { return msg; }


    public ProposalsReceiverBehaviour(Agent a, int millis) {
        super(a);
        timeOut = millis;
    }

    public void onStart() {
        myParent = (DriverFSM) getParent();
        template = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        wakeupTime = (timeOut < 0 ? Long.MAX_VALUE :System.currentTimeMillis() + timeOut);
    }

    public boolean done () {
        return finished;
    }

    public void action()
    {
        System.out.println(myParent.driverAgent.getLocalName() + ": waiting for proposals again");
        msg = myAgent.receive(template);

        if( msg != null) {
            finished = true;
            handle( msg );
            return;
        }

        long dt = wakeupTime - System.currentTimeMillis();
        if ( dt > 0 )
            block(dt);
        else {
            System.out.println("Time is out!!!");
            finished = true;
            handle( msg );
        }
    }

    public void handle( ACLMessage msg) {
        if (msg == null) {
            // Remove driver behaviour (because we haven't got any proposals)
            myParent.driverAgent.removeBehaviour(myParent.driverAgent.driverBehaviour);
            System.out.println(myParent.driverAgent.getLocalName() + " decided NOT TO BE A DRIVER");
        } else {
            myParent.offer = new Offer(msg);
            System.out.println("I have proposals!! Me: " + myParent.driverAgent.getLocalName());
        }
    }

    public void reset() {
        msg = null;
        finished = false;
        super.reset();
    }

    public void reset(long dt) {
        timeOut = dt;
        reset();
    }
}
