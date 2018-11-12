package CarpoolingApp;


import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Offer {
    public int departure, destination;
    public int price;
    public AID agentId;
    public int id; // TODO от единицы!!!
    public ACLMessage message;

    public Offer (ACLMessage message){
        if(message.getPerformative() == ACLMessage.PROPOSE){
            agentId = message.getSender();
            String[] message_info = message.getContent().split(",");

            departure = Integer.parseInt(message_info[0]);
            destination = Integer.parseInt(message_info[1]);
            price  = Integer.parseInt(message_info[2]);
            id = Integer.parseInt(message_info[3]);


            this.message = message;
        }
    }

    public Offer(int passengerDeparture, int passengerDestination, int currentPrice,
                 int currentId){
        departure = passengerDeparture;
        destination = passengerDestination;
        price = currentPrice;
        id = currentId;
    }
}
