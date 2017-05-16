package intellisysla.com.vanheusenshop.entities.report;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by serpel on 5/15/2017.
 */

public class ReportEntryPieResponse {
    private ArrayList<ReportEntry> entries;

    public ReportEntryPieResponse(ArrayList<ReportEntry> entries) {
        this.entries = entries;
    }

    public ArrayList<ReportEntry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<ReportEntry> entries) {
        this.entries = entries;
    }
}
