package com.tarento.analytics.repository;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tarento.analytics.dto.DataSourceRequest;

@Component
public class QueryBuilder {

    private static final Logger logger = LoggerFactory.getLogger(QueryBuilder.class);

    private static final String DATASOURCE_BASE_QUERY = "SELECT id, name, description, database, url, port, username, password, host from datasources";

    @SuppressWarnings("rawtypes")
    public String getQuery(final DataSourceRequest dataSourceRequest, final Map<String, Object> preparedStatementValues) {
        final StringBuilder selectQuery = new StringBuilder(DATASOURCE_BASE_QUERY);

        addWhereClause(selectQuery, preparedStatementValues, dataSourceRequest);
        addOrderByClause(selectQuery, dataSourceRequest);

        logger.info("Query : " + selectQuery);
        return selectQuery.toString();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addWhereClause(final StringBuilder selectQuery, final Map<String, Object> preparedStatementValues,
            final DataSourceRequest dataSourceRequest) {
        if (dataSourceRequest.getId() == null)
            return;
        selectQuery.append(" WHERE ");
        boolean isAppendAndClause = false;
        if (dataSourceRequest.getId() != null) {
            isAppendAndClause = true;
            selectQuery.append(" id = ?");
            preparedStatementValues.put("id", dataSourceRequest.getId());
        }
    }

    private void addOrderByClause(final StringBuilder selectQuery,
            final DataSourceRequest dataSourceRequest) {
        final String sortBy = dataSourceRequest.getSortBy() == null ? "id"
                : dataSourceRequest.getSortBy();
        final String sortOrder = dataSourceRequest.getSortOrder() == null ? "ASC"
                : dataSourceRequest.getSortOrder();
        selectQuery.append(" ORDER BY " + sortBy + " " + sortOrder);
    }

    /**
     * This method is always called at the beginning of the method so that and is prepended before the field's predicate is
     * handled.
     *
     * @param appendAndClauseFlag
     * @param queryString
     * @return boolean indicates if the next predicate should append an "AND"
     */
    private boolean addAndClauseIfRequired(final boolean appendAndClauseFlag, final StringBuilder queryString) {
        if (appendAndClauseFlag)
            queryString.append(" AND");

        return true;
    }

}