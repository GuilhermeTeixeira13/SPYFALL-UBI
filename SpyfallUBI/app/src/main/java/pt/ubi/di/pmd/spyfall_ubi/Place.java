package pt.ubi.di.pmd.spyfall_ubi;

import java.io.Serializable;
import java.util.Objects;

public class Place implements Serializable {
    String name;
    String imagePath;
    String info;
    String category;

    public Place(String name, String imagePath, String info, String category) {
        this.name = name;
        this.imagePath = imagePath;
        this.info = info;
        this.category = category;
    }

    public void setCategory(String  category) {
        this.category = category;
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

    public String  getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", info='" + info + '\'' +
                ", cat=" + category +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(name, place.name) && Objects.equals(imagePath, place.imagePath) && Objects.equals(info, place.info) && Objects.equals(category, place.category);
    }
}
