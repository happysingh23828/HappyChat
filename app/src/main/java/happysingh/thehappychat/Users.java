package happysingh.thehappychat;

/**
 * Created by Happy-Singh on 12/30/2017.
 */

public class Users  {

    String name;
    String image;
    String status;

    public Users(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.status = status;
    }

    public  Users()
    {


    }

    //Command fn + alt + insert t getter and setter

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
