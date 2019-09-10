package retrofit.response.team;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetTeamRes {

    @SerializedName("ReturnValue")
    @Expose
    private List<GetTeamDetails> returnValue = null;
    @SerializedName("StatusCode")
    @Expose
    private String statusCode;
    @SerializedName("StatusDescription")
    @Expose
    private String statusDescription;

    public List<GetTeamDetails> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(List<GetTeamDetails> returnValue) {
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
