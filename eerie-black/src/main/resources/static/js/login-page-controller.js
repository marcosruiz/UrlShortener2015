$(document).ready(
    function() {
        $("#login").submit(
            function(event) {
                event.preventDefault();
                var username = $('#log-user').val();
                var password = $('#log-password').val();
                $.ajax({
                    type : "POST",
                    url : "/auth",
                    headers : {
                        'Authorization': 'Basic ' + btoa(username + ':' + password)
                    },
                    success : function(msg) {
                        console.log("Success!");
                        localStorage.setItem("bearerToken", msg.key);
                        localStorage.setItem("username", msg.username);
                        localStorage.setItem("premium", msg.premium);
                        window.location.replace("../index.html"); //Redirect
                    },
                    error : function() {
                        $("#log-result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });

        $("#signup").submit(
            function(event) {
                event.preventDefault();
                var username = $('#reg-user').val();
                var password = $('#reg-password').val();
                var premium = $('#reg-premium').val();
                $.ajax({
                    type : "POST",
                    url : "/users",
                    data: {
                        "username": username,
                        "password": password,
                        "premium": premium
                    },
                    success : function(msg) {
                        $("#reg-result").html(
                            "<div class='alert alert-success lead'>SUCCESS!</div>");
                    },
                    error : function() {
                        $("#reg-result").html(
                            "<div class='alert alert-danger lead'>ERROR!</div>");
                    }
                });
            });
    });