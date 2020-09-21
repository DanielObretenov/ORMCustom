package entities;

import anotations.Column;
import anotations.Entity;
import anotations.Id;

import java.util.Date;

@Entity(name = "users")
public class User {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;
    @Column(name = "age")
    private int age;
    @Column(name = "registrationDate")
    private Date registrationDate;



    public User(String name, String password, int age, Date registrationDate) {
        this.id = null;
        this.name = name;
        this.password = password;
        this.age = age;
        this.registrationDate = registrationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }
}
