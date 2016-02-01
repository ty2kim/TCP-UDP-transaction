import java.io.*;
import java.net.*;

public class server{
	/* variables */
	private static final int maxBsize = 66535; // maximum byte allowed for java
	private static int n_port = 5840;
	private static int r_port;
	private static ServerSocket TCPsocket = null;
	private static Socket connectionSocket = null;
	private static DatagramSocket UDPsocket = null;
	private static BufferedReader inFromClient = null;
	private static DataOutputStream outToClient = null;
	private static byte[] dataSend = null;
	private static byte[] dataReceive = null;
	private static DatagramPacket packetSend;
	private static DatagramPacket packetReceive;
	private static String msgReversed;
	
	public static void main(String argv[]) throws IOException {
		// arguments check
		if(argv.length != 0){
			System.err.println("There shouldn't be any arguments");
			System.exit(1);
		}
		
		// Stage 1. Negotiation using TCP sockets
		negotiation();
		
		// Stage 2. Transaction using UDP sockets
		transaction();
	}
	
	public static void negotiation() throws IOException{
		
		try {
			// creates TCP connection
			TCPsocket = new ServerSocket(n_port);
			
			// checking availability of the port number selected
			if(TCPsocket == null){
				TCPsocket = new ServerSocket(0);
				n_port = TCPsocket.getLocalPort();
			}
		} 
		catch(IOException e){
			System.err.println("ERROR[SERVER]: "+e.getMessage());
			System.exit(1);
		}
		
		// print port number
		System.out.println("n_port: "+n_port);
		
		try {
			// waits for client to reply back
			connectionSocket = TCPsocket.accept();
			
			// gets the request from client(which is 13)
			inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			inFromClient.readLine();
			
			// creates random port number
			UDPsocket = new DatagramSocket(0);
			r_port = UDPsocket.getLocalPort();
			
			// sends the random port number to client
			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			outToClient.writeBytes(Integer.toString(r_port)+'\n');
		}
		catch(Exception e) {
			System.err.println("ERROR[SERVER]: "+e.getMessage());
			System.exit(1);
		}
		finally{
			TCPsocket.close();
			inFromClient.close();
			outToClient.close();
		}
		
	}
	public static void transaction(){
		dataSend = new byte[maxBsize];
		dataReceive = new byte[maxBsize];
		try {
			// creates packet for receiving
			packetReceive = new DatagramPacket(dataReceive, dataReceive.length);
			
			// receives message from client
			UDPsocket.receive(packetReceive);
			
			// reverse the message
			msgReversed = new StringBuffer((new String(packetReceive.getData())).trim()).reverse().toString();
			
			// creates packet for sending
			dataSend = msgReversed.getBytes();
			packetSend = new DatagramPacket(dataSend, dataSend.length, packetReceive.getAddress(), packetReceive.getPort());
			
			// send the reversed message back to client
			UDPsocket.send(packetSend);
		}
		catch(IOException e) {
			System.err.println("ERROR[SERVER]: "+e.getMessage());
			System.exit(1);
		}
		finally {
			UDPsocket.close();
		}
	}
}
