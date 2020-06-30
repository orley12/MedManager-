package com.example.android.med_manager.model;

public class Medication {

    private String medicationName;
    private int medicationType;
    private String medicationDescription;
    private int medicationDosage;
    private int medicationInterval;
    private long medicationStartDate;
    private long medicationEndDate;
    private long medicationStartTime;
    private int medicationTakenCount;
    private int medicationIgnoreCount;

    public Medication(String medicationName, int medicationType, String medicationDescription,
                      int medicationDosage, int medicationInterval, long medicationStartDate,
                      long medicationEndDate, long medicationStartTime, int medicationTakenCount,
                      int medicationIgnoreCount) {

        this.medicationName = medicationName;
        this.medicationType = medicationType;
        this.medicationDescription = medicationDescription;
        this.medicationDosage = medicationDosage;
        this.medicationInterval = medicationInterval;
        this.medicationStartDate = medicationStartDate;
        this.medicationEndDate = medicationEndDate;
        this.medicationStartTime = medicationStartTime;
        this.medicationTakenCount = medicationTakenCount;
        this.medicationIgnoreCount = medicationIgnoreCount;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public int getMedicationType() {
        return medicationType;
    }

    public void setMedicationType(int medicationType) {
        this.medicationType = medicationType;
    }

    public String getMedicationDescription() {
        return medicationDescription;
    }

    public void setMedicationDescription(String medicationDescription) {
        this.medicationDescription = medicationDescription;
    }

    public int getMedicationDosage() {
        return medicationDosage;
    }

    public void setMedicationDosage(int medicationDosage) {
        this.medicationDosage = medicationDosage;
    }

    public int getMedicationInterval() {
        return medicationInterval;
    }

    public void setMedicationInterval(int medicationInterval) {
        this.medicationInterval = medicationInterval;
    }

    public long getMedicationStartDate() {
        return medicationStartDate;
    }

    public void setMedicationStartDate(long medicationStartDate) {
        this.medicationStartDate = medicationStartDate;
    }

    public long getMedicationEndDate() {
        return medicationEndDate;
    }

    public void setMedicationEndDate(long medicationEndDate) {
        this.medicationEndDate = medicationEndDate;
    }

    public long getMedicationStartTime() {
        return medicationStartTime;
    }

    public void setMedicationStartTime(long medicationStartTime) {
        this.medicationStartTime = medicationStartTime;
    }

    public int getMedicationTakenCount() {
        return medicationTakenCount;
    }

    public void setMedicationTakenCount(int medicationTakenCount) {
        this.medicationTakenCount = medicationTakenCount;
    }

    public int getMedicationIgnoreCount() {
        return medicationIgnoreCount;
    }

    public void setMedicationIgnoreCount(int medicationIgnoreCount) {
        this.medicationIgnoreCount = medicationIgnoreCount;
    }

}
