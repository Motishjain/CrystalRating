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
    private String serviceName;

    @DatabaseField
    private String parametersJsonString;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getParametersJsonString() {
        return parametersJsonString;
    }

    public void setParametersJsonString(String parametersJsonString) {
        this.parametersJsonString = parametersJsonString;
    }
}
