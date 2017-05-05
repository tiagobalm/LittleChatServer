import database.users.UserRequests;

public class Main {

    public static void main(String[] args) {
        String username = args[0];
        String password = args[1];
        boolean result = UserRequests.validateUser(username, password);
        System.out.println(result);
    }
}
