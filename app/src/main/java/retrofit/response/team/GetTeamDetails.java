package retrofit.response.team;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetTeamDetails {

    @SerializedName("gpsdt")
    @Expose
    private String gpsdt;
    @SerializedName("GpsAddress")
    @Expose
    private String gpsAddress;

    public String getGpsdt() {
        return gpsdt;
    }

    public void setGpsdt(String gpsdt) {
        this.gpsdt = gpsdt;
    }

    public String getGpsAddress() {
        return gpsAddress;
    }

    public void setGpsAddress(String gpsAddress) {
        this.gpsAddress = gpsAddress;
    }
}
