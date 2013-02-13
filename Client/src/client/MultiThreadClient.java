package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.canvas.GraphicsContext;

public class MultiThreadClient {
    
    private static Socket socket = null;
    private static PrintStream output = null;
    private static BufferedReader input = null;
    private static String host = "localhost";
    private static int port = 2222;
    private static GraphicsContext gc;
    private static Map<String, Command> commands;
    
    public MultiThreadClient(GraphicsContext gc){
        try{
            socket = new Socket(host, port);
            output = new PrintStream(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            MultiThreadClient.gc = gc;
            commands = new HashMap<>();
            commands.put("s", new StartLine());
            commands.put("a", new AddToLine());
        } catch(Exception e){ System.out.println("Problem creating MultiThread Client"); }
    }
    
    public void startReceiveThread(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                String response;
                try{
                  while((response = input.readLine()) != null){
                      String[] split = response.split(",");
                      double x = Double.parseDouble(split[0]), 
                             y = Double.parseDouble(split[1]);
                      commands.get(split[2]).doIt(gc, x, y);
                  }  
                } catch(Exception e){ System.out.println("Problem parsing response"); }
            }
        }).start();
    }
    
    public void send(String message){
        if(socket != null && input != null && output != null){
            output.println(message.trim());
        }
    }
    
    // I shoulda just put these in separate files
    abstract class Command {
        public abstract void doIt(GraphicsContext gc, double x, double y);
    }
    
    class StartLine extends Command {
        @Override
        public void doIt(GraphicsContext gc, double x, double y){
            gc.beginPath();
            gc.moveTo(x, y);
        }
    }
    
    class AddToLine extends Command {
        @Override
        public void doIt(GraphicsContext gc, double x, double y){
            gc.lineTo(x, y);
            gc.stroke();
        }
    }
}
