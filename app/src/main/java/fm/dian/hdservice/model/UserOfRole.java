package fm.dian.hdservice.model;

/**
 * Created by song on 2015/11/3.
 */
public class UserOfRole extends User {
    private boolean isAdmin;
    private boolean isOwner;

    private User user;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
