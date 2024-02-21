<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" import="fr.cirad.web.controller.GalaxyClientController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Results</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script>
        function prepareNextPage() {
            var selectedHistory = document.querySelector('input[name="selectedHistory"]:checked');
            if (selectedHistory) {
                var historyId = selectedHistory.value;
                window.location.href = "datasets.do?historyId=" + historyId;
            } else {
                alert("Please select a history before proceeding.");
            }
        }
    </script>
</head>

<body style='background-color:#f0f0f0;'>
    <div class="container">
        <h1>Hello ${userName} !</h1>
        <p>Galaxy instance URL: ${param.galaxyUrl}</p>
        <p>API Key: ${param.apiKey}</p>
        <h2>Select the history you want to work on:</h2>
        <c:if test="${not empty histories}">
            <c:forEach var="history" items="${histories}">
                <label class="inputs-label">
                    <input type="radio" name="selectedHistory" value="${history.id}">
                    ${history.name}
                </label>
            </c:forEach>
        </c:if>
        <c:if test="${empty histories}">
            <p>No history available.</p>
        </c:if>
        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>
        <div class="right-container">
            <button type="button" class="submit" onclick="prepareNextPage()">Next</button>
        </div>
    </div>
</body>
</html>
