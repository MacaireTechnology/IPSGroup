package retrofit.response.subForm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubFormDetails {

    @SerializedName("SubmissionDate")
    @Expose
    private String submissionDate;
    @SerializedName("Submission")
    @Expose
    private String submission;
    @SerializedName("CVShort")
    @Expose
    private String cVShort;
    @SerializedName("InterviewSchedule")
    @Expose
    private String interviewSchedule;
    @SerializedName("SelectedCandidate")
    @Expose
    private String selectedCandidate;
    @SerializedName("OfferedCandidates")
    @Expose
    private String offeredCandidates;
    @SerializedName("JoinCandidate")
    @Expose
    private String joinCandidate;

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getSubmission() {
        return submission;
    }

    public void setSubmission(String submission) {
        this.submission = submission;
    }

    public String getCVShort() {
        return cVShort;
    }

    public void setCVShort(String cVShort) {
        this.cVShort = cVShort;
    }

    public String getInterviewSchedule() {
        return interviewSchedule;
    }

    public void setInterviewSchedule(String interviewSchedule) {
        this.interviewSchedule = interviewSchedule;
    }

    public String getSelectedCandidate() {
        return selectedCandidate;
    }

    public void setSelectedCandidate(String selectedCandidate) {
        this.selectedCandidate = selectedCandidate;
    }

    public String getOfferedCandidates() {
        return offeredCandidates;
    }

    public void setOfferedCandidates(String offeredCandidates) {
        this.offeredCandidates = offeredCandidates;
    }

    public String getJoinCandidate() {
        return joinCandidate;
    }

    public void setJoinCandidate(String joinCandidate) {
        this.joinCandidate = joinCandidate;
    }

}
