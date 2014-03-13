<html>
<body>
	<h1>Graphite Demo</h1>
	<input type="text" name="keyword" value="candidate"> <button id = "expand" type="button">expand</button> <button id = "contract" type="button">contract</button> 
	<br>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script> 
	<script type="text/javascript">
		$(document).ready(function() {
			$("#msgid").html("This is Hello World by JQuery");
		});
		$("#expand").bind("click",function(){
		    alert("The paragraph was clicked.");
		  });
        $("#contract").bind("click",function(){
            alert("The paragraph was clicked1.");
          });
	</script>
	<div id="result"></div>
</body>
</html>