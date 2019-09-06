package retrofit.response.status_type;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusTypeDetails {

    @SerializedName("StatusID")
    @Expose
    private String statusID;
    @SerializedName("StatusCode")
    @Expose
    private String statusCode;
    @SerializedName("StatusType")
    @Expose
    private String statusType;
    @SerializedName("StatusDesc")
    @Expose
    private String statusDesc;

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
}
