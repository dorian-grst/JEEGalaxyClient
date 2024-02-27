<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>JEEGalaxyClient</title>
        <link rel="stylesheet" type="text/css" href="css/styles.css">
        <script>
            function redirectToDatasetsPage(historyId) {
                window.location.href = "datasets.do?historyId=" + historyId;
            }
        </script>
    </head>
    <body>
        <div class="container">
            <h1>Uploaded Datasets</h1>
            <h2>On history id: ${historyId}</h2>
            <ul>
                <c:forEach var="file" items="${fileList}">
                    <li>${file}</li>
                </c:forEach>
            </ul>
            <div class="space-container">
                <button type="button" class="back" onclick="history.back()">Back</button>
                <button type="button" class="submit" onclick="redirectToDatasetsPage('${historyId}')">Next</button>
            </div>
        </div>
    </body>
</html>
