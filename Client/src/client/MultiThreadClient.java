package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import javafx.scene.canvas.GraphicsContext;

public class MultiThreadClient {
    
    private static Socket socket = null;
    private static PrintStream output = null;
    private static BufferedReader input = null;
    private static boolean closed = false;
    private static String host = "localhost";
    private static int port = 2222;
    private static GraphicsContext gc;
    
    public MultiThreadClient(GraphicsContext gc){
        try{
            MultiThreadClient.gc = gc;
            socket = new Socket(host, port);
            output = new PrintStream(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(Exception e){ System.out.println("Problem creating MultiThread Client"); }
    }
    
    public void startReceiveThread(){
        new Thread(new Runnable(){
            public void run(){
                String response;
                try{
                  while((response = input.readLine()) != null){
                      parseResponse(response);
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
    
    private void parseResponse(String response){
        String[] commands = response.split(",");
        
        switch (commands[2]) {
            case "s":
                gc.beginPath();
                gc.moveTo(Double.parseDouble(commands[0]), 
                          Double.parseDouble(commands[1]));
                break;
            case "a":
                gc.lineTo(Double.parseDouble(commands[0]),
                          Double.parseDouble(commands[1]));
                gc.stroke();
                break;
        }
    }
}
