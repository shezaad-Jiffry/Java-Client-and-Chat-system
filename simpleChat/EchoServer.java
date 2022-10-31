// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */

    ChatIF serverUI;
    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port, ChatIF serverUI) {
        super(port);
        this.serverUI = serverUI;
    }

    //Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     *
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    	serverUI.display("Message received: " + msg + " from " + client.getInfo("username"));
    	String[] userName = new String[2];
    	try {
    		userName = ((String) msg).split(" ", 2);
    	}catch(Exception e) {
    		
    		
    		try {
    			//if the username has an issue then close the client connection
    			
				client.close();
			} catch (IOException e1) {}
    	}
    	
        if(userName[0].equals("#login")) {
            
            client.setInfo("username", userName[1]);
            serverUI.display(userName[1] + " has logged on");
        }
        //echoes back
        else {
        	try {
				client.sendToClient(client.getInfo("username") + "> "+ msg);
			} catch (IOException e) {}
        }
        
        


    }

    public void handleMessageFromServerUI(String message) {
        if (message.startsWith("#")) {
            //pull apart commands with the delimeter of " "
            String[] command = new String[2];
            try {
                command = message.split(" ", 2);
                
            } catch (Exception e) {
                command[0] = message;
            }
            
            switch (command[0]) {
                //stops the server entireley 
            case "#quit":
                this.stopListening();
                this.quit();
                break;

                //stops listening for new clients
            case "#stop":
            	if(isListening()) {
                	this.stopListening();
            	}
            	else {
            		serverUI.display("server already closed");
            	}
                break;
                
                //instead of fully closing simply stops listening and closes connections
            case "#close":
            	if(this.isListening()) {
	                this.stopListening();
	                try {
	                    this.close();
	                } catch (IOException e) {}
            	}
            	else {
            		serverUI.display("server already closed");
            	}
                break;

                //get the port
            case "#getport":
                serverUI.display("current port is " + this.getPort());
                break;

                //set the port
            case "#setport":
                try {
                    this.setPort(Integer.parseInt(command[1]));
                    serverUI.display("Server port set to " + this.getPort() );
                } catch (Exception e) {
                    serverUI.display("incorrect parameter for port");
                }
                break;

                //start the server back up (relisten)
            case "#start":
            	if(!this.isListening())
	                try {
	                    this.listen();
	                } catch (IOException e) {
	                    serverUI.display("could not listen");
	                }
            	else {
            		serverUI.display("already listening");
            	}
                break;
                
            default:
                serverUI.display("invalid command");
                break;

            }
        } else {
            serverUI.display(message);
            this.sendToAllClients("SERVER MSG> " + message);
        }
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted() {
        serverUI.display("Server listening for clients on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped() {
        serverUI.display("Server has stopped listening for connections.");
    }
    /**
     * Override method that is called everytime user connects
     * sets the client that connected's userName on startup
     * and tells us who connected ie prints the username
     */
    protected void clientConnected(ConnectionToClient client) {
    	serverUI.display("a new client has connected");
        //serverUI.display(client.getInfo("username") + " connected");
    }
    /**
     * override method Called whenever a client disconnects, i.e in 
     * connectionToClient.java whenever close() is called or whenever
     * clientException is called
     */
    synchronized protected void clientDisconnected(ConnectionToClient client) {
        serverUI.display(client.getInfo("username") + " has disconnected");
        this.sendToAllClients("SERVER MSG> " + client.getInfo("username") + " has disconnected");

    }
    /**
     * override method called whenever a client throws an exception, i.e
     * the run method in connectionToClient.java catches an exception
     * calls clientDisconnected as whenever clientException is called
     * the case is usually the client has disconnected prematurley or not 
     * cleanly
     * 
     */
    synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
        clientDisconnected(client);
        

    }
    public void quit() {
        try {
            close();
        } catch (IOException e) {}
        System.exit(0);
    }

    //Class methods ***************************************************

    /**
     * This method is responsible for the creation of 
     * the server instance (there is no UI in this phase).
     *
     * @param args[0] The port number to listen on.  Defaults to 5555 
     *          if no argument is entered.
     */

}
//End of EchoServer class