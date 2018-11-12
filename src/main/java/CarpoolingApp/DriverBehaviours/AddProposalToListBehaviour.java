package CarpoolingApp.DriverBehaviours;

import jade.core.behaviours.OneShotBehaviour;


public class AddProposalToListBehaviour extends OneShotBehaviour{
    private DriverFSM myParent;

    @Override
    public void action() {
        myParent = (DriverFSM) getParent();

        if(myParent.offer != null){
            myParent.driverAgent.receiveOffer(myParent.offer);
        }
    }
}
