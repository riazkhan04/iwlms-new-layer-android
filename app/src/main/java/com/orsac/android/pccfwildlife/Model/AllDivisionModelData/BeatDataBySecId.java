package com.orsac.android.pccfwildlife.Model.AllDivisionModelData;

import com.google.gson.annotations.SerializedName;

public class BeatDataBySecId {
    @SerializedName("beatId")
    private String beatId;
    @SerializedName("beatName")
    private String beatName;

    public BeatDataBySecId(String beatId, String beatName) {
        this.beatId = beatId;
        this.beatName = beatName;
    }

    public String getBeatId() {
        return beatId;
    }

    public void setBeatId(String beatId) {
        this.beatId = beatId;
    }

    public String getBeatName() {
        return beatName;
    }

    public void setBeatName(String beatName) {
        this.beatName = beatName;
    }

    @Override
    public String toString() {
        return beatName;
    }
}
