<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head>
<%@ include file="/WEB-INF/jsp/commonHead.jspFrag" %>
        <title>Data Grabber - Facility Login</title>
    </head>
    <body>
<%@ include file="/WEB-INF/jsp/commonHeader.jspFrag" %>
	<div class="container">
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
        
        <form name="form" method="post" action="/paul/facilityLogin">
          <c:if test="${empty accounts}">
              User name: <input type="text" name="userName" value="${param.userName}">
              <br>
              Password: <input type="password" name="password" value="${param.password}">
          </c:if>
          <c:if test="${! empty accounts}">
              User name:  <input type="text" name="userName" 
              					 value="${param.userName}" readonly>
              <br>
              Password: <input type="password" name="password" 
                               value="${param.password}" readonly>
              <br>
              <select name="account">
                  <c:forEach items="${accounts}" var="account">
                      <option value="${account}">${account}</option>
                  </c:forEach>
              </select>
          </c:if>
          <br>
          <input type="hidden" name="facilityName" value="${facilityName}">
          <input type="hidden" name="returnTo" value="${returnTo}">
          <c:if test='${empty inUse}'>
          	  <button type="submit" name="startSession">OK</button>
          </c:if>
          <c:if test='${!empty inUse}'>
          	  <button type="submit" name="startSession">Logout Existing Session</button>
          	  <input type="hidden" name="endOldSession" value="yes">
          </c:if>
          <button type="button" onclick="window.location = '${returnTo}'">Cancel</button>
        </form>
	</div><!-- /container -->
<%@ include file="/WEB-INF/jsp/commonFooter.jspFrag" %>
    </body>
</html>