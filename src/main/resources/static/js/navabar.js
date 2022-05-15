var nav_bar_account_link;
var connected_link = "<a class=\"dropdown-account dropdown-trigger\" data-target=\"dropdown_connected\"><i class=\"material-icons green-text\">account_box</i></a>";
var disconnected_link = "<a class=\"waves-effect waves-light modal-trigger\" href=\"#modal_connection\"><i class=\"material-icons red-text\">account_box</i></a>";
var btn_disconnect;
var nav_name;


$(document).ready(function () {
    $('.tooltipped').tooltip();

    $('#modal_guild').modal({
        dismissible: false // Modal can be dismissed by clicking outside of the modal
    });

    $('#modal_internet').modal({
        dismissible: false // Modal can be dismissed by clicking outside of the modal
    });


    nav_bar_account_link = $("#nav-bar-account");
    btn_disconnect = $(".nav-disconnect");
    nav_name = $("#nav-name");
    navListeners();


    checkConnection();


});


function connected() {
    console.log("Connected!");
    console.log("Checking token...");
    console.log(window.location.href);
    if (!window.location.href.includes("oauthCallback")) {
        checkToken();
    } else {
        console.log("Oauth page skip check token");
    }


}

function disconnected() {
    console.log("Disconnected");
    nav_bar_account_link.html(disconnected_link);
    var modalConnection = $('#modal_connection');
    modalConnection.modal({
        dismissible: false // Modal can be dismissed by clicking outside of the modal
    });

}


function navListeners() {

    btn_disconnect.click(function () {
        Cookies.remove('token');
        Cookies.remove('name');
        Cookies.remove('guild');
        window.location.reload(true);
    });

    $('#guild_form').change(function () {
        if ($('#btn_ok_guild').hasClass("disabled")) {
            $('#btn_ok_guild').removeClass("disabled");
        }
    });

    $('#btn_ok_guild').click(function () {
        guild = $('input[name=guildRadio]:checked').val();
        Cookies.set('guild', guild, {expires: 31});
        window.location.reload(true);
    });

    $('.guild_change').click(function () {
        let id = this.getAttribute("data-id");
        Cookies.set('guild', id, {expires: 31});
        window.location.reload(true);
    });

    $('.nav-change-guild').click(function () {
        Cookies.remove('guild');
        window.location.reload(true);
    });

}

function getGuild() {
    $.get("api/userManagement/getGuilds", function (data) {
    }).done(function (data) {
        console.log(data);
        $('#guild_form').empty();
        if (data.length === 0 && location.pathname !== "/")
            window.location.replace("/");
        if (data.length === 0) {
            return;
        } else if (data.length === 1) {
            Cookies.set('guild', data[0].id, {expires: 31});
            window.location.reload(true);
        }
        data.forEach(function (element) {
            var template = $('#radioTemplateGuild').clone();
            template.removeAttr("id");
            template.removeAttr("style");
            var content = template.html();
            content = content.replace("@name", element.name);
            content = content.replace(/@id/g, element.id);
            content = content.replace(/@url/g, element.imageUrl == null ? "https://discordapp.com/assets/dd4dbc0016779df1378e7812eabaa04d.png" : element.imageUrl);
            template.html(content);

            $('#guild_form').append(template);
        });
        $('#modal_guild').modal('open');

    }).fail(function (data) {
        if (!error) {
            alert("Com error, please refresh.");
            error = true;
        }

    });
}


function checkConnection() {
    $.ajax({
        type: "GET",
        url: "/api/isReady",
        success: function (data) {
            console.log("Connection Ok");
            console.log(Cookies.get('token'));

            if (Cookies.get('token') === undefined) {
                disconnected()
            } else {
                connected();
            }

        }

    }).fail(function (data) {
        console.error("Connection fail!");
        $('#modal_internet').modal('open');

    });
}


function checkToken() {
    $.ajax({
        type: "GET",
        url: "/api/userManagement/checkToken",
        success: function (data) {
            console.debug("...token is valid.");
            nav_bar_account_link.html(connected_link);
            $('.dropdown-account').dropdown({
                    constrainWidth: false, // Does not change width of dropdown to that of the activator
                    coverTrigger: false, // Displays dropdown below the button
                    alignment: 'left', // Displays dropdown with edge aligned to the left of button
                    stopPropagation: false // Stops event propagation
                }
            );
            nav_name.text(Cookies.get('name'));
            $('#nav-mobile').sidenav({
                menuWidth: 400, // Default is 300
                edge: 'left', // Choose the horizontal origin
                closeOnClick: false, // Closes side-nav on <a> clicks, useful for Angular/Meteor
                draggable: true // Choose whether you can drag to open on touch screens,
            });
            if (Cookies.get('guild') === undefined) {
                getGuild()
            } else {
                $('#drop-trigger-guilds').dropdown({
                        constrainWidth: false, // Does not change width of dropdown to that of the activator
                        coverTrigger: false, // Displays dropdown below the button
                        alignment: 'left', // Displays dropdown with edge aligned to the left of button
                        stopPropagation: false // Stops event propagation
                    }
                );
            }

        }

    }).fail(function (data) {
        console.error("...token is invalid !");
        console.log(data);

        Cookies.remove('token');
        Cookies.remove('name');
        Cookies.remove('guild');
        window.location.reload(true);

    });


}