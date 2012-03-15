<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Data Grabber Admin Interface</title>
    </head>
    <body>
        <h1>Data Grabber Admin Interface</h1>
        <ul>
          <li><a href="control">Control Panel</a></li>
          <li><a href="config">Configuration</a></li>
          <li><a href="sessions">Facility Sessions</a></li>
          <li><a href="atom/queue">Ingestion Queue Feed</a></li>
          <li><a href="queue/ingestible">Ingestion Queue Admin</a></li>
          <li><a href="queue/held">Hold Queue Admin</a></li>
          <li><a href="users">Known Users</a></li>
        </ul>
    </body>
</html>