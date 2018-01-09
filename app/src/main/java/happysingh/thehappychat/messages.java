package happysingh.thehappychat;

/**
 * Created by Happy-Singh on 1/4/2018.
 */

public class messages {

    String message;
    Long timestamp;
    Boolean seen;
    String type;
    String from;

    public  messages()
    {


    }
    public messages(String message, Long timestamp, Boolean seen, String type, String from) {
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
        this.type = type;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
