/**
 * this is JQuery script
 * 
 */
// document ready handler
jQuery(function($) {
	// window.alert("Hello JQuery");
	// highligh the links on the leftside of the profile page.
	$("#profileLeftLink a:first").addClass('highlight');
	$("a", "#profileLeftLink").bind("click", function(event) {
		$("a", "#profileLeftLink").removeClass("highlight");
		$(this).addClass('highlight');

	});

	refeshPic('#userProfilePicId');

	// select refer link on click

});

function refeshPic(picElementId) {
	// location.reload(true);
	var $ = jQuery;
	var src = $(picElementId).attr("src");
	// add extra query string to make sure brower will reload the image
	$(picElementId).attr("src", src + "?" + new Date());
}

function registerReferLinkClick() {
	$("#referLink").click(function() {
		$(this).select();
	});
}
