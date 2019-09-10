package retrofit.response.queryType;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QueryTypeRes {

    @SerializedName("ReturnValue")
    @Expose
    private List<QueryTypeDetails> returnValue = null;
    @SerializedName("StatusCode")
    @Expose
    private String statusCode;
    @SerializedName("StatusDescription")
    @Expose
    private String statusDescription;

    public List<QueryTypeDetails> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(List<QueryTypeDetails> returnValue) {
        this.returnValue = returnValue;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
