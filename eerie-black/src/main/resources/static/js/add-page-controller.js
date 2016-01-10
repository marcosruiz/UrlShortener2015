var TIMER_DURATION = 5;

function Timer (duration, onFinish, onChange) {
    this.duration = duration;
    this.onFinish = onFinish;
    this.onChange = onChange;
}
Timer.prototype.start = function() {
    var self = this;
    var update = function() {
        if (self.duration > 0) {
            self.duration--;
            if (self.onChange != null) self.onChange(self);
            setTimeout(update, 1000);
        } else self.onFinish(self);
    };
    update();
};

$(document).ready(function() {

    var timer = new Timer(
        TIMER_DURATION,
        function() {
            $("#skip-ad").html("Skip this ad");
            $("#skip-ad").removeClass("disabled");
        },
        function(timer) {
            $("#countdown").html(timer.duration + 1);
        }
    );
    timer.start();

});

