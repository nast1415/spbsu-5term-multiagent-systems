package CarpoolingApp.DriverBehaviours;

import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.ArrayList;

public class RegisterBehaviour extends OneShotBehaviour{
    private DriverFSM myParent;

    @Override
    public void action() {
        myParent = (DriverFSM) getParent();

        // Register the driver-agent service in1 the yellow pages (for other agents)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(myAgent.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("carpooling");
        sd.setName("JADE_carpooling");
        dfd.addServices(sd);
        try {
            DFService.register(myAgent, dfd);
            myParent.driverAgent.goodOffers = new ArrayList<>();
            myParent.driverAgent.offers = new ArrayList<>();

            System.out.println(myAgent + " successfully registered in yellow pages service. He can now receive proposals");
        } catch (FIPAException fe) {
            System.err.println("Yellow pages registration failed for " + myAgent.getLocalName());
            fe.printStackTrace();
        }
    }
}
