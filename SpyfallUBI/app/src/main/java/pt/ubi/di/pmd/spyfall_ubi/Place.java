package pt.ubi.di.pmd.spyfall_ubi;

import java.io.Serializable;

public class Place implements Serializable {
    String name;
    String imagePath;
    String info;

    public Place(String name, String imagePath, String info) {
        this.name = name;
        this.imagePath = imagePath;
        this.info = info;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getInfo() {
        return info;
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
