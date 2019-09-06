package db;

public class ClockInOutTime {

    private String id;
    private String emp_id;
    private String clock_type;
    private String clock_in_id;
    private String img_uri;
    private String date_time;
    private String latitude;
    private String longitude;
    private String currentLocation;

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id=id;
    }

    public String getEmp_id(){
        return emp_id;
    }
    public void setEmp_id(String emp_id){
        this.emp_id=emp_id;
    }

    public String getClock_type(){
        return clock_type;
    }
    public void setClock_type(String clock_type){
        this.clock_type=clock_type;
    }

    public String getClock_in_id(){
        return clock_in_id;
    }
    public void setClock_in_id(String clock_in_id){
        this.clock_in_id=clock_in_id;
    }

    public String getDate_time(){
        return date_time;
    }
    public void setDate_time(String date_time){
        this.date_time=date_time;
    }

    public String getImg_uri(){
        return img_uri;
    }
    public void setImg_uri(String img_uri){
        this.img_uri=img_uri;
    }

    public String getLatitude(){
        return latitude;
    }
    public void setLatitude(String latitude){
        this.latitude=latitude;
    }

    public String getLongitude(){
        return longitude;
    }
    public void setLongitude(String longitude){
        this.longitude=longitude;
    }

    public String getCurrentLocation(){
        return currentLocation;
    }
    public void setCurrentLocation(String currentLocation){
        this.currentLocation=currentLocation;
    }

}
