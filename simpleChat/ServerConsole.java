import java.util.Scanner;

import common.ChatIF;

public class ServerConsole implements ChatIF {
	Scanner fromConsole;
	EchoServer echoServer;
	final public static int DEFAULT_PORT = 5555;
	
	
	public ServerConsole(int port) {
		fromConsole = new Scanner(System.in);
		try {
			echoServer = new EchoServer(port, this);
		}
		catch(Exception e) {
            System.out.println("Error: Can't setup server" +
                    " Terminating server.");
                System.exit(1);
		}
	}
	
    public void accept() {
        try {

            String message;

            while (true) {
                message = fromConsole.nextLine();
                
                echoServer.handleMessageFromServerUI(message);
                
            }
        } catch (Exception ex) {
            System.out.println("Unexpected error while reading from console!");
        }
    }
    
	public void display(String message) {
		
		System.out.println("> " + message);
		
	}
	
	
    public static void main(String[] args) {
        int port = 0; //Port to listen on

        try {
            port = Integer.parseInt(args[0]); //Get port from command line
        } catch (Throwable t) {
            port = DEFAULT_PORT; //Set port to 5555
        }

        ServerConsole sc = new ServerConsole(port);

        try {
            sc.echoServer.listen(); //Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
        
        sc.accept();
    }

}
