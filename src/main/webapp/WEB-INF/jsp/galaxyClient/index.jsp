<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>JEEGalaxyClient</title>
        <script type="text/javascript" src="../js/jquery-1.12.4.min.js"></script>
        <link rel="stylesheet" type="text/css" href="../css/styles.css">
        <script>
            $(document).ready(function () {
                function setLocalStorage() {
                    document.querySelector('.loader-container').style.display = 'flex';
                    localStorage.setItem("galaxyUrl", $("#galaxyUrl").val());
                    localStorage.setItem("apiKey", $("#apiKey").val());
                }

                const galaxyUrl = localStorage.getItem("galaxyUrl");
                const apiKey = localStorage.getItem("apiKey");
                if (galaxyUrl) {
                    $("#galaxyUrl").val(galaxyUrl);
                    $("#apiKey").val(apiKey);
                }
                $('#next_histories').click(function () {
                    setLocalStorage();
                    const apiKey = encodeURIComponent($("#apiKey").val());
                    const galaxyUrl = encodeURIComponent($("#galaxyUrl").val());
                    const queryString = window.location.search;
                    const urlParams = new URLSearchParams(queryString);
                    const filesURLsValues = urlParams.getAll('filesURLs');
                    const filesURLsArray = [];
                    filesURLsValues.forEach(value => {
                        filesURLsArray.push(encodeURIComponent(value));
                    });
                    window.location.href = "histories.do?apiKey=" + apiKey + "&galaxyUrl=" + galaxyUrl + "&filesURLs=" + filesURLsArray.join("&filesURLs=");
                });
            });
        </script>
    </head>
    <body>
        <div class="loader-container">
            <div class="loader"></div>
            <h3>Connexion...</h3>
        </div>
        <div class="container">
            <h1>Connect to Galaxy</h1>
            <form id="galaxyForm">
                <label for="galaxyUrl">Galaxy Instance URL:</label>
                <input type="text" id="galaxyUrl" name="galaxyUrl" placeholder="Enter Galaxy Instance URL" required>
                <label for="apiKey">API Key:</label>
                <input type="text" id="apiKey" name="apiKey" placeholder="Enter your API Key" required>
                <c:if test="${not empty error}">
                    <p class="error">${error}</p>
                </c:if>
                <div class="right-container">
                    <button id="next_histories" type="button" class="submit">Submit</button>
                </div>
            </form>
        </div>
    </body>
</html>
