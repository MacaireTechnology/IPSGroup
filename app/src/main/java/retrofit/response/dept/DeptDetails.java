package retrofit.response.dept;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeptDetails {

    @SerializedName("DeprtID")
    @Expose
    private String deprtID;
    @SerializedName("DeprtCode")
    @Expose
    private String deprtCode;
    @SerializedName("Department")
    @Expose
    private String department;

    public String getDeprtID() {
        return deprtID;
    }

    public void setDeprtID(String deprtID) {
        this.deprtID = deprtID;
    }

    public String getDeprtCode() {
        return deprtCode;
    }

    public void setDeprtCode(String deprtCode) {
        this.deprtCode = deprtCode;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    
}
