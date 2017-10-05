import java.io.IOException;
import java.util.*;
import java.io.*;

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

public class EmailServer {
    // Useful constants
    public static final String FAILURE = "FAILURE";
    public static final String DELIMITER = "\t";
    public static final String SUCCESS = "SUCCESS";
    public static final String CRLF = "\r\n";
    private ArrayList<User> users;
    public String fileName;

    // Used to print out extra information
    private boolean verbose = false;

    public ArrayList<User> getUsers() {
        return this.users;
    }

    public EmailServer() {
        this.users = new ArrayList<>(1);
        this.users.add(0, new User());
    }

    public EmailServer(String fileName) throws IOException {
        this.users = new ArrayList<>(1);
        this.users.add(0, new User());
        File f = new File(fileName);
        this.fileName = fileName;
        if (f.exists() && !f.isDirectory()) {
            FileReader fr = new FileReader(f);
            BufferedReader bfr = new BufferedReader(fr);
            while (true) {
                String s = bfr.readLine();
                if (s == null) {
                    break;
                }
                if (s.indexOf(",") == -1) {
                    continue;
                }
                String[] userInfo = s.split(",");
                User newGuy = new User(userInfo[0], userInfo[1]);
                this.users.add(newGuy);
            }
            bfr.close();
            fr.close();
        } else {
            f.createNewFile();
            new EmailServer(fileName);
        }
    }


    ErrorFactory factory = new ErrorFactory();

    public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.printf("Input Server Request: ");
            String command = in.nextLine();

            command = replaceEscapeChars(command);

            if (command.equalsIgnoreCase("kill") || command.equalsIgnoreCase("kill\r\n"))
                break;

            if (command.equalsIgnoreCase("verbose") || command.equalsIgnoreCase("verbose\r\n")) {
                verbose = !verbose;
                System.out.printf("VERBOSE has been turned %s.\n\n", verbose ? "on" : "off");
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
                System.out.print("response: ");
            System.out.println(this.getUsers().size());
            System.out.println(this.getUsers().get(0).getName());
            for (int i = 0; i < this.getUsers().size(); i++) {
                System.out.print(this.getUsers().get(i).getName());
            }
            System.out.printf("\"%s\"\n\n", response);
        }

