package ru.nedovizin.homeaccountancy.models;

import android.graphics.Color;

import java.time.LocalDate;
import java.util.Date;

import ru.nedovizin.homeaccountancy.Period;
import ru.nedovizin.homeaccountancy.database.BaseLab;

public class Category {
    private int id = -1;
    private int priority;
    private Period period;
    private TypeOperation type;
    private int color;
    private NameCategory nameCategory;
    private int exposed;
    private int reserved;
    private int planned;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public TypeOperation getType() {
        return type;
    }

    public void setType(TypeOperation type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return nameCategory.getName();
    }

    public void setName(String name) {
        BaseLab mBaseLab = BaseLab.get(null);
        NameCategory nameCategory = mBaseLab.getNameCategoryByName(name);
        if (nameCategory == null) {
            mBaseLab.addNameCategory(name, getType());
            nameCategory = mBaseLab.getNameCategoryByName(name);
        }
        this.nameCategory = nameCategory;
    }

    public NameCategory getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(NameCategory nameCategory) {
        this.nameCategory = nameCategory;
    }

    public int getExposed() {
        return exposed;
    }

    public void setExposed(int exposed) {
        this.exposed = exposed;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getPlanned() {
        return planned;
    }

    public void setPlanned(int planned) {
        this.planned = planned;
    }

    public int getId() {
        if (id == -1) {
            BaseLab mBaseLab = BaseLab.get(null);
            copyOf(mBaseLab.getCategoryByName(nameCategory.getName()));
        }
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category(String name, TypeOperation type) {
        priority = 1;
        period = new Period();
        color = Color.RED;
        exposed = 0;
        reserved = 0;
        planned = 0;
        setType(type);
        setName(name);
    }

    public Category(NameCategory nameCategory, TypeOperation type) {
        this(nameCategory.getName(), type);
    }

    public void copyOf(Category other) {
        this.setName(other.getName());
        this.setType(other.getType());
        this.setId(other.getId());
        this.setNameCategory(other.getNameCategory());
        this.setPlanned(other.getPlanned());
        this.setReserved(other.getReserved());
        this.setExposed(other.getExposed());
        this.setColor(other.getColor());
        this.setPeriod(other.getPeriod());
    }

    public static Category getById(int id) {
        BaseLab mBaseLab = BaseLab.get(null);
        return mBaseLab.getCategoryById(id);
    }
}
