
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

	function emptyString(s) {
		return !s || s.trim() === "";
	}

	var viewModel = {
		// Which parts of the flow to display ('main', 'login', 'userInfo', 'attachGoogle')
		pageFlow: ko.observable('main'),
		pageSubFlow: ko.observable(),

		formValidation: ko.observable(false),

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
		}
	};

	viewModel.loginPage = {
		loginFields: {
			username: ko.observable(),
			password: ko.observable()
		},
		otpFields: {
			otp: ko.observable()
		}
	};

	viewModel.loginPage.userNameValidates = ko.computed(function () {
		return !viewModel.formValidation() || !emptyString(viewModel.loginPage.loginFields.username());
	});
	viewModel.loginPage.passwordValidates = ko.computed(function () {
		return !viewModel.formValidation() || !emptyString(viewModel.loginPage.loginFields.password());
	});


	function ajaxGet(url, doneFunc) {
		console.log('GET ' + url);
		return $.getJSON(url).done(doneFunc);
	}

	function ajaxPost(url, data, doneFunc) {
		console.log('POST ' + url);
		return $.post(url, data).done(doneFunc);
	}

	function getLoginDetails() {
		console.log("requesting currentUser");
		ajaxGet('/api/public/user/current/', function (data) {
			console.log("data", data);
			var loginDetails = viewModel.loginDetails;
			if (data && data.user) {
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
		viewModel.loginPage.loginFields.username('');
		viewModel.loginPage.loginFields.password('');
		viewModel.loginPage.otpFields.otp('');
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
		viewModel.formValidation(true);
		var l = $('#loginButton').ladda();
		l.ladda('start');
		var logindata = ko.mapping.toJS(viewModel.loginPage.loginFields);
		console.log("Posting data", logindata);
		ajaxPost('/api/public/login/login', logindata, function (data, xhr) {
			console.log("data", data, xhr);
			if (data) {
				viewModel.loginPage.loginFields.username('');
				viewModel.loginPage.loginFields.password('');
				if (data.fullyAuthenticated) {
					viewModel.pageFlow('main');
				} else {
					viewModel.pageSubFlow('totp');
				}
			}
			getLoginDetails();
		}).always(function () { l.ladda('stop'); });
	};

	viewModel.doTotpLogin = function () {
		viewModel.formValidation(true);
		var otp = viewModel.loginPage.otpFields.otp();
		console.log("Posting data", otp);
		ajaxPost('/api/public/login/totp', otp, function (data, xhr) {
			console.log("data", data, xhr);
			viewModel.pageFlow('main');
			viewModel.loginPage.otpFields.otp('');
			getLoginDetails();
		});
	};

	$(document).ready(function () {
		ko.applyBindings(viewModel);
		getLoginDetails();
		//viewModel.pageFlow('login');
	});


}(window.jQuery, window.ko));
