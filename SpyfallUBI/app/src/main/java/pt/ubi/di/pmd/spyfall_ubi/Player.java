package pt.ubi.di.pmd.spyfall_ubi;

import java.io.Serializable;

public class Player implements Serializable {
    String name;
    Integer role;

    public Player(String name, Integer role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public Integer getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}
