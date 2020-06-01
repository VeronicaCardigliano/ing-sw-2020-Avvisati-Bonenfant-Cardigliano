package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.NetworkHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class GuiClient extends Application {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("SANTORINI");

        Gui view = new Gui(primaryStage);

        NetworkHandler nh = new NetworkHandler("localhost", 2033);

        nh.setView(view);

        view.setBuilderBuildObserver(nh);
        view.setBuilderMoveObserver(nh);
        view.setColorChoiceObserver(nh);
        view.setNewPlayerObserver(nh);
        view.setNumberOfPlayersObserver((nh));
        view.setStepChoiceObserver(nh);
        view.setBuilderSetupObserver(nh);
        view.setDisconnectionObserver(nh);
        view.setGodCardChoiceObserver(nh);
        view.setStartPlayerObserver(nh);

        nh.setBuilderBuiltObserver(view);
        nh.setBuilderMovementObserver(view);
        nh.setBuildersPlacedObserver(view);
        nh.setChosenStepObserver(view);
        nh.setColorAssignmentObserver(view);
        nh.setEndGameObserver(view);
        nh.setErrorsObserver(view);
        nh.setGodChoiceObserver(view);
        nh.setPlayerAddedObserver(view);
        nh.setPlayerLoseObserver(view);
        nh.setPlayerTurnObserver(view);
        nh.setStateObserver(view);
        nh.setPossibleBuildObserver(view);
        nh.setPossibleMoveObserver(view);

        (new Thread(nh)).start();
        view.run();

        //final Canvas cell = new Canvas(50, 50);
        //GraphicsContext gc = cell.getGraphicsContext2D();
        //gc.setFill(Color.STEELBLUE);

        //I create a gridpane with rows and colums for the map
        /*
        GridPane g = new GridPane();
        g.add(label, 1, 0);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(100.0/mapDimension);
        g.getColumnConstraints().add(column1);
        g.getColumnConstraints().add(column1);
        g.getColumnConstraints().add(column1);
        g.getColumnConstraints().add(column1);
        g.getColumnConstraints().add(column1);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(100.0/mapDimension);
        g.getRowConstraints().add(row1);
        g.getRowConstraints().add(row1);
        g.getRowConstraints().add(row1);
        g.getRowConstraints().add(row1);
        g.getRowConstraints().add(row1);

        g.setGridLinesVisible(true);
        g.setHgap(7);
        g.setVgap(7);

        //g.add(cell, 0,1);
        g.add(drawBuilder(Color.BLUE, g), 0,3);
        g.add(cell, 0, 3);
        */

        //gc.fillOval(20, 0, 30, 30);
        //gc.strokeOval(20, 0, 30, 30);
        /* test purpose
        StackPane tmp;
        ImageView builder = new ImageView("file:src/main/resources/BuilderMagenta.png");
        tmp = (StackPane) tile.getChildren().get(4);
        builder.setFitWidth(tmp.getPrefWidth());
        builder.setFitHeight(tmp.getPrefHeight());
        tmp.getChildren().add(builder);

        ImageView builder1 = new ImageView("file:src/main/resources/BuilderLightBlue.png");
        tmp = (StackPane) tile.getChildren().get(2);
        builder1.setFitWidth(tmp.getPrefWidth());
        builder1.setFitHeight(tmp.getPrefHeight());
        tmp.getChildren().add(builder1);

        //I add the image to the stackpane and it refreshes automatically
        ImageView builder2 = new ImageView("file:src/main/resources/BuilderWhite.png");
        tmp = (StackPane) tile.getChildren().get(8);
        builder2.setFitWidth(tmp.getPrefWidth());
        builder2.setFitHeight(tmp.getPrefHeight());
        tmp.getChildren().add(builder2);


        tmp = (StackPane) tile.getChildren().get(6);
        tmp.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        */

        //-----> StackPane puts elements one in front of another <-----
        //-----> FlowPane puts elements one after/under another  <-----

        //topRegion.prefHeightProperty().bind(s.heightProperty().multiply(ratioFromTop));
        //anchorPane.getChildren().addAll(sampleError, btn1, godCardsBtn, buildersBtn);

        //also informations or confirmations
        /*
        Alert error = new Alert(Alert.AlertType.ERROR);
        String string = "ERROR: WRONG INSERTION";
        error.setContentText(string);
        error.setOnShowing(new EventHandler<DialogEvent>(){
            @Override
            public void handle(DialogEvent dialogEvent) {

            }

        });
        */

        /* ALTERNATIVE: the user can resize just mofifying one between height or width
        stage.setMinHeight(580);
        DoubleBinding w = stage.heightProperty().subtract(28).divide(2); stage.minWidthProperty().bind(w); stage.maxWidthProperty().bind(w);*/
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Border borderPaint (Scene s) {
        BorderStroke line1 = new BorderStroke(Color.LIGHTGREEN, Color.LIGHTGREEN, Color.LIGHTGREEN, Color.LIGHTGREEN, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
               BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT,null);
               // new Insets(s.getHeight()*ratioFromTop,s.getWidth()*ratioFromSides,s.getHeight()*ratioFromBottom,s.getWidth()*ratioFromSides));
        return new Border(line1);
    }

}