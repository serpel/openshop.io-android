package intellisysla.com.vanheusenshop.entities.report;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by serpel on 5/15/2017.
 */

public class ReportEntryLineResponse {

    @SerializedName("firstline")
    private ArrayList<ReportEntry> firstLine;
    @SerializedName("secondline")
    private ArrayList<ReportEntry> secondLine;

    public ReportEntryLineResponse(ArrayList<ReportEntry> firstLine, ArrayList<ReportEntry> secondLine) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
    }

    public ArrayList<ReportEntry> getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(ArrayList<ReportEntry> firstLine) {
        this.firstLine = firstLine;
    }

    public ArrayList<ReportEntry> getSecondLine() {
        return secondLine;
    }

    public void setSecondLine(ArrayList<ReportEntry> secondLine) {
        this.secondLine = secondLine;
    }
}
