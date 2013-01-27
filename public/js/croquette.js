(function() {
	var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var chatSocket = new WS("@routes.Application.chatSocket().webSocketURL()")

    var sendMessage = function() {
        chatSocket.send(JSON.stringify( { content: $("#talk").val() } ))
        $("#talk").val('')
    }
    
	var receiveEvent = function(event) {
        var message= JSON.parse(event.data)

	    if (message.error) {
	        chatSocket.close()
	        return
	    } else if (message) {
	    	i++;
	    	var position = "left"
	    	if (i % 2) {
	    		position = "right"
	    	}
	    	var box = createArrowBox(message.content, position)
	    	addRow(box)
	    	scrollBottom()
	    }
    }
	
	var i = 2;
	
	function createArrowBox(content, position) {
		var date    = $('<tr><td class="date" align="' + position + '"><i>14:25</i></td></tr>')
		var bubble  = $('<div class="' + position + '_arrow_box">' + content + '</div>')
		var message = $('<tr><td align="' + position + '">' + bubble.prop("outerHTML") + '</td>')
		message.css("opacity", "0")
		
		return message
	}
	
	function addRow(box) {
        $('#messages tr:last').after(box);
        box.animate({ opacity: 1 }, 500)
	}
	
	//for each new message added, scroll down smoothly so that we can always see the last message 
	function scrollBottom() {
		$('.inner-center').animate({ scrollTop: $('#messages').height() }, 400)
	}
	
    var handleReturnKey = function(e) {
	    if (e.charCode == 13 || e.keyCode == 13) {
	        e.preventDefault()
	        sendMessage()
	    } 
	}
	
    // Fonctions appelees lors des evenements sur les objets HTML
	$("#talk").keypress(handleReturnKey)  
	$(".sendButton").click(function() { sendMessage() });
	$(".clearButton").click(function() { $("#messages").html('<table id="messages" style="width:100%"><tr></tr></table>') }); // TODO: Suppression dans le cache
	
	chatSocket.onmessage = receiveEvent
});

function choixContact(phoneNumber) {
	$.getJSON("/getConversationAjax?phoneNumber=" + phoneNumber, {}, function(conversation) {
		$.each(conversation.messages, function(index, message) {
			//if (message.send)
			//	alert("Droite : " + message.text)
			//else
			//	alert("Gauche : " + message.text)
		})
	})
	.error(function() { alert("error"); }) // DEBUG
}