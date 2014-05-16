

define(['stomp/stomp'], function(stomp) {
	
	var module = {}, subs = [], client = null, connected = false;
	
	function connect(callback, errback) {
		if( !connected ) {
			client = Stomp.over(new SockJS('/stomp'));
			client.connect('test', 'user', function () {
				 connected = true;
				 if( typeof callback === 'function' ) {
					 callback();
				 }
			 },
			 function (error) {
				 if ( typeof errback === 'function' ) {
					 errback(error);
				 }
			 });
		} else {
			setTimeout(function () {
				if( typeof callback === 'function' ) {
					 callback();
				 }
			}, 0);
		}
	};
	
	function then(callback, errback) {
		connect(function () {
			if( typeof callback === 'function' ) {
				callback(module);
			}
		}, 
		function (error) {
			 if ( typeof errback === 'function' ) {
				 errback(error);
			 }
		});
	};
	
	module.then = then;
	
	function clock(timezone, callback, errback) {
		connect(function () {
			subs.push(client.subscribe('/app/clock/' + timezone, function (msg) {
				if( typeof callback === 'function' ) {
					callback(msg);
					client.subscribe('/topic/clock/' + timezone, function (msg) {
						callback(msg);
					});
				}
			}));
		},
		function () {
			if ( typeof errback === 'function' ) {
				errback(error);
			}
		});
	};
	
	module.clock = clock;
		
	window.onbeforeunload = function (event) {
		client.disconnect();
	};
	
	return module;
});
