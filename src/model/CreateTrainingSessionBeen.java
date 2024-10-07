package model;

public class CreateTrainingSessionBeen {

   
    public String getSess_id() {
        return sess_id;
    }

    public void setSess_id(String sess_id) {
        this.sess_id = sess_id;
    }

   
    public String getTrainer_id() {
        return trainer_id;
    }

   
    public void setTrainer_id(String trainer_id) {
        this.trainer_id = trainer_id;
    }

   
    public String getDate() {
        return date;
    }

   
    public void setDate(String date) {
        this.date = date;
    }

   
    public String getTime() {
        return time;
    }

    
    public void setTime(String time) {
        this.time = time;
    }

   
    public String getDuration() {
        return duration;
    }

    
    public void setDuration(String duration) {
        this.duration = duration;
    }

    
    public String getFee() {
        return fee;
    }

    
    public void setFee(String fee) {
        this.fee = fee;
    }

    
    public String getType() {
        return type;
    }

   
    public void setType(String type) {
        this.type = type;
    }

   
    public String getSpec() {
        return spec;
    }

    
    public void setSpec(String spec) {
        this.spec = spec;
    }

    private String sess_id;
    private String trainer_id;
    private String date;
    private String time;
    private String duration;
    private String fee;
    private String type;
    private String spec;
    
}
