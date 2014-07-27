$.noConflict(true);

// ----------------end of above tooltip files
/**
 * this is JQuery script
 * 
 */
// document ready handler
jQuery(function($) {
	// window.alert("Hello JQuery");
	tooltipClass = 'div.regularSection a'; // replace with your class, for
	// multiple
	// classes, separate with comma.

	function str_replace(search, replace, subject) {
		return subject.split(search).join(replace);
	}
	xOffset = 5;
	yOffset = 20;
	fadeInTime = 1000;
	jQuery(tooltipClass)
			.hover(
					function(e) {
						this.t = $(this).text();
						this.title = "";
						this.t = str_replace("::", "<br />", this.t);
						this.t = str_replace("[!]",
								"<span class='tooltipTitle'>", this.t);
						this.t = str_replace("[/!]", "</span><br />", this.t);
						this.t = str_replace("[", "<", this.t);
						this.t = str_replace("]", ">", this.t);
						if (this.t != "") {
							jQuery("body").append(
									"<p id='tooltip'>" + this.t + "</p>");
							jQuery("#tooltip").css(
									"top",
									$(this).offset().top + $(this).height()
											- xOffset + "px").css("left",
									e.screenX + "px").fadeIn(fadeInTime);
						}
					}, function() {
						jQuery("#tooltip").remove();
					});
	jQuery(tooltipClass).mousemove(
			function(e) {
				jQuery("#tooltip").css(
						"top",
						$(this).offset().top + $(this).height() - xOffset
								+ "px").css("left", e.screenX + "px");
			});

	// $('.commandButtonOrLink')
	// add highlight on the help
	$("#leftSideLink a:first").addClass('highlight');
	$("a", "#leftSideLink").bind("click", function(event) {
		$("a", "#leftSideLink").removeClass("highlight");
		$(this).addClass('highlight');

	});
	// add the highlight for main menu
	$(".nav1 a:first").addClass('current');

	// toggle help link
	toggleHelpLink();

	// highlight menu
	highLightMenu();

});

function HandleAjaxError(xhr, status, exception) {
	var message = "There was an error with the AJAX request.\n";
	switch (status) {
	case 'timeout':
		message += "The request timed out.";
		break;
	case 'notmodified':
		message += "The request was not modified but was not retrieved from the cache.";
		break;
	case 'parseerror':
		message += "XML/Json format is bad.";
		break;
	default:
		message += "HTTP Error (" + request.status + " " + request.statusText
				+ ").";
	}
	if (exception != null) {
		message += "Exception has encountered: " + exception;
	}
	message += "\n";
	alert(message);
}

// used to save
function ajaxSave(source) {
	var ed = tinyMCE.get('textarea');

	// Do you ajax call here, window.setTimeout fakes ajax call
	ed.setProgressState(1); // Show progress
	alert(ed.getContent());
	/*
	 * document.getElementById('textareahiddenid').setValue(ed.getContent());
	 * jsf.ajax.request(source, event, { render : '@form', exectute : '@form'
	 * }); window.setTimeout(function() { ed.setProgressState(0); // Hide
	 * progress alert(ed.getContent()); }, 300);
	 */
}

function handleComplete(xhr, status, args) {
	var isValid = args.isValid;
	if (isValid) {
		if (typeof PF('messageDlg') != 'undefined') {
			PF('messageDlg').hide();
		}
		if (typeof PF('loginDlg') != 'undefined') {
			PF('loginDlg').hide();
			// init textarea with xheditor when user log in on viewthread page
			// window.xheditor = false;
			// initTextAreaWithEditor();
			window.location = location.href;
		}
		if (typeof PF('addStore') != 'undefined') {
			PF('addStore').hide();
		}
		if (typeof PF('addAffiliate') != 'undefined') {
			PF('addAffiliate').hide();
		}
		if (typeof PF('viewEditStore') != 'undefined') {
			PF('viewEditStore').hide();
		}
		if (typeof PF('viewEdit') != 'undefined') {
			PF('viewEdit').hide();
		}
		if (typeof PF('viewEditWidget') != 'undefined') {
			PF('viewEditWidget').hide();
		}

	}
}

// move cursor to the given location. used in the viewThread page, when reply
// button is clicked.
function jumpTo(to) {
	var new_position = jQuery(to).offset();
	window.scrollTo(new_position.left, new_position.top);
}

// seach the 1st xheditor from the given fromForm for the PIcture URL and set it
// to target value
function searchAndSetThumbPicURL(fromFormId, to) {
	var picURL = $(fromFormId).find('td.xheIframeArea').find('iframe').first()
			.contents().find('body').find('img').first().attr('src');
	// alert(picURL);

	if (picURL) {
		$(to).attr('value', picURL);
		// alert($(to).attr('value'));
	}
}
function searchAndSetShortContent(fromFormId, to) {
	var fullContent = $(fromFormId).find('td.xheIframeArea').find('iframe')
			.first().contents().find('body').text();
	// alert(picURL);
	var Two50Words = fullContent.split(' ').slice(0,250).join(' ');

	if (Two50Words) {
		$(to).attr('value', Two50Words);
		// alert($(to).attr('value'));
	}
}

function resizeToMax(id) {
	myImage = new Image();
	var img = document.getElementById(id);
	myImage.src = img.src;
	if (myImage.naturalWidth > 130 || myImage.naturalHeight > 130) {
		if (myImage.naturalWidth > myImage.naturalHeight) {
			myImage.height = 130 / myImage.naturalWidth * myImage.naturalHeight;
			myImage.width = 130;

		} else {
			myImage.width = 130 / myImage.naturalHeight * myImage.naturalWidth;
			myImage.height = 130;
		}
	}
	img.width = myImage.width;
	if (img.width > 130) {
		img.width = 130;
	}
	img.height = myImage.height;
	if (img.height > 130) {
		img.height = 130;
	}
}

function disableButton(id) {
	$(id).attr('disabled', true);
	$(id).attr('onclick', null).unbind('click');
	$(id).addClass('disabled');

}
function toggleHelpLink() {
	$ = jQuery;
	$("a.helpLink").bind("click", function(event) {

		$(this).parent().parent().find("div").toggleClass("helpText");

	});
}
function highLightMenu() {
	$ = jQuery;
	var filePath = window.location.pathname;
	if (filePath != "/index.xhtml") {
		$("#home").removeClass("current");
	}
	if (filePath == '/category/indexthreads4AllCategory.xhtml') {
		$("#hotDeal").addClass("current");
	} else if (filePath == '/news/index.xhtml') {
		$("#newsIndex").addClass("current");
	} else if (filePath == '/stores/listStore.xhtml') {
		$("#listStore").addClass("current");
	} else if (filePath == '/help/help.xhtml') {
		$("#cashBack").addClass("current");
	}

}
