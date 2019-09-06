package retrofit.response.leaveType;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LeaveTypeDetails {

    @SerializedName("LeaveID")
    @Expose
    private String leaveID;
    @SerializedName("LeaveCode")
    @Expose
    private String leaveCode;
    @SerializedName("Description")
    @Expose
    private String description;

    public String getLeaveID() {
        return leaveID;
    }

    public void setLeaveID(String leaveID) {
        this.leaveID = leaveID;
    }

    public String getLeaveCode() {
        return leaveCode;
    }

    public void setLeaveCode(String leaveCode) {
        this.leaveCode = leaveCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
