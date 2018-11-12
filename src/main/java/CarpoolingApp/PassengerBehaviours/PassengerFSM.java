package CarpoolingApp.PassengerBehaviours;


import CarpoolingApp.MyAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class PassengerFSM extends FSMBehaviour{
    public MyAgent passengerAgent;

    private final String SEARCH_DRIVERS_STATE = "Search drivers";
    private final String SEND_OFFERS_STATE = "Send offers to drivers";
    private final String REPLIES_RECEIVE_STATE = "Wait for answers to offers";
    private final String RAISE_PRICE_STATE = "Raise offer price";
    private final String REMOVE_DRIVER_STATE = "Remove driver from list of potential driver";
    private final String REPLY_ACCEPTED_OFFER_STATE = "Send agree to accepted offer";
    private final String CONFIRMATION_RECEIVE_STATE = "Wait for confirmation";
    private final String FINAL_STATE = "Final transaction";

    public String currentConversation;
    //public int currentIterationID = 0;
    public List<AID> acceptableDrivers = new ArrayList<>();
    public String driverToRemove;
    public ACLMessage acceptedProposal;


    public PassengerFSM(Agent agent) {
        super(agent);

        passengerAgent = (MyAgent) myAgent;
        passengerAgent.setPassengerPrice(20);

        // Register states of FSM
        registerFirstState(new SearchDriversBehaviour(), SEARCH_DRIVERS_STATE);
        registerState(new SendOffersBehaviour(), SEND_OFFERS_STATE);
        registerState(new RepliesReceiverBehaviour(agent, 3000), REPLIES_RECEIVE_STATE);
        registerState(new RaisePriceBehaviour(), RAISE_PRICE_STATE);
        registerState(new RemoveDriverBehaviour(), REMOVE_DRIVER_STATE);
        registerState(new ReplyAcceptedOfferBehaviour(), REPLY_ACCEPTED_OFFER_STATE);
        registerState(new ConfirmationReceiverBehaviour(agent, 3000), CONFIRMATION_RECEIVE_STATE);
        registerLastState(new FinalBehaviour(), FINAL_STATE);

        // Register transitions between states
        // First two transitions are default (without options)
        registerDefaultTransition(
                SEARCH_DRIVERS_STATE,
                SEND_OFFERS_STATE
        );

        registerDefaultTransition(
                SEND_OFFERS_STATE,
                REPLIES_RECEIVE_STATE
        );

        // Next transition has several options (depends on event value) and default value
        registerTransition(
                REPLIES_RECEIVE_STATE,
                RAISE_PRICE_STATE,
                0 // If event == 0 then we haven't got positive replies for our offers and we should rise the price
        );

        registerTransition(
                REPLIES_RECEIVE_STATE,
                REMOVE_DRIVER_STATE,
                -1 // If event == -1 then some drivers haven't sent us replies
                   // and we should delete them from our drivers list
        );

        registerTransition(
                REPLIES_RECEIVE_STATE,
                REPLY_ACCEPTED_OFFER_STATE,
                1 // If event == 1 then we have accepted offer state and we need to reply on it
        );

        registerDefaultTransition(
                RAISE_PRICE_STATE,
                SEARCH_DRIVERS_STATE,
                new String[] { REPLIES_RECEIVE_STATE } // String to be reset
        );

        registerDefaultTransition(
                REMOVE_DRIVER_STATE,
                REPLIES_RECEIVE_STATE
        );

        registerDefaultTransition(
                REPLY_ACCEPTED_OFFER_STATE,
                CONFIRMATION_RECEIVE_STATE
        );

        registerTransition(
                CONFIRMATION_RECEIVE_STATE,
                REPLIES_RECEIVE_STATE,
                0, // If event == 0 then we haven't got confirmations we wait for replies again
                new String[] { CONFIRMATION_RECEIVE_STATE } // String to be reset
        );

        registerTransition(
                CONFIRMATION_RECEIVE_STATE,
                FINAL_STATE,
                1 // If event == 1 then we receive confirmation and go to final state
        );
    }
}
