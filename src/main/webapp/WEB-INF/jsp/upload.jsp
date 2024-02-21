<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" import="fr.cirad.web.controller.GalaxyClientController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Upload Files</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script>
    function handleNext() {
        document.querySelector('.loader-container').style.display = 'flex';
        var textarea = document.getElementById("filesTextarea");
        var lines = textarea.value.split(/\r?\n/);
        var fileList = [];
        
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i].trim();
            if (line !== "") {
                fileList.push(line);
            }
        }
        
        if (fileList.length > 0) {
            var fileListString = fileList.join(",");
            
            window.location.href = "uploaded.do?historyId=${historyId}&fileList=" + encodeURIComponent(fileListString);
            window.open("https://usegalaxy.eu/", "_blank");
        } else {
            alert("Please enter at least one file link.");
        }
    }
    </script>
</head>
<body>
	<div class="loader-container">
		<div class="loader"></div>
		<h3>Uploading files...</h3>
	   	<div class="text-container">
			<h4>Check the progress at :</h4>
			<a href="https://usegalaxy.eu/" target="_blank">https://usegalaxy.eu/</a>
		</div>
	</div>
	<div class="container">
	    <h1>Upload Files</h1>
       	<h4>Write in the textarea the absolute path or url of the files you want to upload</h4>
       	<p>Note: Use one line per file as in the example</p>
       	<textarea id="filesTextarea" rows="5" placeholder='https://application/example/file.fasta &#10;/home/usr/example.txt'></textarea>
	    <div class="space-container">
	        <button type="button" class="back" onclick="history.back()">Back</button>
	        <button type="button" class="submit" onclick="handleNext()">Upload</button>
	    </div>
	</div>
</body>
</html>