package happysingh.thehappychat;

/**
 * Created by Happy-Singh on 1/4/2018.
 */

public class requests  {

    String req_type;

    public  requests()
    {

    }
    public requests(String req_type) {
        this.req_type = req_type;
    }

    public String getReq_type() {
        return req_type;
    }

    public void setReq_type(String req_type) {
        this.req_type = req_type;
    }
}
