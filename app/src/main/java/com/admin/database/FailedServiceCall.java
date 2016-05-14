package com.admin.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Admin on 14-05-2016.
 */
@DatabaseTable(tableName = "FAILED_SERVICE_CALL")
public class FailedServiceCall implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String serviceId;

    @DatabaseField
    private String parametersJsonString;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getParametersJsonString() {
        return parametersJsonString;
    }

    public void setParametersJsonString(String parametersJsonString) {
        this.parametersJsonString = parametersJsonString;
    }
}
