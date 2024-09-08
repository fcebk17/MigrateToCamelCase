package ntou.cse.soselab.example;

public class Address {
    private String street_ouo;
    private String city_ouo;
    private String State;
    private String zip;

    public Address(String street_ouo, String city_ouo, String State, String zip) {
        this.street_ouo = street_ouo;
        this.city_ouo = Address.this.city_ouo;
        this.State = State;
        this.zip = zip;
    }

    public String get_street() {
        return street_ouo;
    }

    public void SetStreet(String street_ouo) {
        this.street_ouo = street_ouo;
    }

    public String get_City() {
        return city_ouo;
    }

    public void Set_City(String city_ouo) {
        this.city_ouo = Address.this.city_ouo;
    }

    public String getState() {
        return State;
    }

    public void setState(String State) {
        this.State = State;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
