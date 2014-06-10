

define(['stomp/stomp'], function(stomp) {
	
	var module = {}, subs = [], client = null, connected = false, connecting = false, conpending = [];
	
	function connect(callback, errback) {
		var idx;
		if( !connected ) {
			conpending.push([callback, errback]);
			if( !connecting ) {
				connecting = true;
				client = Stomp.client('ws://'+window.location.host+'/clock/stomp');
				client.connect('test', 'user', function () {
					connected = true;
					connecting = false;
					for(idx=0; idx<conpending.length; idx++) {
						if( typeof conpending[idx][0] === 'function' ) {
							conpending[idx][0]();
						}
					}
					conpending = [];
				},
				function (error) {
					connecting = false;
					for(idx=0; idx<conpending.length; idx++) {
						if ( typeof conpending[idx][1] === 'function' ) {
							conpending[idx][1](error);
						}
					}
					conpending = [];
				});
			}
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
	
	function clock(callback, errback) {
		connect(function () {
			subs.push(client.subscribe('/app/clock', function (msg) {
				if( typeof callback === 'function' ) {
					callback(msg);
					client.subscribe('/topic/clock', function (msg) {
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
