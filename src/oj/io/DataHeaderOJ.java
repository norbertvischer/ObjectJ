/*
 * DataHeaderOJ.java
 * -- documented

 * setters and getters for  metadata about the project, such as version
 */

package oj.io;

import java.util.Date;

public class DataHeaderOJ {

    private String name;
    private String version;
    private Date updated;
    private String filename;
    private String description;

    public DataHeaderOJ() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public Date getUpdated() {
        return updated;
    }

    public String getFilename() {
        return filename;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}