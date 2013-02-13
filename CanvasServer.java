import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;


public class CanvasServer {
	
	private static ServerSocket serverSocket = null;
	private static Socket socket = null;
	private static final int maxClients = 10;
	private static final ClientThread[] clients = new ClientThread[maxClients];


	public static void main(String[] args){
		int port = 2222;
		try{
			serverSocket = new ServerSocket(port);
		} catch(IOException e) { e.printStackTrace(); }


		while(true){
			try{
				socket = serverSocket.accept();
				int i = 0;
				for(i = 0; i < maxClients; ++i){
					if(clients[i] == null){
						(clients[i] = new ClientThread(socket, clients)).start();
						break;
					}
				}
			} catch(Exception e) { System.out.println("Problem starting Client"); }
		}

	}
}



class ClientThread extends Thread {

	private String clientName = null;
	private BufferedReader input = null;
	private PrintStream output = null;
	private Socket socket = null;
	private final ClientThread[] clients;
	private int maxClients;

	public ClientThread(Socket socket, ClientThread[] clients){
		this.socket = socket;
		this.clients = clients;
		maxClients = clients.length;
	}

	public void run(){
		int maxClients = this.maxClients;
		ClientThread[] clients = this.clients;

		try{
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintStream(socket.getOutputStream());
			String name;

			while(true){
				String line = input.readLine();
				if(line.equals("x")) { break; }
				synchronized(this){
					for(int i = 0; i < maxClients; ++i){
						if(clients[i] != null){
							clients[i].output.println(line);
						}
					}
				}
			}
			// It'll never break outta the loop but whatevs		
		} catch(Exception e) { System.out.println("Problem in Client Thread"); }
	}

}






