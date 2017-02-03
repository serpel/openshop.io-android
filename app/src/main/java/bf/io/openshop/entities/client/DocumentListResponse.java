package bf.io.openshop.entities.client;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alienware on 2/3/2017.
 */

public class DocumentListResponse {

    @SerializedName("records")
    private List<Document> documents;

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
