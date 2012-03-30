<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <h1>Data Grabber Access Control</h1>
    </head>
    <body>
        <h1>Data Grabber Login</h1>
        <p>
        Login to the Data Grabber to claim files, view the
        queues and perform other tasks.  Use your normal ACLS user name and password,
        or administrator credentials.
        </p>
        
        ${message}
        
        <form name="form" method="POST" method="post"
              action='<%= response.encodeURL("j_security_check") %>'>
          <c:if test="${empty accounts}">
              User name: <input type="text" name="j_username" value="${param.userName}">
              <br>
              Password: <input type="password" name="j_password" value="${param.userName}">
          </c:if>
          <br>
          <button type="submit" name="startSession">Login</button>
          <button type="button" onclick="window.location = '/paul/sessions'">Cancel</button>
        </form>
    </body>
</html>