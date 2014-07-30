package com.dotmarketing.osgi.viewtools;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapterImpl;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.Role;
import com.dotmarketing.business.UserAPI;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.exception.DotDataException;

import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;

public class SQLResultsViewTool implements ViewTool {

	private static final UserAPI userAPI = APILocator.getUserAPI();
	Context ctx;
	private InternalContextAdapterImpl ica;

	public void init(Object obj) {

		ViewContext context = (ViewContext) obj;
		ctx = context.getVelocityContext();
	
	}

	public ArrayList<HashMap<String, String>> getSQLResults(String dataSource, String sql, int startRow, int maxRow) {
		   
		ArrayList<HashMap<String, String>> errorResults = new ArrayList<HashMap<String, String>>();
		   
		if(!canUserEvaluate()){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("hasDotConnectSQLError", "true");
			map.put("dotConnectSQLError", "External SQL Scripting is disabled in your dotcms instance.");
			errorResults.add(map);
			return errorResults;
		} else{
		
			try{
				   
				ica = new InternalContextAdapterImpl(ctx);
				String fieldResourceName = ica.getCurrentTemplateName();
				String conInode = fieldResourceName.substring(fieldResourceName.indexOf("/") + 1, fieldResourceName.indexOf("_"));

				Contentlet con = APILocator.getContentletAPI().find(conInode, APILocator.getUserAPI().getSystemUser(), true);

				if (!UtilMethods.isSet(sql)) {
			        	return new ArrayList<HashMap<String, String>>();
			        }
			        if (sql.toLowerCase().indexOf("user_") > -1) {
			        	Logger.error(this,"getSQLResults macro is trying to query the user_ table");
					Logger.debug(this,"Check content with id: " + con.getIdentifier());
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("hasDotConnectSQLError", "true");
					map.put("dotConnectSQLError", "getSQLResults macro is trying to query the user_ table");
					errorResults.add(map);
					return new ArrayList<HashMap<String, String>>();
			        }
			        if (sql.toLowerCase().indexOf("cms_role") > -1) {
			            	Logger.error(this,"getSQLResults macro is trying to query the cms_role table");
					Logger.debug(this,"Check content with id: " + con.getIdentifier());
			            	HashMap<String, String> map = new HashMap<String, String>();
			            	map.put("hasDotConnectSQLError", "true");
			            	map.put("dotConnectSQLError", "getSQLResults macro is trying to query the cms_role table");
			            	errorResults.add(map);
			            	return new ArrayList<HashMap<String, String>>();
			        }
			        if (sql.toLowerCase().indexOf("delete from") > -1 || sql.toLowerCase().indexOf("drop") > -1
			        	|| sql.toLowerCase().indexOf("truncate") > -1 || sql.toLowerCase().indexOf("alter table") > -1
		                        || sql.toLowerCase().indexOf("alter database") > -1 || sql.toLowerCase().startsWith("update")) {
			           	Logger.error(this,"getSQLResults macro is trying to run a forbidden query");
					Logger.debug(this,"Check content with id: " + con.getIdentifier());
				        HashMap<String, String> map = new HashMap<String, String>();
			           	map.put("hasDotConnectSQLError", "true");
			            	map.put("dotConnectSQLError", "getSQLResults macro is trying to run a forbidden query");
			           	errorResults.add(map);
			            	return new ArrayList<HashMap<String, String>>();
			        }
			        
			        try {
			        	DotConnect dc = new DotConnect();

					dc.setSQL(sql);
		                    	if(!UtilMethods.isSet(startRow)){
		                        	startRow = 0;
	        	            	}else{
	    					if (startRow > 0) {
	    		        			dc.setStartRow(startRow);
	    		            		}
					}

		                    	if(!UtilMethods.isSet(maxRow)){
		                        	maxRow = 0;
					}else{
	    			        	if (maxRow > 0) {
	    		        	        	dc.setMaxRows(maxRow);
						}
					}

					if (dataSource.equals("default")) {
					    	if(!Config.getBooleanProperty("ALLOW_VELOCITY_SQL_ACCESS_TO_DOTCMS_DB", false)){
					        	Logger.error(this,"getSQLResults macro is trying to execute queries using the default connection pool.");
			        		    	Logger.debug(this,"ALLOW_VELOCITY_SQL_ACCESS_TO_DOTCMS_DB is set to false");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("hasDotConnectSQLError", "true");
							map.put("dotConnectSQLError", "getSQLResults macro is trying to execute queries using the default connection pool. ALLOW_VELOCITY_SQL_ACCESS_TO_DOTCMS_DB is set to false");
							errorResults.add(map);
							return new ArrayList<HashMap<String, String>>();
			        	    	}
			            		else{
			            			return dc.getResults();
						}
				      	} else {
						return dc.getResults(dataSource);
				        }
				} catch (Exception e) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("hasDotConnectSQLError", "true");
					map.put("dotConnectSQLError", "There was a sql error:" + e.getMessage());
					errorResults.add(map);
					return errorResults;
			        }   
			}
			catch(Exception e){
				Logger.error(this,"There was a problem retrieving the content where ViewTool was called");
		        	HashMap<String, String> map = new HashMap<String, String>();
		           	map.put("hasDotConnectSQLError", "true");
			        map.put("dotConnectSQLError", "There was a sql error:" + e.getMessage());
		        	errorResults.add(map);
		           	return errorResults;                
			}
		}   
	}

	protected boolean canUserEvaluate(){
		try{
                	ica = new InternalContextAdapterImpl(ctx);
	                String fieldResourceName = ica.getCurrentTemplateName();
        	        String conInode = fieldResourceName.substring(fieldResourceName.indexOf("/") + 1, fieldResourceName.indexOf("_"));

                	Contentlet con = APILocator.getContentletAPI().find(conInode, APILocator.getUserAPI().getSystemUser(), true);

	                User mu = userAPI.loadUserById(con.getModUser(), APILocator.getUserAPI().getSystemUser(), true);
        	        Role scripting =APILocator.getRoleAPI().loadRoleByKey("Scripting Developer");
                	return APILocator.getRoleAPI().doesUserHaveRole(mu, scripting);
		}	
		catch(Exception e){
			Logger.warn(this.getClass(), "Scripting called with error" + e);
			return false;	
		}
		return false;
	}
}
