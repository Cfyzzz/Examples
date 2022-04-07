package ru.nedovizin.homeaccountancy.models;

import ru.nedovizin.homeaccountancy.database.BaseLab;

public class NameCategory {
    private int id = -1;
    private String name;
    private TypeOperation type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeOperation getType() {
        return type;
    }

    public void setType(TypeOperation type) {
        this.type = type;
    }

    public NameCategory(String name, TypeOperation type) {
        this.name = name;
        this.type = type;
    }

    public int getId() {
        if (id == -1) {
            BaseLab mBaseLab = BaseLab.get(null);
            copyOf(mBaseLab.getNameCategoryByName(name));
        }
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    public static NameCategory getById(int id) {
        BaseLab mBaseLab = BaseLab.get(null);
        return mBaseLab.getNameCategoryById(id);
    }

    public void copyOf(NameCategory other) {
        this.setName(other.getName());
        this.setType(other.getType());
        this.setId(other.getId());
    }
}
