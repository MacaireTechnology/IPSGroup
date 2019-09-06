package retrofit.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccessKeyRes {

    @SerializedName("EmpID")
    @Expose
    private Integer empID;
    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("LoginPassword")
    @Expose
    private Object loginPassword;
    @SerializedName("StatusCode")
    @Expose
    private String statusCode;
    @SerializedName("StatusDescription")
    @Expose
    private String statusDescription;

    public Integer getEmpID() {
        return empID;
    }

    public void setEmpID(Integer empID) {
        this.empID = empID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Object getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(Object loginPassword) {
        this.loginPassword = loginPassword;
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
