
// Logging
(function () {
	'use strict';

	// Define logging methods if they do not already exists
	if (!window.console) {
		var names = ["log", "debug", "info", "warn", "error", "assert", "dir", "dirxml",
			"group", "groupEnd", "time", "timeEnd", "count", "trace", "profile", "profileEnd"];
		window.console = {};
		var doNothing = function() {};
		for (var i = 0; i < names.length; ++i) {
			window.console[names[i]] = doNothing;
		}
	}
}());


// CSRF Ajax handling
(function ($) {
	'use strict';

	var csrfToken;

	// pre-filter - Write csrf token to request headers
	$.ajaxPrefilter(function (options, originalOptions, xhr) {
		if (csrfToken) {
			console.log('ajaxPrefilter - setting csrf token', csrfToken);
			xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
		}
	});

	// Read csrf token from response headers
	$(document).ajaxComplete(function (event, xhr, settings) {
		var token = xhr.getResponseHeader('X-CSRF-TOKEN');
		if (token) {
			csrfToken = token;
			console.log('ajaxComplete - read csrf token from response', csrfToken);
		}
	});
}(window.jQuery));


// Main functionality
(function ($, ko) {
	'use strict';

	var viewModel = {
		// Which parts of the flow to display ('frony', 'login', 'userInfo', 'attachGoogle', 'attachYubi')
		pageFlow: ko.observable('main'),
		pageSubFlow: ko.observable(),

		loginDetails: {
			userName: ko.observable(),
			displayName: ko.observable(),
			email: ko.observable(),
			type: ko.observable(),
			state: ko.observable(),
			twoFactorType: ko.observable(),
			created: ko.observable(),
			lastLogin: ko.observable(),
			loggedIn: ko.observable(false),
			fullyAuthenticated: ko.observable(false)
		},
		loginFields: {
			username: ko.observable(),
			password: ko.observable()
		},
		otpFields: {
			otp: ko.observable()
		}
	};

	function ajaxGet(url, doneFunc) {
		console.log('GET ' + url);
		$.getJSON(url).done(doneFunc);
	}

	function ajaxPost(url, data, doneFunc) {
		console.log('POST ' + url);
		$.post(url, data).done(doneFunc);
	}

	function getLoginDetails() {
		console.log("requesting currentUser");
		ajaxGet('/api/public/user/current/', function (data) {
			console.log("data", data);
			var loginDetails = viewModel.loginDetails;
			if (data) {
				console.log("Performing mapping");
				ko.mapping.fromJS(data.user, {}, loginDetails);
				loginDetails.loggedIn(true);
				loginDetails.fullyAuthenticated(data.fullyAuthenticated);
			} else {
				loginDetails.userName(undefined);
				loginDetails.displayName(undefined);
				loginDetails.email(undefined);
				loginDetails.type(undefined);
				loginDetails.state(undefined);
				loginDetails.twoFactorType(undefined);
				loginDetails.created(undefined);
				loginDetails.lastLogin(undefined);
				loginDetails.loggedIn(false);
				loginDetails.fullyAuthenticated(false);
			}
		});
	}

	viewModel.prepareLoginFlow = function () {
		viewModel.loginFields.username('');
		viewModel.loginFields.password('');
		viewModel.otpFields.otp('');
		viewModel.pageSubFlow('password');
		viewModel.pageFlow('login');
	};

	viewModel.logout = function () {
		ajaxPost('/api/public/logout', '', function (data, xhr) {
			console.log("data", data, xhr);
			getLoginDetails();
		});
		viewModel.pageFlow('main');
	};

	viewModel.loginCancel = function () {
		if (viewModel.loginDetails.loggedIn()) {
			viewModel.logout();
		}
		viewModel.pageFlow('main');
	}

	viewModel.doPasswordLogin = function () {
		var logindata = ko.mapping.toJS(viewModel.loginFields);
		console.log("Posting data", logindata);
		ajaxPost('/api/public/login/login', logindata, function (data, xhr) {
			console.log("data", data, xhr);
			if (data) {
				if (data.fullyAuthenticated) {
					viewModel.pageFlow('main');
				} else {
					viewModel.pageSubFlow('totp');
				}
			}
			getLoginDetails();
		});
	};

	viewModel.doTotpLogin = function () {
		var otp = viewModel.otpFields.otp();
		console.log("Posting data", otp);
		ajaxPost('/api/public/login/totp', otp, function (data, xhr) {
			console.log("data", data, xhr);
			viewModel.pageFlow('main');
			viewModel.otpFields.otp('');
			getLoginDetails();
		});
	};

	$(document).ready(function () {
		ko.applyBindings(viewModel);
		getLoginDetails();
		//viewModel.pageFlow('login');
	});


}(window.jQuery, window.ko));
