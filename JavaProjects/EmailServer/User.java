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
public class User {
    private String username = "root";
    private String password = "cs180";
    DynamicBuffer buf = new DynamicBuffer(2);

    public User() {
        this.username = "root";
        this.password = "cs180";
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return this.username;
    }

    public boolean checkPassword(String password) {
        if (password.equals(this.password)) {
            return true;
        }
        return false;
    }

    public int numEmail() {
        return buf.numElements();
    }

    public void receiveEmail(String sender, String message) {
        Email email = new Email(username, sender, buf.numElements(), message);
        buf.add(email);
    }

    public Email[] retrieveEmail(int n) {
        return buf.getNewest(n);
    }

    public boolean removeEmail(long emailID) {
        for (int i = 0; i < buf.numElements(); i++) {
            if (emailID == buf.getEmailList()[i].getID()) {
                buf.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean isValid() {
        String[] nameList = username.split("");
        String[] passList = password.split("");

        if (nameList.length > 20 || nameList.length < 1) {
            return false;
        }

        if (passList.length > 40 || passList.length < 4) {
            return false;
        }

        return (username.matches("[A-Za-z0-9]+") && password.matches("[A-Za-z0-9]+"));
    }
}
