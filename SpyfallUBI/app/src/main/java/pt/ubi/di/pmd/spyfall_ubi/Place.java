package pt.ubi.di.pmd.spyfall_ubi;

public class Place {
    String name;
    String imagePath;
    String info;

    public Place(String name, String imagePath, String info) {
        this.name = name;
        this.imagePath = imagePath;
        this.info = info;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
