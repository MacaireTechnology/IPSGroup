package retrofit;

import com.google.gson.JsonObject;
import retrofit.response.AccessKeyRes;
import retrofit.response.ApplyLeaveRes;
import retrofit.response.ClockinRes;
import retrofit.response.ImageRes;
import retrofit.response.LeaveReqRes;
import retrofit.response.LoginRes;
import retrofit.response.TrackRes;
import retrofit.response.adjuest.AdjustList;
import retrofit.response.admin.IssueTrackerRes;
import retrofit.response.approval_attend.ApprovalAttendRes;
import retrofit.response.contact.ContactRes;
import retrofit.response.cutomerdata.CustomDataRes;
import retrofit.response.dept.DeptRes;
import retrofit.response.issueApproval.IssueApprovalListRes;
import retrofit.response.leaveType.LeaveTypeRes;
import retrofit.response.leave_approval.LeaveApprovalRes;
import retrofit.response.queryType.QueryTypeRes;
import retrofit.response.status_type.StatusTypeRes;
import retrofit.response.subForm.SubFormRes;
import retrofit.response.team.GetTeamRes;
import retrofit.response.team_attend.TeamAttendRes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("CheckMobileNumber/chkMobileNumber")
    Call<AccessKeyRes> doLoginOtp(@Query("MobNum") String mobNo);

    @GET("Account/CheckUserLogin")
    Call<LoginRes> doLogin(@Query("UserName") String username, @Query("Password") String password);

    @POST("Account/ClockInOut")
    Call<ImageRes> douploadimg(@Body JsonObject body);

    @GET("Account/GetClockInOutData")
    Call<ClockinRes> dogetattdetails(@Query("EmpID") String empid, @Query("AttDT") String att_dt);

    @POST("LeaveRequest/SaveLeaveApply")
    Call<ApplyLeaveRes> doapplyleave(@Body JsonObject body);

    @GET("LeaveRequest/GetLeaveRequestStatus")
    Call<LeaveReqRes> dorequestleavestatus(@Query("EmployeeID") String empid);

    @GET("MyAccount/ReadTeam")
    Call<TrackRes> dotrackteam(@Query("RptManagerEmpID") String emp_id);

    @GET("MyAccount/GetTeamAttendance")
    Call<TeamAttendRes> doTeamAttend(@Query("RptManagerEmpID") String emp_id);

    @GET("AdjustAttendance/GetAdjustAttendance")
    Call<ApprovalAttendRes> doAdjustAttend(@Query("EmployeeID") String emp_id);

    @POST("AdjustAttendance/SaveAdjustAttendanceApproval")
    Call<ApplyLeaveRes> doUpdateLeave(@Body JsonObject body);

    @GET("LeaveRequest/GetLeaveRequestData")
    Call<LeaveApprovalRes> doLeaveRequest(@Query("EmployeeID") String emp_id);

    @POST("LeaveRequest/SaveLeaveApplyApproval")
    Call<ApplyLeaveRes> doLeaveReq(@Body JsonObject body);

    @GET("BindCombo/GetLeaveType")
    Call<LeaveTypeRes> doLeaveType();

    @GET("BindCombo/GetStatus")
    Call<StatusTypeRes> doStatusType(@Query("StatusType") String StatusType);

    @POST("Customers/SaveCustomers")
    Call<ApplyLeaveRes> doSaveCustomers(@Body JsonObject body);

    @GET("Customers/GetCustomersData")
    Call<CustomDataRes> doCustomData();

    @GET("SubmissionForm/GetSubmissionFormData")
    Call<SubFormRes> doSubForm(@Query("EmployeeID") String EmployeeID);

    @POST("SubmissionForm/SaveSubmissionForm")
    Call<ApplyLeaveRes> doSaveSubForm(@Body JsonObject body);

    @POST("Dsr/SaveDsr")
    Call<ApplyLeaveRes> doSaveDSR(@Body JsonObject body);

    @POST("Account/SaveGpsAdress")
    Call<ApplyLeaveRes> doSaveGPSAddr(@Body JsonObject body);

    @POST("AdjustAttendance/SaveAdjustAttendance")
    Call<ApplyLeaveRes> doSaveAdjustAttendance(@Body JsonObject body);

    @GET("AdjustAttendance/GetAdjustAttendanceStatus")
    Call<AdjustList> doAdjustAtt(@Query("EmployeeID") String EmployeeID);

    @GET("MyAccount/ReadContacts")
    Call<ContactRes> doContact();

    @POST("IssueTracker/SaveIssueTracker")
    Call<ApplyLeaveRes> doIssueTracker(@Body JsonObject body);

    @GET("IssueTracker/GetIssueTrackerStatus")
    Call<IssueTrackerRes> doListIssueTracker(@Query("IssueCreationID") String EmployeeID);

    @GET("BindCombo/GetDepartment")
    Call<DeptRes> doGetDepartment();

    @GET("BindCombo/GetQueryType")
    Call<QueryTypeRes> doQueryType();

    @GET("IssueTracker/GetIssueTrackertData")
    Call<IssueApprovalListRes> doGetIssueList(@Query("EmployeeID") String EmployeeID);

    @POST("IssueTracker/SaveIssueTrackerApproval")
    Call<ApplyLeaveRes> doSaveIssueTrack(@Body JsonObject body);

    @GET("MyAccount/GetTeamDatas")
    Call<GetTeamRes> doGetTeamData(@Query("EmpID") String EmpID);

    @GET("MyAccount/ReadMyAccount")
    Call<ContactRes> doProfile(@Query("EmpID") String EmpID);

    @GET("submitsms.jsp")
    Call<ContactRes> doSendOTP(@Query("user") String user,
                               @Query("key") String key,
                               @Query("mobile") String mobile,
                               @Query("message") String message,
                               @Query("senderid") String senderid,
                               @Query("accusage") String accusage);
}
