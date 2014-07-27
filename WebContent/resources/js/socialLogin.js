/**
 * this is JQuery script
 * 
 */
// for facebook
window.fbAsyncInit = function() {
	FB.init({
		appId : '139576526194442',
		status : true, // check login status
		cookie : true, // enable cookies to allow the server to
		// access the session
		xfbml : true
	// parse XFBML
	});

	// Here we subscribe to the auth.authResponseChange JavaScript
	// event. This event is fired
	// for any authentication related change, such as login, logout
	// or session refresh. This means that
	// whenever someone who was previously logged out tries to log
	// in again, the correct case below
	// will be handled.
	FB.Event.subscribe('auth.authResponseChange', function(response) {
		// Here we specify what we do with the response
		// anytime this event occurs.
		if (response.status === 'connected') {
			// The response object is returned with a status
			// field that lets the app know the current
			// login status of the person. In this case,
			// we're handling the situation where they
			// have logged in to the app.
			populateUserInfo();
		} else if (response.status === 'not_authorized') {
			// In this case, the person is logged into
			// Facebook, but not into the app, so we call
			// FB.login() to prompt them to do so.
			// In real-life usage, you wouldn't want to
			// immediately prompt someone to login
			// like this, for two reasons:
			// (1) JavaScript created popup windows are
			// blocked by most browsers unless they
			// result from direct interaction from people
			// using the app (such as a mouse click)
			// (2) it is a bad experience to be continually
			// prompted to login upon page load.
			// FB.login();
		} else {
			// In this case, the person is not logged into
			// Facebook, so we call the login()
			// function to prompt them to do so. Note that
			// at this stage there is no indication
			// of whether they are logged into the app. If
			// they aren't then they'll see the Login
			// dialog right after they log in to Facebook.
			// The same caveats as above apply to the
			// FB.login() call here.
			// FB.login();
		}
	});
};

// Load the SDK asynchronously
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

function populateUserInfo() {
	FB.api('/me', function(response) {
		if (response.username) {
			$('#userid4Social').val(response.username.replace(/\s+/g, ''));
		} else {
			var email = response.email;
			var atindex = email.indexOf('@');
			if (atindex != -1) {
				$('#userid4Social').val(email.substring(0, atindex));
			} else {
				$('#userid4Social').val(email);
			}
		}
		$('#email4Social').val(response.email);
		$('#socialToken').val(response.id);
		$('#loginType').val('FACEBOOK');
		if ($('#enroll4SocialForm .ui-message-error-icon').size() > 0) {
			// $('#enroll4SocialFormqq')
			// .addClass('diplayNone');
			// left blank on purpose
		} else {
			$('#submit').click();
		}

	});
}

// for google.com
(function() {
	var po = document.createElement('script');
	po.type = 'text/javascript';
	po.async = true;
	po.src = 'https://apis.google.com/js/client:plusone.js';
	var s = document.getElementsByTagName('script')[0];
	s.parentNode.insertBefore(po, s);
})();

function signinCallback(authResult) {
	if (authResult['status']['signed_in']) {
		// Update the app to reflect a signed in user
		// Hide the sign-in button now that the user is
		// authorized, for example:
		document.getElementById('signinButton').setAttribute('style',
				'display: none');
		populateGoogleUserInfo(authResult);

	} else {
		// Update the app to reflect a signed out user
		// Possible error values:
		// "user_signed_out" - User is signed-out
		// "access_denied" - User denied access to your app
		// "immediate_failed" - Could not automatically log in
		// the user
		// console.log('Sign-in state: ' + authResult['error']);
	}
}

function populateGoogleUserInfo(oInfo) {
	gapi.client.load('plus', 'v1', loadProfile); // Trigger
	// request
	// to get
	// the email
	// address.
}
/**
 * Uses the JavaScript API to request the user's profile, which includes their
 * basic information. When the plus.profile.emails.read scope is requested, the
 * response will also include the user's primary email address and any other
 * email addresses that the user made public.
 */
function loadProfile() {
	var request = gapi.client.plus.people.get({
		'userId' : 'me'
	});
	request.execute(loadProfileCallback);
}

/**
 * Callback for the asynchronous request to the people.get method. The profile
 * and email are set to global variables. Triggers the user's basic profile to
 * display when called.
 */
function loadProfileCallback(obj) {
	// Filter the emails object to find the user's primary
	// account, which might
	// not always be the first in the array. The filter() method
	// supports IE9+.
	oInfo = obj;
	displayName = oInfo.displayName;
	displayName = displayName.replace(/\s+/g, '');
	$('#userid4Socialgoogle').val(displayName);
	if (oInfo.emails[0]) {
		$('#email4Socialgoogle').val(oInfo.emails[0].value);
	} else {
		$('#email4Socialgoogle').val('provideYourEmail@gmail.com');
	}
	$('#socialTokengoogle').val(oInfo.id);
	$('#loginTypegoogle').val("GOOGLE");
	if ($('#enroll4SocialFormgoogle .ui-message-error-icon').size() > 0) {
		// $('#enroll4SocialForm').addClass('diplayNone');
		// left blank on purpose
	} else {
		$('#submitgoogle').click();
	}
}

// for qq

var cbLoginFun = function(oInfo, opts) {
	// 登录成功
	// 根据返回数据，更换按钮显示状态方法
	var dom = document.getElementById(opts['btnId']), _logoutTemplate = [
	// 头像
	'<span><img src="{figureurl}" class="{size_key}"/></span>',
	// 昵称
	'<span>{nickname}</span>',
	// 退出
	'<span><a href="javascript:QC.Login.signOut();">退出</a></span>' ].join("");
	dom && (dom.innerHTML = QC.String.format(_logoutTemplate, {
		nickname : QC.String.escHTML(oInfo.nickname), // 做xss过滤
		figureurl : oInfo.figureurl
	}));
	if (QC.Login.check()) {
		populateQQUserInfo(oInfo);
	}
};

function populateQQUserInfo(oInfo) {
	QC.Login.getMe(function(openId, accessToken) {
		$('#userid4Socialqq').val(oInfo.nickname);
		$('#email4Socialqq').val('provideYourEmail@qq.com');
		$('#socialTokenqq').val(openId);
		$('#loginTypeqq').val("QQ");
		if ($('#enroll4SocialFormqq .ui-message-error-icon').size() > 0) {
			// $('#enroll4SocialForm').addClass('diplayNone');

			// left blank on purpose
		} else {
			$('#submitqq').click();
		}
	});
}