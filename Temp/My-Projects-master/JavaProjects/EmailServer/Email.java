import java.util.Date;

/**
 * Created by Saul Yin on 3/25/2016.
 */
public class Email {
    private String recipient = "";
    private String sender = "";
    private long id = 0;
    private String message = "";
    private String currentTime = "";

    public Email(String recipient, String sender, long id, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.id = id;
        this.message = message;
        Date a = new Date();
        this.currentTime = a.toString();
    }

    public String getOwner() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public long getID() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return (String.format("%s;%s;" + " " + "From: %s " + "\"%s\"", this.id, this.currentTime,
                this.sender, this.message));
    }
}
