//Subject.java
public class User {
    private String userId;
    private String name;
    private boolean isStudent;

    public User(String userId, String name, boolean isStudent) {
        this.userId = userId;
        this.name = name;
        this.isStudent = isStudent;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public boolean isStudent() { return isStudent; }

    @Override
    public String toString() {
        return name + " (" + (isStudent ? "Student" : "Staff") + ")";
    }
}

