<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Facility Login</title>
    </head>
    <body>
        <h1>Facility Login for the "${facilityName}" facility</h1>
        
        ${message}
        
        <form name="form" method="POST" method="post"
              action="/paul/facilities/${facilityName}">
          <input type="text" name="userName" value="${param.userName}">
          <input type="password" name="password" value="">
          <button type="submit" name="startSession">Log in</button>
        </form>
    </body>
</html>