        in.close();
    }


    /**
     * Determines which client command the request is using and calls
     * the function associated with that command.
     *
     * @param request - the full line of the client request (CRLF included)
     * @return the server response
     */
    public String parseRequest(String request) {

        String result = "FAILURE";
        String[] resultList;
        String[] temp;
        String[] commands = {"ADD-USER", "DELETE-USER", "GET-ALL-USERS", "SEND-EMAIL", "GET-EMAILS", "DELETE-EMAIL"};
        try {
            resultList = request.split("\t");
            if (!resultList[resultList.length - 1].endsWith("\r\n")) {
                return (factory.makeErrorMessage(-10));
            }
            temp = resultList[resultList.length - 1].split("\r\n");
        } catch (Exception e) {
            return (factory.makeErrorMessage(-11));
        }
        resultList[resultList.length - 1] = temp[0];

        int check = 0;
        for (int i = 0; i < commands.length; i++) {
            if (resultList[0].equals(commands[i])) {
                check = 1;
                if (i == 0 || i == 1 || i == 2) {
                    if (resultList.length != 3) {
                        return (factory.makeErrorMessage(-10));
                    }
                } else if (i == 4 || i == 5) {
                    if (resultList.length != 4) {
                        return (factory.makeErrorMessage(-10));
                    }
                } else if (i == 3) {
                    if (resultList.length != 5) {
                        return (factory.makeErrorMessage(-10));
                    }
                }
            }
        }
        if (check == 0) {
            return (factory.makeErrorMessage(-11));
        }

        User theGuy = new User(resultList[1], resultList[2]);

        if (!theGuy.isValid()) {
            return (factory.makeErrorMessage(-23));
        }
        if (!resultList[0].equals("ADD-USER")) {
            int find = 0;
            for (User i : this.users) {
                if (i.getName().equals(resultList[1])) {
                    find = 1;
                    if (!i.checkPassword(resultList[2])) {
                        return (factory.makeErrorMessage(-21));
                    }
                    break;
                }
            }
            if (find == 0) {
                return (factory.makeErrorMessage(-20));
            }
        } else {
            int find = 0;
            for (User i : this.users) {
                if (resultList[1].equals(i.getName())) {
                    find = 1;
                }
            }
            if (find == 1) {
                return (factory.makeErrorMessage(-22));
            }
        }
        try {
            switch (resultList[0]) {
                case ("ADD-USER"):
                    try {
                        return (addUser(resultList));
                    } catch (Exception e) {
                        return (factory.makeErrorMessage(-1) + "\r\n");
                    }

                case ("DELETE-USER"):
                    try {
                        return (deleteUser(resultList));
                    } catch (Exception e) {
                        return (factory.makeErrorMessage(-10));
                    }

                case ("GET-ALL-USERS"):
                    try {
                        return (getAllUsers(resultList));
                    } catch (Exception e) {
                        return (factory.makeErrorMessage(-20));
                    }

                case ("SEND-EMAIL"):
                    try {
                        int find = 0;
                        for (User i : this.users) {
                            if (i.getName().equals(resultList[3])) {
                                find = 1;
                                return (sendEmail(resultList));
                            }
                        }
                        if (find == 0) {
                            return (factory.makeErrorMessage(-20));
                        }
                        break;
                    } catch (Exception e) {
                        return (factory.makeErrorMessage(-23));
                    }

                case ("GET-EMAILS"):
                    try {
                        return (getEmails(resultList));
                    } catch (Exception e) {
                        return (factory.makeErrorMessage(-23));
                    }
                case ("DELETE-EMAIL"):
                    try {
                        Object test = resultList[3];
                        if (!(test instanceof Long)) {
                            return (factory.makeErrorMessage(-23));
                        }
                        return (deleteEmail(resultList));
                    } catch (Exception e) {
                        return (factory.makeErrorMessage(-1));
                    }
                default:
                    return (factory.makeErrorMessage(-11));
            }

        } catch (Exception e) {
            return (factory.makeErrorMessage(-11));
        }
        return ("SUCCESS\r\n");
    }

    /**
     * Replaces "poorly formatted" escape characters with their proper
     * values. For some terminals, when escaped characters are
     * entered, the terminal includes the "\" as a character instead
     * of entering the escape character. This function replaces the
     * incorrectly inputed characters with their proper escaped
     * characters.
     *
     * @param str - the string to be edited
     * @return the properly escaped string
     */
    private static String replaceEscapeChars(String str) {
        str = str.replace("\\r\\n", "\r\n"); // may not be necessary, but just in case
        str = str.replace("\\r", "\r");
        str = str.replace("\\n", "\n");
        str = str.replace("\\t", "\t");
        str = str.replace("\\f", "\f");

        return str;
    }

    public String addUser(String[] args) {
        User newGuy = new User(args[1], args[2]);
        this.users.add(newGuy);
        if (this.fileName != null) {
            File f = new File(this.fileName);
            try {
                FileOutputStream fo = new FileOutputStream(f, true);
                PrintWriter pw = new PrintWriter(fo);
                String userInfo = args[1] + "," + args[2] + "\n";
                pw.println(userInfo);
                pw.close();
            } catch (IOException e) {
                return (factory.makeErrorMessage(-1));
            }
        }

        return ("SUCCESS\r\n");
    }

    public String getAllUsers(String[] args) {
        String data = "";
        int check = 0;
        for (User i : this.users) {
            if (args[1].equals(i.getName())) {
                check = 1;
                break;
            } else {
                return (factory.makeErrorMessage(-21));
            }
        }
        if (check == 1) {
            for (User j : this.users) {
                data += ("\t" + j.getName());
            }
            return (SUCCESS + data + "\r\n");
        }
        return (factory.makeErrorMessage(-20));
    }

    private void deleteText(ArrayList<String> list) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName));
            for (String i : list) {
                writer.write(i);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.print(factory.makeErrorMessage(-1));
        }
    }

    public String deleteUser(String[] args) {
        for (int i = 0; i < this.users.size(); i++) {
            if (args[1].equals(this.users.get(i).getName())) {
                if (!args[1].equals("root")) {
                    this.users.remove(i);
                    if (this.fileName != null) {
                        File f = new File(fileName);
                        try {
                            ArrayList<String> content = new ArrayList<>(4);
                            FileReader fr = new FileReader(f);
                            BufferedReader bfr = new BufferedReader(fr);
                            while (true) {
                                String s = bfr.readLine();
                                if (s == null) {
                                    break;
                                }
                                if (s.indexOf(",") == -1) {
                                    continue;
                                }
                                String find = args[1];
                                if (!find.equals(s.substring(0, s.indexOf(",")))) {
                                    content.add(s);
                                }
                                System.out.println(content.toString());
                                this.deleteText(content);
                            }
                            bfr.close();

                        } catch (IOException e) {
                            System.out.println("fuck");
                        }
                    }
                    return (SUCCESS + "\r\n");
                } else {
                    return (factory.makeErrorMessage(-23));
                }
            }
        }
        return (factory.makeErrorMessage(-20));
    }

    public String sendEmail(String[] args) {
        String message = args[4].trim();
        for (User i : this.users) {
            if (args[3].equals(i.getName())) {
                if (message.length() >= 1) {
                    i.receiveEmail(args[1], args[4]);
                    return (SUCCESS + "\r\n");
                }
                return (factory.makeErrorMessage(-23));
            }
        }
        return (factory.makeErrorMessage(-20));
    }

    public String getEmails(String[] args) {
        String data = "";
        for (User i : this.users) {
            if (args[1].equals(i.getName())) {
                if (Integer.parseInt(args[3]) >= 1) {
                    Email[] temp = i.retrieveEmail(Integer.parseInt(args[3]));
                    if (temp == null) {
                        return (SUCCESS + "\r\n");
                    }
                    for (Email j : temp) {
                        data += ("\t" + j.toString());
                    }
                    return (SUCCESS + data + CRLF);
                }
                return (factory.makeErrorMessage(-23));
            }
        }
        return (factory.makeErrorMessage(-20));
    }

    public String deleteEmail(String[] args) {
        for (User i : this.users) {
            if (args[1].equals(i.getName())) {
                if (i.numEmail() == 0) {
                    return (factory.makeErrorMessage(-23));
                }
                ArrayList<Long> idList = new ArrayList<>(i.numEmail());
                Email[] command = i.retrieveEmail(i.numEmail());
                for (Email j : command) {
                    idList.add(j.getID());
                }
                for (long k : idList) {
                    if (Long.parseLong(args[3]) == k) {
                        i.removeEmail(Long.parseLong(args[3]));
                        return (SUCCESS + CRLF);
                    }
                }
                return (factory.makeErrorMessage(-23));
            }
        }
        return (factory.makeErrorMessage(20));
    }

    /**
     * This main method is for testing purposes only.
     *
     * @param args - the command line arguments
     */

    public static void main(String[] args) {
        (new EmailServer()).run();
    }
}

