package happysingh.thehappychat;

/**
 * Created by Happy-Singh on 1/4/2018.
 */

public class chat    {
    Boolean seen;
    Long timestamp;
    String message;
    String from;
    String type;

    public chat(Boolean seen, Long timestamp, String message, String from, String type) {
        this.seen = seen;
        this.timestamp = timestamp;
        this.message = message;
        this.from = from;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public chat() {

    }


    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
