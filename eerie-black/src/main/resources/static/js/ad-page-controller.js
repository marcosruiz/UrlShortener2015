$(document).ready(function() {

    var TIMER_DURATION = 5;

    var button = $("#skip-ad");
    var countdown = $("#countdown");
    var hash = window.location.search.slice(window.location.search.indexOf("=") + 1); //FIXME This shouldn't be hardcoded!

    // Request token on document load
    $.ajax({
        type: 'GET',
        url: '/' + hash + '/key',
        success: function(msg) { // If succeeded, set a countdown of five seconds and then request the real URI
            var token = msg.key;
            var remainingTimeouts = TIMER_DURATION;
            var startTimeout = function() {
                if (remainingTimeouts > 0) {
                    countdown.html(remainingTimeouts);
                    remainingTimeouts--;
                    setTimeout(startTimeout, 1000);
                } else requestRealUri(token);
            };
            startTimeout();
        },
        error: function() {
            button.html("Something bad happened :(");
        }
    });

    // Request real uri
    var requestRealUri = function(token) {
        console.log(token);
        button.html("Loading...");
        $.ajax({
            type: 'GET',
            url: '/' + hash + '/realTarget',
            data: { key: token },
            success: function(msg) {
                button.href = msg.target;
                button.html("Skip this ad");
                button.removeClass("disabled");
            },
            error: function() {
                button.html("Something bad happened :(");
            }
        });
    };

});

