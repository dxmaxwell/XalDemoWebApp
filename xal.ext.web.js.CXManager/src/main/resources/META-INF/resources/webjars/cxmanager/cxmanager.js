

define([ 'stomp/stomp' ], function(stomp) {
	
	var module = {}, subs = [], client = null, connected = false, connecting = false, connpending = [], connidentity = 0;
		
	
	function connect(callback, errback) {
		var client, idx;
		if( !connected ) {
			connpending.push({ cb:callback, eb:errback });
			if( !connecting ) {
				connecting = true;
				client = Stomp.client('ws://'+window.location.host+'/cxm/stomp');
				client.connect('test', 'user', function () {
					connected = true;
					connecting = false;
					for(idx=0; idx<connpending.length; idx++) {
						if( typeof connpending[idx].cb === 'function' ) {
							connpending[idx].cb(client);
						}
					}
					connpending = [];
				},
				function (error) {
					connecting = false;
					for(idx=0; idx<connpending.length; idx++) {
						if ( typeof connpending[idx].eb === 'function' ) {
							connpending[idx].eb(error);
						}
					}
					connpending = [];
				});
			}
		} else {
			setTimeout(function () {
				if( typeof callback === 'function' ) {
					 callback(client);
				 }
			}, 0);
		}
	};
	
	function read(uri, callback) {
		connect(
		  function (client) {
			  var connid = connidentity++;
			  client.subscribe('/user/topic/data/CH'+connid, function (msg) {
				  if( typeof callback === 'function' ) {
					  if( msg.body ) {
						  callback(JSON.parse(msg.body));
					  }
				  }
			  });
			  client.send('/app/read/CH'+connid, {}, JSON.stringify({ uri: uri })); 
		  },
		  function (error) {
			  console.debug(error);
		  }
		);
	};
	
	module.read = read;
	
	return module;
});