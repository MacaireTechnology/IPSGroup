package retrofit.response.dept;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeptRes {

    @SerializedName("ReturnValue")
    @Expose
    private List<DeptDetails> returnValue = null;
    @SerializedName("StatusCode")
    @Expose
    private String statusCode;
    @SerializedName("StatusDescription")
    @Expose
    private String statusDescription;

    public List<DeptDetails> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(List<DeptDetails> returnValue) {
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
