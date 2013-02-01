var POSITION_OF_INCOMING_MESSAGE = "left"
var POSITION_OF_SENDING_MESSAGE = "right" 

/**
 * send a json formatted message with a websocket and add message to the conversation UI 
 */
var sendMessage = function (websocket, msg) {
	var jsonMsg = JSON.stringify( { content: msg })		
	websocket.send(jsonMsg)
	addMessageToConversation(msg, POSITION_OF_SENDING_MESSAGE)
	
}


/**
 * check if key pressed was the return key
 */ 
function keyPressedIsReturnKey(key) {
    if (key.charCode == 13 || key.keyCode == 13) {
        key.preventDefault()
        return true
    } 
    return false
}


/**
 * add the incoming message to the chat  
 */
function handleIncomingMessage(event) {
	var message = JSON.parse(event.data)

    if (message.error) {
        chatSocket.close()
        return
    } else if (message) {
    	addMessageToConversation(message.content, POSITION_OF_INCOMING_MESSAGE)
    }
}


/**
 * add a message to the conversation UI  
 */
function addMessageToConversation(message, position) {
	// create the message element
	var box = createArrowBox(message, position)
	
	// set it invisible
	box.css("opacity", "0")
	
	// add it to the UI
   	$('#messages').append(box)
   	
   	// set it visible
    box.animate({ opacity: 1 }, 500)

    // scroll down smoothly so that we can always see the last message
    $('.inner-center').animate({ scrollTop: $('#messages').height() }, 400)
}

		
function createArrowBox(content, position) {
	var date    = $('<tr><td class="date" align="' + position + '"><i>14:25</i></td></tr>')
	var bubble  = $('<div class="' + position + '_arrow_box">' + content + '</div>')
	var message = $('<tr><td align="' + position + '">' + bubble.prop("outerHTML") + '</td>')
	
	return message
}


/**
 * clear the conversation
 */
function clearConversation() {
	$("#messages").empty()
}


function choixContact(phoneNumber) {
	$.getJSON("/getConversationAjax?phoneNumber=" + phoneNumber, {},
			function(conversation) {
				$.each(conversation.messages, function(index, message) {
					// if (message.send)
					// alert("Droite : " + message.text)
					// else
					// alert("Gauche : " + message.text)
				})
			}).error(function() {
		alert("error");
	}) // DEBUG
}