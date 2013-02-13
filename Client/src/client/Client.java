package client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Client extends Application {
    
    Canvas c;
    GraphicsContext gc;
    MultiThreadClient mtc;
    
    @Override
    public void start(Stage primaryStage) {
        
        c = new Canvas();
        gc = c.getGraphicsContext2D();
        mtc = new MultiThreadClient(gc);
        mtc.startReceiveThread();       
        
        /*
         * Form of incoming messages:
         *  - "xCoord,yCoord,s" => Start new Line
         *  - "xCoord,yCoord,a" => Add points to current Line
         *  - "x"               => Disconnect
         */
        c.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent e){
                gc.beginPath();
                gc.moveTo(e.getX(), e.getY());
                mtc.send(e.getX() + "," + e.getY() + ",s");
            }
        });
        
        c.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent e) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                mtc.send(e.getX() + "," + e.getY() + ",a");
            }
        });
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent e){
                mtc.send("x");
            }
        });
        
        
        Group root = new Group();
        root.getChildren().add(c);
        
        
        Scene scene = new Scene(root, 500, 450);
        c.widthProperty().bind(scene.widthProperty());
        c.heightProperty().bind(scene.heightProperty());
        
        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
