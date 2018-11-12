package CarpoolingApp.DriverBehaviours;

import CarpoolingApp.MyAgent;
import CarpoolingApp.Offer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;

import java.util.ArrayList;
import java.util.List;


public class DriverFSM extends FSMBehaviour{
    public MyAgent driverAgent;

    private final String REGISTER_STATE = "Register in yellow pages service";
    private final String PROPOSALS_RECEIVE_STATE = "Wait for potential passengers proposals";
    private final String CREATE_PROPOSALS_LIST_STATE  = "Add all received proposals to the list";
    private final String GET_BEST_PROPOSALS_STATE = "Get best proposals from the list";
    private final String REJECT_PROPOSALS_STATE = "Reject all proposals";
    private final String ACCEPT_PROPOSALS_STATE = "Accept best proposals";
    private final String AGREEMENTS_RECEIVE_STATE = "Wait for agreement of passengers with best proposals";
    private final String HANDLE_CANCELLATIONS_STATE = "Remove passengers who canceled their proposal";
    private final String CONFIRM_STATE = "Confirm proposal";

    public Offer offer;
    public AID cancelledAgent;
    public ArrayList<String> ignoringAgents;

    public DriverFSM(Agent agent) {
        super(agent);

        driverAgent = (MyAgent) myAgent;
        // driverAgent.setBetterPrice

        // Register states of FSM
        registerFirstState(new RegisterBehaviour(), REGISTER_STATE);

        registerState(new ProposalsReceiverBehaviour(agent,30000), PROPOSALS_RECEIVE_STATE); // wait 30 seconds for proposals
        registerState(new AddProposalToListBehaviour(), CREATE_PROPOSALS_LIST_STATE);
        registerState(new GetBestProposalsBehaviour(), GET_BEST_PROPOSALS_STATE);
        registerState(new RejectProposalsBehaviour(), REJECT_PROPOSALS_STATE);
        registerState(new AcceptProposalsBehaviour(), ACCEPT_PROPOSALS_STATE);
        registerState(new AgreementsReceiverBehaviour(agent, 3000), AGREEMENTS_RECEIVE_STATE);
        registerState(new HandleCancellationsBehaviour(), HANDLE_CANCELLATIONS_STATE);

        registerLastState(new ConfirmBehaviour(), CONFIRM_STATE);

        // Register transitions between states
        // First three transitions are default (without options)
        registerDefaultTransition(
                REGISTER_STATE,
                PROPOSALS_RECEIVE_STATE
        );

        registerDefaultTransition(
                PROPOSALS_RECEIVE_STATE,
                CREATE_PROPOSALS_LIST_STATE
        );

        registerDefaultTransition(
                CREATE_PROPOSALS_LIST_STATE,
                GET_BEST_PROPOSALS_STATE
        );

        // Next transition has several options (depends on event value) and default value
        registerTransition(
                GET_BEST_PROPOSALS_STATE,
                REJECT_PROPOSALS_STATE,
                1 // If event == 1 then we haven't got good proposals and we reject all proposals
        );

        registerTransition(
                GET_BEST_PROPOSALS_STATE,
                ACCEPT_PROPOSALS_STATE,
                0 // If event == 0 then we have good proposals and we accept them and reject other
        );

        registerDefaultTransition(
                REJECT_PROPOSALS_STATE,
                PROPOSALS_RECEIVE_STATE,
                new String[] {PROPOSALS_RECEIVE_STATE} // String to be reset
        );

        registerDefaultTransition(
                ACCEPT_PROPOSALS_STATE,
                AGREEMENTS_RECEIVE_STATE
        );

        registerTransition(
                AGREEMENTS_RECEIVE_STATE,
                HANDLE_CANCELLATIONS_STATE,
                1 // If event == 1 then we haven't got all agreements and we need to handle cancellations
        );

        registerTransition(
                AGREEMENTS_RECEIVE_STATE,
                CONFIRM_STATE,
                0 // If event == 0 then we have got all agreements and we confirm them
        );

        registerDefaultTransition(
                HANDLE_CANCELLATIONS_STATE,
                (driverAgent.goodOffers.size() > 0) ? GET_BEST_PROPOSALS_STATE : PROPOSALS_RECEIVE_STATE,
                new String[]{ PROPOSALS_RECEIVE_STATE } // String to be reset
        );


    }



}
