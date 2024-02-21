<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Datasets</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script>
        function validateForm() {
            var checkboxes = document.querySelectorAll('input[type="checkbox"]:checked');
            if (checkboxes.length === 0) {
                alert("Please select at least one dataset.");
                return false;
            }
            return true;
        }
    </script>
</head>

<body>
    <div class="container">
        <div class="text-container">
            <h1>Datasets for History ${historyId}</h1>
        </div>
        <form id="datasetsForm" action="workflowAvailable.do" method="get" onsubmit="return validateForm()">
            <c:if test="${not empty datasets}">
                <c:forEach var="dataset" items="${datasets}">
				    <label class="inputs-label">
				        <input type="checkbox" name="selectedDatasetIds" value="${dataset.id}" />
				        ${dataset.name}
				    </label>
				</c:forEach>
            </c:if>
            <c:if test="${empty datasets}">
                <p>No datasets available.</p>
            </c:if>
            <c:if test="${not empty error}">
                <p class="error">${error}</p>
            </c:if>
            <input type="hidden" name="historyId" value="${historyId}">
            <a href="upload.do?historyId=${historyId}">Upload files</a>
            <div class="space-container">
                <button type="button" class="back" onclick="history.back()">Back</button>
                <c:if test="${not empty datasets}">
                	<button type="submit" class="submit">Next</button>
				</c:if>
            </div>
        </form>
    </div>
</body>
</html>
