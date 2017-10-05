import java.util.ArrayList;
import java.util.Arrays;

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
public class DynamicBuffer {
    private Email[] emailList;
    private int initSize = 0;
    private int lonti = 0;

    public DynamicBuffer(int initSize) {
        this.initSize = initSize;
        this.emailList = new Email[this.initSize];
    }

    public int getBufferSize() {
        return emailList.length;
    }

    public int numElements() {
        int index = 0;
        for (Email i : emailList) {
            if (i != null) {
                index++;
            } else {
                break;
            }
        }
        return index;
    }

    public void add(Email email) {
        if (numElements() == getBufferSize() - 1) {
            Email[] middle = new Email[2 * getBufferSize()];
            for (int i = 0; i < getBufferSize(); i++) {
                middle[i] = emailList[i];
            }
            emailList = middle;

        }
        emailList[numElements()] = email;
    }

    public boolean remove(int index) {
        boolean result = false;

        if (index >= 0 && index < numElements()) {
            for (int i = index; i < numElements() - 1; i++) {
                emailList[i] = emailList[i + 1];
            }
            result = true;
            emailList[numElements() - 1] = null;
        } else {
            return result;
        }

        if (numElements() <= getBufferSize() / 4) {
            if (getBufferSize() / 2 >= this.initSize) {
                Email[] middle = new Email[getBufferSize() / 2];
                for (int i = 0; i < numElements(); i++) {
                    middle[i] = emailList[i];
                }
                emailList = middle;
            }
        }
        return result;
    }

    public Email[] getEmailList() {
        return emailList;
    }

    public Email[] getNewest(int n) {
        Email[] resultEmails = new Email[n];
        ArrayList<Email> temp = new ArrayList<Email>(n);

        if (numElements() == 0 || n <= 0) {
            return null;
        }
        if (n >= numElements()) {
            Email[] specialResult = new Email[numElements()];
            for (int i = numElements() - 1; i > -1; i--) {
                temp.add(emailList[i]);
            }
            for (int i = 0; i < temp.size(); i++) {
                specialResult[i] = temp.get(i);
            }
            return specialResult;
        } else if (n == 1) {

            resultEmails[0] = emailList[numElements() - 1];
        } else {
            for (int i = numElements() - 1; i > numElements() - 1 - n; i--) {
                temp.add(emailList[i]);
            }
            for (int i = 0; i < temp.size(); i++) {
                resultEmails[i] = temp.get(i);
            }
        }
        return resultEmails;
    }

    public static void main(String[] args) {
        DynamicBuffer buffer = new DynamicBuffer(4);
        Email email = new Email("nima", "niba", 3, "4");
        buffer.add(email);
        System.out.println(Arrays.toString(buffer.getNewest(10)));
    }
}
