<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
  <head>
    <title>Facility Sessions</title>
  </head>
  <body>
    <h1>ACLS Facility Sessions</h1>
    <c:forEach items="${facilities}" var="facility">
      <c:if test="${!facility.dummy}">
        <c:if test="${facility.inUse}">
          <form action="sessions/${facility.currentSession.sessionUuid}"
                method="post">
            ${facility.facilityName} is in use
            - user : ${facility.currentSession.userName},
              started : ${facility.currentSession.loginTime}
            <button name="endSession" type="submit">End session</button>
          </form>
        </c:if>
        <c:if test="${! facility.inUse}">
          <form action="facilities/${facility.facilityName}"
                method="post">
            ${facility.facilityName} is idle
            <button name="startSession" type="submit">Start session</button>
          </form>
        </c:if>
      </c:if>
    </c:forEach>
  </body>
</html>