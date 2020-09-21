import entities.User;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Connector connector = new Connector("mysql",
                "127.0.0.1",
                "3306",
                "workshop");
        User user = new User("Daniel", "123456", 1, new Date());
        user.setId(2);
        EntityManager<User> userEntityManager = new EntityManager<>(Connector.getConnection());
        userEntityManager.persist(user);
    }
}
