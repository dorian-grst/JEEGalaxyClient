<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" import="fr.cirad.web.controller.GalaxyClientController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>

<head>
	<script type="text/javascript" src="js/jquery-1.12.4.min.js"></script>
	<script type="text/javascript">
    $(document).ready(function () {
		$.ajax({	// load assemblies
			url: '<c:url value="<%=GalaxyClientController.testURL%>" />',
			type: "POST",
			dataType: "json",
			contentType: "application/json;charset=utf-8",
			data: JSON.stringify({
				"field1": "value1", "field2": "value2"
			}),
			success: function(jsonResult) {
				alert("ok");
			},
			error: function(xhr, ajaxOptions, thrownError) {
				alert("error: " + thrownError);
			}
		});
    });
	</script>
</head>

<body style='background-color:#f0f0f0;'>
test ${toto}
</body>

</html>