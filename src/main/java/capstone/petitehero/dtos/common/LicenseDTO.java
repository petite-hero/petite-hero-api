package capstone.petitehero.dtos.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "config")
public class LicenseDTO implements Serializable {

    private Integer outer_radius;
    private Integer report_delay;
    private String safezone_cron_time;
    private String license_EN;
    private String license_VN;

    @XmlElement
    public Integer getOuter_radius() {
        return outer_radius;
    }

    public void setOuter_radius(Integer outer_radius) {
        this.outer_radius = outer_radius;
    }

    @XmlElement
    public Integer getReport_delay() {
        return report_delay;
    }

    public void setReport_delay(Integer report_delay) {
        this.report_delay = report_delay;
    }

    @XmlElement
    public String getSafezone_cron_time() {
        return safezone_cron_time;
    }

    public void setSafezone_cron_time(String safezone_cron_time) {
        this.safezone_cron_time = safezone_cron_time;
    }

    @XmlElement
    public String getLicense_EN() {
        return license_EN;
    }

    public void setLicense_EN(String license_EN) {
        this.license_EN = license_EN;
    }

    @XmlElement
    public String getLicense_VN() {
        return license_VN;
    }

    public void setLicense_VN(String license_VN) {
        this.license_VN = license_VN;
    }
}
