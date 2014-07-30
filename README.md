SQLResultsTool
==============

This OSGi viewtool allows any dotcms user with proper permissions to run SQL statements from frontend pages.

Requirements:

1. The User must have the "Scripting Developer" role assigned to it in order to execute this ViewTool from a Content/Widget.
2. In case the ViewTool is intended to query a different database than dotcms, this Datasource must be defined in {dotcms_home}/tomcat/conf/Catalina/localhost/ROOT.xml file (for 2.x versions of dotcms) or in {dotcms_home}/extra/tomcat-7/context.xml (for 3.x versions of dotcms). For non-standard versions of dotcms, a different connection pool must be set directly in the Application Server.
