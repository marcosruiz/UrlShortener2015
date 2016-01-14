$(document).ready(
    function() {

        var header = {};
        var token = localStorage.getItem("bearerToken");
        var username = localStorage.getItem("username");
        var premium = localStorage.getItem("premium") == "true";
        if (premium) $("#premium-field").html(
            "<div class='checkbox'><label><input type='checkbox' id='sponsor'> No ads!</label></div>");
        if (token) {
            $("#log-out-warn").html(
                "You are logged in as " + username + ". <a href='#' onclick='logout()'>Log out</a>");
            header = { "Authorization": "Bearer " + token };
        }
        else $("#log-out-warn").html(
                "<a href='/login.html'>Log in</a> with a premium account in order to create links without ads!");

        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/link",
                    headers: header,
                    // data : $(this).serialize(),
                    data : {
                        url: $("#url").val(),
                        brand: $("#brand").val(),
                        sponsor: $("#sponsor").val() ? "no" : "yes"
                    },
                    success : function(msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>");
                    },
                    error : function() {
                        $("#result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });

function logout() {
    localStorage.removeItem("bearerToken");
    localStorage.removeItem("username");
    localStorage.removeItem("premium");
    window.location.replace("../login.html");
}