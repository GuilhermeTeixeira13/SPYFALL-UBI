package pt.ubi.di.pmd.spyfall_ubi;

import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {
    String name;
    Integer role;
    Integer points;

    public Player(String name, Integer role, Integer points) {
        this.name = name;
        this.role = role;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public Integer getRole() {
        return role;
    }

    public Integer getPoints() {
        return points;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", role=" + role +
                ", points=" + points +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name) && Objects.equals(role, player.role) && Objects.equals(points, player.points);
    }
}
