$(document).ready(function() {
    $.get("/authentications/user-info", function(data) {
        $("#menuUsername").text(data.username); // Exibe no menu
    });

    $("#logoutLink").on("click", function (e) {
        e.preventDefault();

        $.ajax({
            url: "/authentications/logout",
            type: "POST",
            success: function () {
                window.location.href = "/login"
            },
            error: function () {
                alert("Erro ao tentar fazer logout.");
            }
        });
    });
});