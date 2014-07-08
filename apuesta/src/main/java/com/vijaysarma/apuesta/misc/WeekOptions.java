package com.vijaysarma.apuesta.misc;

public class WeekOptions {
    private final int year;
    private final int currentWeek;
    private final int[] weeks;
    private final String type;

    public WeekOptions(int year, int currentWeek, int[] weeks, String type) {
        this.year = year;
        this.currentWeek = currentWeek;
        this.weeks = weeks;
        this.type = type;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public int getYear() {
        return year;
    }

    public int[] getWeeks() {
        return weeks;
    }

    public String getType() {
        return type;
    }

    public static class WeekOptionsBuilder {
        private int year;
        private int week;
        private String type;
        private int[] weeks;

        public WeekOptionsBuilder() {
            year = 2014;
            week = 1;
            type = "REG";
            weeks = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
        }

        public WeekOptionsBuilder setType(String type) {
            this.type = type;
            return this;
        }

        public WeekOptionsBuilder setYear(int year) {
            this.year = year;
            return this;
        }

        public WeekOptionsBuilder setCurrentWeek(int week) {
            this.week = week;
            return this;
        }

        public WeekOptions build() {
            return new WeekOptions(year, week, weeks, type);
        }
    }
}
