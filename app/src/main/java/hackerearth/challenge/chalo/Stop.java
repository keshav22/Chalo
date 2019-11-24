package hackerearth.challenge.chalo;

public class Stop {

    private String stopId;
    private String stopName;
    private int sequence;
    private double latitute;
    private double longitude;

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setLatitute(double latitute) {
        this.latitute = latitute;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStopId() {
        return stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public int getSequence() {
        return sequence;
    }

    public double getLatitute() {
        return latitute;
    }

    public double getLongitude() {
        return longitude;
    }
}
