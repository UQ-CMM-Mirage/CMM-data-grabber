<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
    <head>
        <title>Data Grabber - Facility Login</title>
    </head>
    <body>
        <h1>ACLS Facility Login for "${facilityName}"</h1>
        
        <p>
        This page can be used to "login" to a facility (e.g. a microscope) when
        the ACLS login program on the application is not working properly.  If
        you are not logged in to the facility when you save files, you will 
        need to manually "claim" them so that they can be transferred to Mirage.
        </p>
        <p>
        Enter your normal ACLS username and password.  (You will be prompted for
        your ACLS account name if you have multiple ACLS accounts.)
        </p>
        
        ${message}
        
        <form name="form" method="POST" method="post"
              action="/paul/facilities/${facilityName}">
          <c:if test="${empty accounts}">
              User name: <input type="text" name="userName" value="${param.userName}">
              <br>
              Password: <input type="password" name="password" value="${param.userName}">
          </c:if>
          <c:if test="${! empty accounts}">
              User name:  <input type="text" name="userName" 
              					 value="${param.userName}" readonly>
              <br>
              Password: <input type="password" name="password" 
                               value="${param.userName}" readonly>
              <br>
              <select name="account">
                  <c:forEach items="${accounts}" var="account">
                      <option value="${account}">${account}</option>
                  </c:forEach>
              </select>
          </c:if>
          <br>
          <button type="submit" name="startSession">OK</button>
          <button type="button" onclick="window.location = '/paul/sessions'">Cancel</button>
        </form>
    </body>
</html>