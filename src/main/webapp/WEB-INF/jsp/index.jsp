<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" import="fr.cirad.web.controller.GalaxyClientController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <script type="text/javascript" src="js/jquery-1.12.4.min.js"></script>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
</head>

<body style='background-color:#f0f0f0;'>
    <div class="container">
        <h1>Connect to Galaxy</h1>
        <form id="galaxyForm" action="histories.do" method="get">
            <label for="galaxyUrl">Galaxy Instance URL:</label>
            <input type="text" id="galaxyUrl" name="galaxyUrl" placeholder="Enter Galaxy Instance URL" required>
            <label for="apiKey">API Key:</label>
            <input type="text" id="apiKey" name="apiKey" placeholder="Enter your API Key" required>
            <c:if test="${not empty error}">
            	<p class="error">${error}</p>
        	</c:if>
            <div class="right-container">
            	<button type="submit" class="submit">Submit</button>
            </div>
        </form>
    </div>
</body>
</html>
