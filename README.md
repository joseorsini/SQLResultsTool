SQLResultsTool
==============

This OSGi viewtool allows any dotcms user with proper permissions to run SQL statements from frontend pages.

Requirements:

1. This ViewTool can only be executed from Contents or Widgets.

2. The Content/Widget Editor User must have the "Scripting Developer" role assigned to it in order to execute this ViewTool from a Content/Widget.

3. In case the ViewTool is intended to query a different database than dotcms, this Datasource must be defined in {dotcms_home}/tomcat/conf/Catalina/localhost/ROOT.xml file (for 2.x versions of dotcms) or in {dotcms_home}/extra/tomcat-7/context.xml (for 3.x versions of dotcms). For non-standard versions of dotcms, a different connection pool must be set directly in the Application Server.

4. If the user wants to use the ViewTool for querying the dotcms database:

a) The user can create a different datasource with limited permissions over the database/schema (following the 2nd Requirement from this document).

b) If it's not possible to create a limited datasource, it can query the dotcms database with the "default" datasource as long as this variable is set to true in the dotmarketing-config.properties file

ALLOW_VELOCITY_SQL_ACCESS_TO_DOTCMS_DB

This variable should be customized through a configuration plugin (static plugin).

How to use it: 
=============

getSQLResults Tool:
```
#set($resultsList = $sqlResultsTool.getSQLResults(String datasource, String query, int startRow, int maxRow))
```

Parameters:

a) "datasource": can be set to:

- "default": The default datasource specified for accessing the dotcms database.
- "custom": A custom datasource specified for limited access to dotcms database or any other database.

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

getParameterizedSQLResults Tool:
```
#set($resultsList = $sqlResultsTool.getParameterizedSQLResults(String datasource, String query, ArrayList<Object> params, int startRow, int maxRow))
```

Parameters:

a) "datasource": can be set to:

- "default": The default datasource specified for accessing the dotcms database.
- "custom": A custom datasource specified for limited access to dotcms database or any other database.

b) "query": The SQL query you want to run from a Content/Widget in a frontend page. Restricted queries are:

- DELETE FROM.
- DROP.
- TRUNCATE.
- ALTER DATABASE.
- ALTER TABLE.
- Any query that hits the "user_" or "cms_role" tables from the dotcms database.
- All queries or operations set at database level by the DBA.

NOTE: for parameterized queries, the '?' character is required for parameterizing queries and conditions.

c) "params": It's a list of objects that will act as query's parameters. The size of this List and the amount of allowed parameters in the query must be the same.

d) "startRow": It allows results pagination. If pagination is not desired, set it to 0.

e) "maxRow": Sets a limit of results to display from the query's output.

Output
======

This ViewTool's output is an 
```
ArrayList<HashMap<String, String>> 
```
object where you can get results through a For loop.

