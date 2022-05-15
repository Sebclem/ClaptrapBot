var hash = window.location.hash.replace("#", "").split("&");
var discordToken = "";

debugger;

hash.forEach(function (value) {
    if (value.indexOf("access_token") !== -1) {
        discordToken = value.split("=")[1];
        return 0;
    }
});
if (discordToken !== "") {
    console.log(discordToken);
    $.ajax({
        type: "POST",
        dataType: 'json',
        contentType: 'application/json',
        url: "/api/userManagement/oauthLogin?token=" + discordToken,
        success: function (data) {
            console.log(data);
            Cookies.set('token', data.token, {expires: 31});
            Cookies.set('name', data.name, {expires: 31});
            window.location = "/";
        }

    }).fail(function (data) {
        console.log(data);
    });
} else {
    window.location = "/";
}
