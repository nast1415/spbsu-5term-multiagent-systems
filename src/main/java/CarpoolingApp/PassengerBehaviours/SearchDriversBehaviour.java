package CarpoolingApp.PassengerBehaviours;


import CarpoolingApp.MyAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchDriversBehaviour extends OneShotBehaviour {
    private MyAgent passengerAgent;

    public List<AID> acceptableDrivers = new ArrayList<>();

    @Override
    public void action() {
        PassengerFSM myParent = (PassengerFSM) getParent();
        passengerAgent = (MyAgent) myAgent;

        try {
            // Build the description used as template for the search
            DFAgentDescription agentDescription = new DFAgentDescription();
            ServiceDescription serviceDescription = new ServiceDescription();
            serviceDescription.setType("carpooling");
            agentDescription.addServices(serviceDescription);


            DFAgentDescription[] results = DFService.search(passengerAgent, agentDescription);

            if (results.length > 0) {
                for (DFAgentDescription dfd : results) {
                    AID driverName = dfd.getName();

                    Iterator it = dfd.getAllServices();
                    while (it.hasNext()) {
                        ServiceDescription sd = (ServiceDescription) it.next();
                        if (sd.getType().equals("carpooling")) {
                            if (!driverName.equals(passengerAgent.getAID()) && !acceptableDrivers.contains(driverName)) {
                                myParent.acceptableDrivers.add(driverName);
                                acceptableDrivers.add(driverName);
                            }
                        }
                    }
                }
            }

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
