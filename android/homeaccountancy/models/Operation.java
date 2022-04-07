package ru.nedovizin.homeaccountancy.models;


import ru.nedovizin.homeaccountancy.Period;

public class Operation {
    private int id = -1;
    private int value;
    private Category category;
    private Period period;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }
}
