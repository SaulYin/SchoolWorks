/**
 * <b> CS 180 - Project 4 - Email Server Skeleton </b>
 * <p>
 * <p>
 * This is the skeleton code for the EmailServer Class. This is a private email
 * server for you and your friends to communicate.
 *
 * @author Sihao Yin <(yin93@purdue.edu)>
 * @version (2016.3.26)
 * @lab (L15)
 */
import cs180.net.Socket;
import cs180.net.ServerSocket;

import java.io.*;
import java.util.Scanner;


public class OnlineEmailServer extends EmailServer {
    ServerSocket serverSocket;
    private boolean verbose = false;

    public OnlineEmailServer(String filename, int port) throws IOException {
        super(filename);
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                this.processClient(socket);
                socket.setSoTimeout(6000);
            }
        } catch (IOException e) {
            System.out.println(factory.makeErrorMessage(-1));
        }
    }

    public void processClient(Socket client) throws IOException {
        PrintWriter pw = new PrintWriter(client.getOutputStream());

        Scanner in = new Scanner(client.getInputStream());
        while (true) {
            pw.printf("Input Server Request: ");
            String command = in.nextLine();

            if (command.equalsIgnoreCase("kill") || command.equalsIgnoreCase("kill\r\n"))
                break;

            if (command.equalsIgnoreCase("verbose") || command.equalsIgnoreCase("verbose\r\n")) {
                verbose = !verbose;
                pw.printf("VERBOSE has been turned %s.\n\n", verbose ? "on" : "off");
                continue;
            }

            String response = null;
            try {
                response = parseRequest(command);
            } catch (Exception ex) {
                response = ErrorFactory.makeErrorMessage(ErrorFactory.UNKNOWN_ERROR,
                        String.format("An exception of %s occurred.", ex.getClass().toString()));
            }


            //if (response.startsWith("SUCCESS" + DELIMITER))
            //	response = response.replace(DELIMITER, NEWLINE);
            if (response.startsWith(FAILURE) && !DELIMITER.equals("\t"))
                response = response.replace(DELIMITER, "\t");

            if (verbose)
                pw.printf("response: ");
            pw.printf("\"%s\"\n\n", response);
        }

        in.close();
        pw.close();
    }

    public void stop() {

    }
}
