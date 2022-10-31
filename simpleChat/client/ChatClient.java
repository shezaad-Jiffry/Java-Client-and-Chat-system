// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
    //Instance variables **********************************************

    /**
     * The interface type variable.  It allows the implementation of 
     * the display method in the client.
     */
    ChatIF clientUI;
    
    private String userName;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     *
     * @param host The server to connect to.
     * @param port The port number to connect on.
     * @param clientUI The interface type variable.
     */

    public ChatClient(String host, int port, ChatIF clientUI, String userName)
    throws IOException {
        super(host, port); //Call the superclass constructor
        if(userName == null)
        	System.exit(0);
        this.userName = userName;
        this.clientUI = clientUI;
        try {
        	openConnection();
        }catch(Exception e) {
        	System.out.println("cannot open connection awaiting command");
        }
        

    }

    //Instance methods ************************************************

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg) {
        clientUI.display(msg.toString());
    }
    
    

    /**
     * This method handles all data coming from the UI            
     *
     * @param message The message from the UI.    
     */
    public void handleMessageFromClientUI(String message) {
        //commands available when connected
    	
    	if(message.startsWith("#")) {
    		
    		//if possible split into 2 arguments based on command
	        String[] command = new String[2];
	        
	        try {
	            command = message.split(" ", 2);
	            
	        } catch (Exception e) {
	        	command[0] = message;
	        }
	        
	        switch (command[0]) {
	        //quit the entire console
	        case "#quit":
	            quit();
	            break;
	        //close the connection leave client on
	        case "#logoff":
	        	try {
					this.closeConnection();
				} catch (IOException e) {}
	            break;
	        //set the host name
	        case "#sethost":
	            try {
	                this.setHost(command[1]);
	                clientUI.display("host changed to " + this.getHost());
	            } catch (Exception e) {
	                clientUI.display("invalid parameter for sethost");
	
	            }
	            break;
	            
	        //set port name
	        case "#setport":
	            try {
	                this.setPort(Integer.parseInt(command[1]));
	                clientUI.display("port changed to " + this.getPort());
	            } catch (Exception e) {
	                clientUI.display("invalid parameter for setport");
	            }
	            break;
	            
	        //get host name
	        case "#gethost":
	            clientUI.display("current host is " + this.getHost());
	            break;
	            
	        //get port name
	        case "#getport":
	            clientUI.display("current port is " + this.getPort());
	            break;
	            
	        //restablish connection, throws error if already connected
	        case "#login":
	        	if(!this.isConnected()) {
		            try {
		                openConnection();
		            } catch (Exception e) {
		                clientUI.display("invalid host or port");
		            }
	        	}
	        	else
	        		clientUI.display("already connected");
	        	break;
	        default:
	            clientUI.display("invalid command given");
	            break;
	        }
    	}
    	else {
            try {
                sendToServer(message);
            } catch (IOException e) {
                clientUI.display("Could not send message to server.  Terminating client.");
                quit();
            }
    	}
    }

    /**
     * Override method called whenever a connection is established
     * between the client and server, simply prints a reaferiming 
     * message to the console. Called at the start of the run method 
     * in AbstractClient
     */
    public void connectionEstablished() {
        clientUI.display(userName + " has logged on");
        try {
			sendToServer("#login " + userName);
		} catch (IOException e) {}

    }

    /**
     * Override method that is called whenever the connection between
     * client and server is terminated, simply prints a reaferming message
     * to the console. called whenever closeConnection is called
     */
    public void connectionClosed() {
        clientUI.display("Connection between server and client terminated");

    }

    /**
     * Override method that is called whenever the client raises an exception
     * that would interfere with it talking to the server i.e in the run method
     * of AbstractClient
     */
    public void connectionException(Exception e) {
        if (e.getMessage() == "Connection reset") {
            clientUI.display("fatal connection error, terminating client");
            quit();
        } else {
            try {
                closeConnection();
            } catch (Exception e1) {

            }
        }
    }
    /**
     * This method terminates the client.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException e) {}
        System.exit(0);
    }
}
//End of ChatClient class