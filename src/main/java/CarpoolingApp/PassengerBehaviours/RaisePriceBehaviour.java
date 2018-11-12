package CarpoolingApp.PassengerBehaviours;


import CarpoolingApp.MyAgent;
import jade.core.behaviours.OneShotBehaviour;

public class RaisePriceBehaviour extends OneShotBehaviour {
    MyAgent passengerAgent;

    @Override
    public void action() {
        passengerAgent = (MyAgent) myAgent;
        passengerAgent.setPassengerPrice(passengerAgent.getPassengerPrice() + 50);
        if (passengerAgent.getPassengerPrice() > passengerAgent.getBasicPrice()) {
            passengerAgent.removeBehaviour(passengerAgent.passengerBehaviour);
        } else
            System.out.println("Passenger " + passengerAgent.getLocalName() + " raise offer price to "
                    + passengerAgent.getPassengerPrice());

    }
}
