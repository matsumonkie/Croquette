var POSITION_OF_INCOMING_MESSAGE = "left"
var POSITION_OF_SENDING_MESSAGE = "right" 
var SEND_SMS_ACTION = "send-sms-action"
var RECEIVE_SMS_ACTION = "receive-sms-action"

	
/**
 * send a json formatted message with a websocket and add message to the
 * conversation UI
 */
var sendMessage = function (websocket, msg) {
	var activeContact = getCurrentActiveContact()

	if(activeContact == null) {
		return
	}
	
	var now = new Date()

	var jsonObject = { 
			content: msg,
			authorPhoneNumber: "me",
			action: SEND_SMS_ACTION,
			recipient: activeContact,
			date: now.getHours() + "h" + now.getMinutes()
	}
	var jsonMsg = JSON.stringify( jsonObject )		
	websocket.send(jsonMsg)
	addMessageToConversation(jsonObject, POSITION_OF_SENDING_MESSAGE)
	
}


/**
 * return the current contact phone number 
 */
function getCurrentActiveContact() {
	return $('li.active').attr("title")

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
    	addMessageToConversation(message, POSITION_OF_INCOMING_MESSAGE)
    }
}


/**
 * add a message to the conversation UI
 */
function addMessageToConversation(jsonMsg, position) {
	// create the message element
	var box = createArrowBox(jsonMsg, position)
	
	// set it invisible
	box.css("opacity", "0")
	
	// add it to the UI
   	$('#messages').append(box)
   	
   	// set it visible
    box.animate({ opacity: 1 }, 500)

    // scroll down smoothly so that we can always see the last message
    $('.inner-center').animate({ scrollTop: $('#messages').height() }, 400)
}


/**
 * create a dom containing the message positioned either on the left or on the
 * right depending on who is the author
 */
function createArrowBox(jsonMsg, position) {
	var message = new Array()
	message.push('<tr>')
	
	// create message
	message.push('<td align="' + position + '">')
	message.push('<i class="date">' + jsonMsg.date + '</i>')
	message.push('<div class=' + position + '_arrow_box>' + jsonMsg.content + '</div>')
	message.push('</td>')

	message.push('</tr>')

	// create a fake dom with the created array and only retreive its children
	var dom = $('<div></div>').html( message.join('') ).children();
	
	return dom
}


/**
 * clear the conversation
 */
function clearConversation() {
	$("#messages").empty()
}


function foo(element) {
	alert($(element).attr("title"))
}

/**
 * 
 */
function loadConversation(element, phoneNumber) {
	// conversation is already loaded
	if($(element).hasClass('active')) {
		return
	}
	
	// clear chat
	clearConversation()
	
	// unhighlight previous contact
	$('li.active').removeClass('active')
	
	// highlight current contact
	$(element).addClass('active')
	
	// retrieve the conversation
	$.getJSON("/getConversationAjax?phoneNumber=" + phoneNumber, {}, function(conversation) { addConversationToView(conversation) } 
	).error(function() {
		alert("error")
	});
			
}

function addConversationToView(conversation) {
	$.each(conversation, function(index, message) {
		if (message.action == SEND_SMS_ACTION) {
			addMessageToConversation(message, POSITION_OF_SENDING_MESSAGE)
		} else {
			addMessageToConversation(message, POSITION_OF_INCOMING_MESSAGE)
		}
	})
}
