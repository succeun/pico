$( document ).ajaxStart(function() {
	$( "#loadingother" ).show();
});

$( document ).ajaxComplete(function() {
	$( "#loadingother" ).hide();
});

function reload() {
    $.ajax({
        url: "reload",
        type: "post",
        data: null,
        success: function() {
        	 alert("Success reload.");
             location.reload();
        },
        error: function() {
        	alert("Server Fail...");
        },
        complete: function() {
        	//
        }
    });
}

function requestPage(url, request) {
	var data = null;
	if (request != null)
		data = $(request).serialize();
		
    $.ajax({
        url: url,
        type: "post",
        data: data,
        success: function(data) {
        	if (data != null)
        		alert(data);
        	else
        		alert("Success reload.");
            location.reload();
        },
        error: function() {
        	alert("Server Fail...");
        },
        complete: function() {
        	//
        }
    });
}

function openWindow(url, name){
    window.open(url, ((name != null) ? name : null),
    "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes");
}
