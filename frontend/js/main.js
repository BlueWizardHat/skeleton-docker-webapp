
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


// knockout extensions
(function (ko) {
	'use strict';

	ko.bindingHandlers.fadeVisible = {
		init: function(element, valueAccessor) {
			// Initially set the element to be instantly visible/hidden depending on the value
			var value = valueAccessor();
			$(element).toggle(ko.unwrap(value)); // Use "unwrapObservable" so we can handle values that may or may not be observable
		},
		update: function(element, valueAccessor) {
			// Whenever the value subsequently changes, slowly fade the element in or out
			var value = valueAccessor();
			ko.unwrap(value) ? $(element).fadeIn() : $(element).fadeOut();
		}
	};

	ko.bindingHandlers.slideVisible = {
		init: function(element, valueAccessor) {
			// Initially set the element to be instantly visible/hidden depending on the value
			var value = valueAccessor();
			$(element).slideToggle(ko.unwrap(value)); // Use "unwrapObservable" so we can handle values that may or may not be observable
		},
		update: function(element, valueAccessor) {
			// Whenever the value subsequently changes, slowly fade the element in or out
			var value = valueAccessor();
			ko.unwrap(value) ? $(element).slideDown() : $(element).slideUp();
		}
	};

}(window.ko));


// Main functionality
(function ($, ko) {
	'use strict';

	function emptyString(s) {
		return !s || s.trim() === "";
	}

	var pages = [ '#loginPage', '#welcomePage', '#userInfoPage' ];
	var currentPage = '#welcomePage';

	var viewModel = {
		pageFlow: ko.observable('#welcomePage'),
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

	viewModel.pageFlow.subscribe(function (newPage) {
		if (newPage !== currentPage) {
			$(currentPage).hide('fade', {}, 250, function () {
				$(newPage).show('fade', {}, 250);
			});
			currentPage = newPage;
		}
	});

	viewModel.loginPage.userNameValidates = ko.computed(function () {
		return !viewModel.formValidation() || !emptyString(viewModel.loginPage.loginFields.username());
	});
	viewModel.loginPage.passwordValidates = ko.computed(function () {
		return !viewModel.formValidation() || !emptyString(viewModel.loginPage.loginFields.password());
	});
	viewModel.loginPage.totpValidates = ko.computed(function () {
		return !viewModel.formValidation() || !emptyString(viewModel.loginPage.otpFields.otp());
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
			var loginDetails = viewModel.loginDetails;
			console.log("data", data);
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
		viewModel.pageFlow('#loginPage');
	};

	viewModel.logout = function () {
		ajaxPost('/api/public/logout', '', function (data, xhr) {
			console.log("data", data, xhr);
			getLoginDetails();
		});
		viewModel.pageFlow('#welcomePage');
	};

	viewModel.loginCancel = function () {
		if (viewModel.loginDetails.loggedIn()) {
			viewModel.logout();
		}
		viewModel.pageFlow('#welcomePage');
	}

	viewModel.doPasswordLogin = function () {
		var l = $('#loginButton').ladda();
		var logindata = ko.mapping.toJS(viewModel.loginPage.loginFields);
		viewModel.formValidation(true);
		l.ladda('start');
		console.log("Posting data", logindata);
		ajaxPost('/api/public/login/login', logindata, function (data, xhr) {
			console.log("data", data, xhr);
			if (data) {
				viewModel.loginPage.loginFields.username('');
				viewModel.loginPage.loginFields.password('');
				if (data.fullyAuthenticated) {
					viewModel.pageFlow('#welcomePage');
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
			viewModel.pageFlow('#welcomePage');
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
