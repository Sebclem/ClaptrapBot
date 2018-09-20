var nav_bar_account_link;
var connected_link = "<a class=\"dropdown-account dropdown-trigger\" data-target=\"dropdown_connected\"><i class=\"material-icons green-text\">account_box</i></a>";
var disconnected_link = "<a class=\"waves-effect waves-light modal-trigger\" href=\"#modal_connection\"><i class=\"material-icons red-text\">account_box</i></a>";
var input_name;
var input_psw;
var btn_submit;
var btn_disconnect;
var nav_name;




$(document).ready(function() {
    $('.tooltipped').tooltip();
    $('#nav-mobile').sidenav({
        menuWidth: 400, // Default is 300
        edge: 'left', // Choose the horizontal origin
        closeOnClick: false, // Closes side-nav on <a> clicks, useful for Angular/Meteor
        draggable: true // Choose whether you can drag to open on touch screens,
    });

    $('#modal_guild').modal({
        dismissible: false // Modal can be dismissed by clicking outside of the modal
    });

    $('#modal_internet').modal({
        dismissible: false // Modal can be dismissed by clicking outside of the modal
    });





    nav_bar_account_link = $("#nav-bar-account");
    input_name = $("#user_input");
    input_psw = $("#password_input");
    btn_submit = $("#btn-submit-connect");
    btn_disconnect = $(".nav-disconnect");
    nav_name = $("#nav-name");
    navListeners();
    if(Cookies.get('token') === undefined){
        disconnected()
    }
    else{
        connected();
    }

    checkConnection();


});


function popOutSubmit(){
    if (btn_submit.hasClass("scale-in")) {
        btn_submit.removeClass("scale-in");
        btn_submit.addClass("scale-out");
    }
}

function popInSubmit(){
    if (btn_submit.hasClass("scale-out")) {
        btn_submit.removeClass("scale-out");
        btn_submit.addClass("scale-in");
    }
}



function connected(){
    console.log("Connected!");
    nav_bar_account_link.html(connected_link);
    $('.dropdown-account').dropdown({
            constrainWidth: false, // Does not change width of dropdown to that of the activator
            coverTrigger: false, // Displays dropdown below the button
            alignment: 'left', // Displays dropdown with edge aligned to the left of button
            stopPropagation: false // Stops event propagation
        }
    );
    nav_name.text(Cookies.get('name'));
    if (typeof needLogin !== 'undefined') {
        if (Cookies.get('guild') === undefined) {
            getGuild()
        }
    }
}

function disconnected() {
    console.log("Disconnected");
    nav_bar_account_link.html(disconnected_link);
    var modalConnection = $('#modal_connection');
    modalConnection.modal({
        dismissible: false // Modal can be dismissed by clicking outside of the modal
    });
    if (typeof needLogin !== 'undefined'){
        modalConnection.modal('open');
    }

}


function tryConnection() {
    var request = { name: input_name.val(), password: input_psw.val()};
    $.ajax({
        type: "POST",
        dataType: 'json',
        contentType: 'application/json',
        url: "/api/userManagement/requestToken",
        data:  JSON.stringify(request),
        success: function (data) {
            console.log(data);
            Cookies.set('token',data.token, { expires: 31 });
            Cookies.set('name', data.name, { expires: 31 });
            debugger;
            location.reload();
        }

    }).fail(function (data) {
        console.log(data);
        switch(data.responseJSON.error){
            case "user":
                input_name.addClass("invalid");
                break;

            case "password":
                input_psw.addClass("invalid");
                break;
        }

    });


    return true;
}


function navListeners() {
    input_name.on("input", function () {
        if(input_name.val() !== "" && input_psw.val() !== "") {
            popInSubmit();
        }else
        {
            popOutSubmit();
        }
    });

    input_psw.on("input", function () {
        if(input_name.val() !== "" && input_psw.val() !== "") {
            popInSubmit();
        }else
        {
            popOutSubmit();
        }
    });

    btn_disconnect.click(function () {
       Cookies.remove('token');
       Cookies.remove('name');
       Cookies.remove('guild');
       location.reload();
    });

    $('#guild_form').change(function () {
        if ($('#btn_ok_guild').hasClass("disabled")) {
            $('#btn_ok_guild').removeClass("disabled");
        }
    });

    $('#btn_ok_guild').click(function () {
        guild = $('input[name=guildRadio]:checked').val();
        Cookies.set('guild', guild, { expires: 31 });
        location.reload();

    });

    $('.nav-change-guild').click(function () {
        Cookies.remove('guild');
        location.reload();
    })
}

function getGuild(){
    $.get("api/userManagement/getGuilds", function (data) {
    }).done(function (data) {
        console.log(data);
        $('#guild_form').empty();

        if(data.length === 1){
            Cookies.set('guild', data[0].id, { expires: 31 });
            return;
        }
        data.forEach(function(element){
            var template = $('#radioTemplateGuild').clone();
            template.removeAttr("id");
            template.removeAttr("style");
            var content = template.html();
            content = content.replace("@name", element.name);
            content = content.replace(/@id/g, element.id);
            template.html(content);

            $('#guild_form').append(template);
        });
        $('#modal_guild').modal('open');

    }).fail(function (data) {
        if(!error){
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
        }

    }).fail(function (data) {
        console.error("Connection fail!");
        $('#modal_internet').modal('open');

    });
}