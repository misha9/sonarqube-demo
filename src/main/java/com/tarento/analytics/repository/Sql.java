package com.tarento.analytics.repository;

/**
 * This interface will hold all the SQL Quries which are being used by the
 * application Internally, the inner interface will have the queries separated
 * based on the functionalities that they are associated with
 * 
 * @author Darshan Nagesh
 *
 */
public interface Sql {

	final String ID = "id";

	/**
	 * All the queries associated with the Common activities or transactions will be
	 * placed here
	 * 
	 * @author Darshan Nagesh
	 *
	 */

	public interface ConfigQueries {
		final String GET_DASHBOARDS_FOR_PROFILE = "select dash.id, dash.dashboard_id, dash.title from dashboards dash LEFT JOIN role_dashboards rd ON dash.dashboard_id = rd.dashboard_id "
				+ " LEFT JOIN role r ON r.id = rd.role_id WHERE dash.profile = ? and r.role_name = ? ";
		final String GET_DASHBOARD_CONFIG = "select * from dashboards where dashboard_id = ? and profile = ?"; 
		final String GET_VISUALIZATION_BY_CODE = "select * from visualizations where visualization_code = ?";
		final String SAVE_VISUALIZATIONS = "INSERT INTO visualizations (visualization_code, chart_name, chart_type, allow_zero, value_type, is_decimal, drill_chart, document_type, actions, limits, filters, comments) "  + 
				" VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ";
		final String UPDATE_QUERIES = "UPDATE visualizations set queries = ? "  + 
				" WHERE visualization_code = ?  ";
		final String UPDATE_AGGREGATION_PATH= "UPDATE visualizations set aggregation_paths = ? "  + 
				" WHERE visualization_code = ?  ";
		final String SAVE_DASHBOARDS = "INSERT INTO dashboards (dashboard_id,title,description,is_active,style,widget_title,show_filters,show_widgets,show_widget_title,filters,widget_charts,visualizations,profile)" + 
			    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		final String UPDATE_FILTERS = "UPDATE dashboards set filters = ? " + 
			    "WHERE dashboard_id = ? ";
		final String UPDATE_WIDGET_CHARTS = "UPDATE dashboards set widget_charts = ? " + 
			    "WHERE dashboard_id = ? ";
		final String UPDATE_VISUALIZATIONS = "UPDATE dashboards set visualizations = ? " + 
			    "WHERE dashboard_id = ? ";
		final String SAVE_ROLE_DASHBOARDS = "INSERT INTO role_dashboards (role_id, dashboard_id, org_id) VALUES (?,?,?)";

	}


}
