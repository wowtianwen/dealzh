// ------------------facebook js plugin---------------------Start
window.fbAsyncInit = function() {
	FB.init({
		appId : '139576526194442', // App ID
		// channelUrl : '//WWW.YOUR_DOMAIN.COM/channel.html', // Channel File
		status : true, // check login status
		cookie : true, // enable cookies to allow the server to access the
		// session
		xfbml : true
	// parse XFBML
	});

	// Additional initialization code here
};

// Load the SDK Asynchronously
(function(d) {
	var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
	if (d.getElementById(id)) {
		return;
	}
	js = d.createElement('script');
	js.id = id;
	js.async = true;
	js.src = "//connect.facebook.net/en_US/all.js";
	ref.parentNode.insertBefore(js, ref);
}(document));

$(document).ready(function() {
	$('#share_button').click(function(e) {
		e.preventDefault();
		// alert($('#threadContent img').eq(1).attr('src'));
		var dealReferUId = $('#dealReferUId').attr('value');
		var referURL = $(location).attr('href');
		if (dealReferUId) {
			referURL = referURL + "&fromuid=" + dealReferUId;
		}
		FB.ui({
			method : 'feed',
			name : $('head title').text(),
			link : referURL,
			picture : $('#contentDetail img').first().attr('src'),
			description : $('#contentDetail').text().substring(0, 200)

		});
	});
});

// ------------------facebook js plugin---------------------End
