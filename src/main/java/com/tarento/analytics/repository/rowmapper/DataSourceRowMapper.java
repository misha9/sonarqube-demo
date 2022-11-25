package com.tarento.analytics.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.tarento.analytics.model.DataSource;

@Component
public class DataSourceRowMapper implements RowMapper<DataSource> {
    @Override
    public DataSource mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final DataSource dataSources = new DataSource();
        dataSources.setId(rs.getLong("id"));
        dataSources.setName(rs.getString("name"));
        dataSources.setDescription(rs.getString("description"));
        dataSources.setUrl(rs.getString("url"));
        dataSources.setHost(rs.getString("host"));
        dataSources.setUsername(rs.getString("username"));
        dataSources.setPassword(rs.getString("password"));
        dataSources.setCreatedBy(rs.getLong("created_by"));
        dataSources.setCreatedDate(rs.getLong("created_date")); 
        dataSources.setUpdatedBy(rs.getLong("updated_by"));
        dataSources.setUpdatedDate(rs.getLong("updated_date")); 
        return dataSources;
    }

}
