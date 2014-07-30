SQLResultsTool
==============

This OSGi viewtool allows any dotcms user with proper permissions to run SQL statements from frontend pages.

Requirements:

1. This ViewTool can only be executed from Contents or Widgets.

2. The Content/Widget Editor User must have the "Scripting Developer" role assigned to it in order to execute this ViewTool from a Content/Widget.

3. In case the ViewTool is intended to query a different database than dotcms, this Datasource must be defined in {dotcms_hom}/tomcat/conf/Catalina/localhost/ROOT.xml file (for 2.x versions of dotcms) or in {dotcms_home}/extra/tomcat-7/context.xml (for 3.x versions of dotcms). For non-standard versions of dotcms, a different connection pool must be set directly in the Application Server.

4. If the user wants to use the ViewTool for querying the dotcms database:

a) The user can create a different datasource with limited permissions over the database/schema (following the 2nd Requirement from this document).

b) If it's not possible to create a limited datasource, it can query the dotcms database with the "default" datasource as long as this variable is set to true in the dotmarketing-config.properties file

ALLOW_VELOCITY_SQL_ACCESS_TO_DOTCMS_DB

This variable should be customized through a configuration plugin (static plugin).

How to use it: 
=============
ViewTool Call:
```
#set($resultsList = $sqlResultsTool.getSQLResults(String datasource, String query, int startRow, int maxRow))
```

Parameters:

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

Output
======

This ViewTool's output is a ArrayList<HashMap<String, String>> where you can get results on a For loop.

Code:
```
<h3>Calling ViewTool from a Content:</h3>
<br>
Query: "select title, identifier, inode, mod_date from contentlet where identifier = 'd05ef74e-5903-4c43-bef6-77749f271ff8' order by mod_date desc"
<br><br>
#set($resultsList = $sqlResultsTool.getSQLResults("default", "select title, identifier, inode, mod_date from contentlet where identifier = 'd05ef74e-5903-4c43-bef6-77749f271ff8' order by mod_date desc", 0, 4))
Results: $resultsList
<br>
<br>
#if($resultsList.size() > 0)
 #foreach ($res in $resultsList)
 <ul>
  <li>Title: $res.get("title")</li>
  <li>Identifier: $res.get("identifier")</li>
  <li>Inode: $res.get("inode")</li>
  <li>Mod Date: $res.get("mod_date")</li>
 </ul>
 #end
#end
```
Output:
```
Calling ViewTool from a Content:


Query: "select title, identifier, inode, mod_date from contentlet where identifier = 'd05ef74e-5903-4c43-bef6-77749f271ff8' order by mod_date desc" 

Results: [{title=Testing new viewtool, mod_date=2014-07-30 18:26:49.072, oddoreven=0, rownumber=0, inode=bb11b6cf-d8a6-467f-ab94-6d8becaca1c4, identifier=d05ef74e-5903-4c43-bef6-77749f271ff8}, {title=Testing new viewtool, mod_date=2014-07-30 18:26:33.527, oddoreven=1, rownumber=1, inode=97bccd7a-9014-40a7-a838-d134bfe20aa0, identifier=d05ef74e-5903-4c43-bef6-77749f271ff8}, {title=Testing new viewtool, mod_date=2014-07-30 18:26:22.952, oddoreven=0, rownumber=2, inode=51b3eab5-bf2d-40dc-92dd-1559a8246be5, identifier=d05ef74e-5903-4c43-bef6-77749f271ff8}, {title=Testing new viewtool, mod_date=2014-07-30 18:24:48.142, oddoreven=1, rownumber=3, inode=345690ab-ea5f-4042-a37b-695bc5389f74, identifier=d05ef74e-5903-4c43-bef6-77749f271ff8}] 

Title: Testing new viewtool
Identifier: d05ef74e-5903-4c43-bef6-77749f271ff8
Inode: bb11b6cf-d8a6-467f-ab94-6d8becaca1c4
Mod Date: 2014-07-30 18:26:49.072

Title: Testing new viewtool
Identifier: d05ef74e-5903-4c43-bef6-77749f271ff8
Inode: 97bccd7a-9014-40a7-a838-d134bfe20aa0
Mod Date: 2014-07-30 18:26:33.527

Title: Testing new viewtool
Identifier: d05ef74e-5903-4c43-bef6-77749f271ff8
Inode: 51b3eab5-bf2d-40dc-92dd-1559a8246be5
Mod Date: 2014-07-30 18:26:22.952

Title: Testing new viewtool
Identifier: d05ef74e-5903-4c43-bef6-77749f271ff8
Inode: 345690ab-ea5f-4042-a37b-695bc5389f74
Mod Date: 2014-07-30 18:24:48.142
```

