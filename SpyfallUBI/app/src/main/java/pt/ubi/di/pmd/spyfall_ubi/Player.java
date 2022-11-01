package pt.ubi.di.pmd.spyfall_ubi;

public class Player {
    String name;
    Integer role;

    public Player(String name, Integer role) {
        this.name = name;
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
