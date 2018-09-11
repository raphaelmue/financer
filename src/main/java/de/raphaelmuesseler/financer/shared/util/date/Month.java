package de.raphaelmuesseler.financer.shared.util.date;

import de.raphaelmuesseler.financer.client.ui.I18N;

public enum Month {
    JANUARY(1, "january"),
    FEBRUARY(2, "february"),
    MARCH(3, "march"),
    APRIL(4, "april"),
    MAY(5, "may"),
    JUNE(6, "june"),
    JULY(7, "july"),
    AUGUST(8, "august"),
    SEPTEMBER(9, "september"),
    OCTOBER(10, "october"),
    NOVEMBER(11, "november"),
    DECEMBER(12, "december");

    private final int number;
    private final String key;

    Month(int number, String key) {
        this.number = number;
        this.key = key;
    }

    public static Month getMonthByNumber(int number) {
        for (Month month : values()) {
            if (month.getNumber() == number) {
                return month;
            }
        }
        return null;
    }

    public int getNumber() {
        return number;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return I18N.get(this.key);
    }

    @Override
    public String toString() {
        return this.getName() + " (" + this.getNumber() + ")";
    }
}
