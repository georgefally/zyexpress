
package net.zyexpress.site;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ZYExpressConfiguration extends Configuration {

    //region uploadDir
    @NotEmpty
    private String uploadDir;

    @JsonProperty
    public String getUploadDir() {
        return uploadDir;
    }

    @JsonProperty
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
    //endregion

    //region database
    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
    //endregion
}

