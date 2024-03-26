<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>JEEGalaxyClient</title>
        <script type="text/javascript" src="../js/jquery-1.12.4.min.js"></script>
        <link rel="stylesheet" type="text/css" href="../css/styles.css">
        <script>
            $(document).ready(function () {
                $('#next_upload').click(function () {
                    const selectedHistory = document.querySelector('input[name="selectedHistory"]:checked');
                    const galaxyUrl = localStorage.getItem("galaxyUrl");
                    const apiKey = localStorage.getItem("apiKey");
                    if (selectedHistory) {
                        const historyId = selectedHistory.value;
                        const queryString = window.location.search;
                        const urlParams = new URLSearchParams(queryString);
                        const filesURLsValues = urlParams.getAll('filesURLs');
                        const filesURLsArray = [];
                        filesURLsValues.forEach(value => {
                            filesURLsArray.push(encodeURIComponent(value));
                        });
                        window.location.href = "upload.do?apiKey=" + apiKey + "&galaxyUrl=" + galaxyUrl + "&historyId=" + historyId + "&filesURLs=" + filesURLsArray.join("&filesURLs=");
                    } else {
                        alert("Please select a history before proceeding.");
                    }
                });
                $('#back_upload').click(function () {
                    history.back();
                });
            });
        </script>
    </head>

    <body>
        <div class="container">
            <div style="display: flex; flex-direction: row; gap: 10px; white-space: nowrap;">
                <h1>Connected to</h1>
                <h1 style="color: seagreen">${galaxyUrl}</h1>
                <h1>as</h1>
                <h1 style="color: seagreen">${userName}</h1>
            </div>
            <h2>Select the history you want to work on :</h2>
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
            <div class="space-container">
                <button id="back_upload" type="button" class="back">Back</button>
                <button id="next_upload" type="button" class="submit">Next</button>
            </div>
        </div>
    </body>
</html>
