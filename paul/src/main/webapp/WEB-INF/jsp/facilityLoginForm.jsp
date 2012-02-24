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