Code:
```
<h3>Calling getSQLResults Tool from a Content:</h3>
<br>
Query: select * from identifier where asset_type = 'htmlpage' and parent_path = '/home/'
<br><br>
#set($query = "select * from identifier where asset_type = 'htmlpage' and parent_path = '/home/'")
#set($resultsList = $sqlResultsTool.getSQLResults("default", "$!{query}", 0, 4))
Results: $resultsList
<br>
<br>
#if($resultsList.size() > 0)
 #foreach ($res in $resultsList)
 <ul>
  <li>ID: $res.get("id")</li>
  <li>Parent Path: $res.get("parent_path")</li>
  <li>Asset Name: $res.get("asset_name")</li>
  <li>Asset Type: $res.get("asset_type")</li>
 </ul>
 #end
#end
<br><br>
<h3>Calling getParameterizedSQLResults Tool from a Content:</h3>
<br>
Query: "select * from identifier where asset_type = ? and parent_path = ?"
<br>
Params: 'htmlpage','/home/'
<br><br>
#set($query = "select * from identifier where asset_type = ? and parent_path = ?")
#set($paramsList = $contents.getEmptyList())
#set($temp = $paramsList.add("htmlpage"))
#set($temp = $paramsList.add("/home/"))
#set($resultsList = $sqlResultsTool.getParameterizedSQLResults("default", "$!query", $paramsList, 0, 4))
Results: $resultsList
<br>
<br>
#if($resultsList.size() > 0)
 #foreach ($res in $resultsList)
 <ul>
  <li>ID: $res.get("id")</li>
  <li>Parent Path: $res.get("parent_path")</li>
  <li>Asset Name: $res.get("asset_name")</li>
  <li>Asset Type: $res.get("asset_type")</li>
 </ul>
 #end
#end​
```
Output:
```
Calling getSQLResults Tool from a Content:


Query: select * from identifier where asset_type = 'htmlpage' and parent_path = '/home/' 

Results: [{host_inode=48190c8c-42c4-46af-8d1a-0cd5db894797, id=69bd8ab7-861c-4eba-826b-19adec1d7ec8, parent_path=/home/, asset_name=index.html, asset_type=htmlpage, oddoreven=0, syspublish_date=, rownumber=0, sysexpire_date=}, {host_inode=d234cfc0-926e-49d1-a7a8-d7033ffe793f, id=c09cdd1c-3740-409d-bc6b-dfdcb52ca070, parent_path=/home/, asset_name=index.html, asset_type=htmlpage, oddoreven=1, syspublish_date=, rownumber=1, sysexpire_date=}, {host_inode=d234cfc0-926e-49d1-a7a8-d7033ffe793f, id=baf137d7-961a-494d-bd4d-786b0a60bac8, parent_path=/home/, asset_name=locations.html, asset_type=htmlpage, oddoreven=0, syspublish_date=, rownumber=2, sysexpire_date=}, {host_inode=48190c8c-42c4-46af-8d1a-0cd5db894797, id=c9162a5c-6e6b-4e68-8bd7-4d46e8a929f4, parent_path=/home/, asset_name=mobile-confirm.html, asset_type=htmlpage, oddoreven=1, syspublish_date=, rownumber=3, sysexpire_date=}] 

ID: 69bd8ab7-861c-4eba-826b-19adec1d7ec8
Parent Path: /home/
Asset Name: index.html
Asset Type: htmlpage
ID: c09cdd1c-3740-409d-bc6b-dfdcb52ca070
Parent Path: /home/
Asset Name: index.html
Asset Type: htmlpage
ID: baf137d7-961a-494d-bd4d-786b0a60bac8
Parent Path: /home/
Asset Name: locations.html
Asset Type: htmlpage
ID: c9162a5c-6e6b-4e68-8bd7-4d46e8a929f4
Parent Path: /home/
Asset Name: mobile-confirm.html
Asset Type: htmlpage


Calling getParameterizedSQLResults Tool from a Content:


Query: "select * from identifier where asset_type = ? and parent_path = ?" 
Params: 'htmlpage','/home/' 

Results: [{host_inode=48190c8c-42c4-46af-8d1a-0cd5db894797, id=69bd8ab7-861c-4eba-826b-19adec1d7ec8, parent_path=/home/, asset_name=index.html, asset_type=htmlpage, oddoreven=0, syspublish_date=, rownumber=0, sysexpire_date=}, {host_inode=d234cfc0-926e-49d1-a7a8-d7033ffe793f, id=c09cdd1c-3740-409d-bc6b-dfdcb52ca070, parent_path=/home/, asset_name=index.html, asset_type=htmlpage, oddoreven=1, syspublish_date=, rownumber=1, sysexpire_date=}, {host_inode=d234cfc0-926e-49d1-a7a8-d7033ffe793f, id=baf137d7-961a-494d-bd4d-786b0a60bac8, parent_path=/home/, asset_name=locations.html, asset_type=htmlpage, oddoreven=0, syspublish_date=, rownumber=2, sysexpire_date=}, {host_inode=48190c8c-42c4-46af-8d1a-0cd5db894797, id=c9162a5c-6e6b-4e68-8bd7-4d46e8a929f4, parent_path=/home/, asset_name=mobile-confirm.html, asset_type=htmlpage, oddoreven=1, syspublish_date=, rownumber=3, sysexpire_date=}] 

ID: 69bd8ab7-861c-4eba-826b-19adec1d7ec8
Parent Path: /home/
Asset Name: index.html
Asset Type: htmlpage
ID: c09cdd1c-3740-409d-bc6b-dfdcb52ca070
Parent Path: /home/
Asset Name: index.html
Asset Type: htmlpage
ID: baf137d7-961a-494d-bd4d-786b0a60bac8
Parent Path: /home/
Asset Name: locations.html
Asset Type: htmlpage
ID: c9162a5c-6e6b-4e68-8bd7-4d46e8a929f4
Parent Path: /home/
Asset Name: mobile-confirm.html
Asset Type: htmlpage
```
