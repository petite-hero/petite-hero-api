package capstone.petitehero.dtos.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "config")
public class LicenseDTO implements Serializable {

    private Integer outer_radius;
    private Integer report_delay;
    private String safezone_cron_time;
    private String task_cron_time;
    private Integer total_hour_task_education;
    private Integer total_hour_task_skills;
    private Integer total_hour_task_housework;
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

    @XmlElement
    public String getTask_cron_time() {
        return task_cron_time;
    }

    public void setTask_cron_time(String task_cron_time) {
        this.task_cron_time = task_cron_time;
    }

    @XmlElement
    public Integer getTotal_hour_task_education() {
        return total_hour_task_education;
    }

    public void setTotal_hour_task_education(Integer total_hour_task_education) {
        this.total_hour_task_education = total_hour_task_education;
    }

    @XmlElement
    public Integer getTotal_hour_task_skills() {
        return total_hour_task_skills;
    }

    public void setTotal_hour_task_skills(Integer total_hour_task_skills) {
        this.total_hour_task_skills = total_hour_task_skills;
    }

    @XmlElement
    public Integer getTotal_hour_task_housework() {
        return total_hour_task_housework;
    }

    public void setTotal_hour_task_housework(Integer total_hour_task_housework) {
        this.total_hour_task_housework = total_hour_task_housework;
    }
}
