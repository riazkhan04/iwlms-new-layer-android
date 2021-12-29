package com.orsac.android.pccfwildlife.Model;

public class IncidentTypeModel {

    private String incident_name;
    private String incident_value;

    public IncidentTypeModel(String incident_name, String incident_value) {
        this.incident_name = incident_name;
        this.incident_value = incident_value;
    }

    public String getIncident_name() {
        return incident_name;
    }

    public void setIncident_name(String incident_name) {
        this.incident_name = incident_name;
    }

    public String getIncident_value() {
        return incident_value;
    }

    public void setIncident_value(String incident_value) {
        this.incident_value = incident_value;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\""+incident_name + '\"' +
                ", \"value\":\"" + incident_value + '\"' +
                '}';

        ////        return "{" +
        ////                "\u005c\u0022name\u005c\u0022:\u005c\u0022"+incident_name + '\"' +
        ////                ", \u005c\u0022value\u005c\u0022:\u005c\u0022" + incident_value + '\"' +
        ////                '}';
    }
}
