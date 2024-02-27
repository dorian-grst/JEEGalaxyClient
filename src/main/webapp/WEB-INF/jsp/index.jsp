<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" import="fr.cirad.web.controller.GalaxyClientController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <script type="text/javascript" src="js/jquery-1.12.4.min.js"></script>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const form = document.getElementById('galaxyForm');
            form.addEventListener('submit', function () {
                document.querySelector('.loader-container').style.display = 'flex';
            });
        });
        function getLocalStorage(){
            var galaxyUrl = localStorage.getItem("galaxyUrl");
            var apiKey = localStorage.getItem("apiKey");

            console.log("galaxyUrl in localStorage: " + galaxyUrl);
            console.log("apiKey in localStorage: " + apiKey);

            if (galaxyUrl) {
                console.log(" add galaxyUrl from localStorage to galaxyUrl");
                $("#galaxyUrl").val(galaxyUrl);
                console.log(" add apiKey from localStorage to apiKey");
                $("#apiKey").val(apiKey);
            }
        }
        function setLocalStorage(){
            console.log("localStorage set item"+$("#galaxyUrl").val()+$("#apiKey").val());
            localStorage.setItem("galaxyUrl", $("#galaxyUrl").val());
            localStorage.setItem("apiKey", $("#apiKey").val());
            console.log(localStorage);
        }
        $(document).ready(function() {
            getLocalStorage();
        });

    </script>

</head>

<body style='background-color:#f0f0f0;'>
    <div class="loader-container">
        <div class="loader"></div>
        <h3>Connexion...</h3>
    </div>
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
                <button type="submit" class="submit" onClick="setLocalStorage()">Submit</button>
            </div>
        </form>
    </div>
</body>
</html>
