package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.view.ViewManager;

/**
 * Hello world!
 *
 */
public class Server
{
    public static void main( String[] args )
    {
        Model model = new Model();
        ViewManager vm = new ViewManager();
        Controller controller = new Controller(model, vm);

        model.setBuilderBuiltObserver(vm);
        model.setBuilderMovementObserver(vm);
        model.setBuildersPlacedObserver(vm);
        model.setChosenStepObserver(vm);
        model.setColorAssignmentObserver(vm);
        model.setEndGameObserver(vm);
        model.setErrorsObserver(vm);
        model.setGodChoiceObserver(vm);
        model.setPlayerAddedObserver(vm);
        model.setPlayerLoseObserver(vm);
        model.setPlayerTurnObserver(vm);
        model.setViewSelectObserver(vm);
        model.setStateObserver(vm);
        model.setPossibleBuildObserver(vm);
        model.setPossibleMoveObserver(vm);



        MultiThreadServer server = new MultiThreadServer(2033);

        server.startServer(vm, controller);

    }
}
