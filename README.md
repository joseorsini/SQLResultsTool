SQLResultsTool
==============

This OSGi viewtool allows any dotcms user with proper permissions to run SQL statements from frontend pages.

Requirements:

1. This ViewTool can only be executed from Contents or Widgets.

2. The Content/Widget Editor User must have the "Scripting Developer" role assigned to it in order to execute this ViewTool from a Content/Widget.

3. In case the ViewTool is intended to query a different database than dotcms, this Datasource must be defined in {dotcms_hom}/tomcat/conf/Catalina/localhost/ROOT.xml file (for 2.x versions of dotcms) or in {dotcms_home}/extra/tomcat-7/context.xml (for 3.x versions of dotcms). For non-standard versions of dotcms, a different connection pool must be set directly in the Application Server.

4. If the user wants to use the ViewTool for querying the dotcms database:

a) It can create a different datasource with limited permissions over the database/schema (following the 2nd Requirement from this document).

b) If it's not possible to create a limited datasource, it can query the dotcms database with the "default" datasource as long as this variable is set to true in the dotmarketing-config.properties file

ALLOW_VELOCITY_SQL_ACCESS_TO_DOTCMS_DB

This variable should be customized through a configuration plugin (static plugin).

How to use it: 
=============

#set($resultsList = $sqlResultsTool.getSQLResults(String datasource, String query, int startRow, int maxRow))

a) "datasource": can be set to:

- "default": The default datasource specified for accessing the dotcms database.
- "custom": A custom datasource specified for limited access to dotcmcs database or any other database.

b) "query": The SQL query you want to run from a Content/Widget in a frontend page. Restricted queries are:

- DELETE FROM.
- DROP.
- TRUNCATE.
- ALTER DATABASE.
- ALTER TABLE.
- Any query that hits the "user_" or "cms_role" tables from the dotcms database.
- All queries or operations set at database level by the DBA.

c) "startRow": It allows results pagination. If pagination is not desired, set it to 0.

d) "maxRow": Sets a limit of results to display from the query's output.

