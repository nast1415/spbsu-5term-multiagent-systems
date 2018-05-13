import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.MessageTemplate;


public class DriverAgent extends Agent {
    // Information of the potential ride
    private String departure, destination;
    // List of available driver agents
    private AID[] availableDriverAgents;

    protected void setup() {
        Object[] args = getArguments();
        departure = args[0].toString();
        destination = args[1].toString();

        System.out.println("Hello! Driver-agent " + getAID().getName() + " is ready. I want to drive from: " + departure +
                " to: " + destination);

        // Register the driver-agent service in the yellow pages (for other agents)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("carpooling");
        sd.setName("JADE_carpooling");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    // Put agent clean-up operations here
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Printout a dismissal message
        System.out.println("Driver agent terminating.");
    }

    private class RequestPerformer extends Behaviour {
        private AID bestDriver; // The agent who provides the best offer
        private int bestPrice;  // The best offered price
        private int repliesCnt = 0; // The counter of replies from driver agents
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;

        @Override
        public void action() {

        }

        @Override
        public boolean done() {
            return false;
        }
    }

}
