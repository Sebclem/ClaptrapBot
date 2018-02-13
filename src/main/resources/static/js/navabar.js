var nav_bar_account_link;
var connected_link = "<a class=\"dropdown-account\" data-activates=\"dropdown_connected\"><i class=\"material-icons green-text\">account_box</i></a>";
var disconnected_link = "<a class=\"waves-effect waves-light modal-trigger\" href=\".modal_connection\"><i class=\"material-icons red-text\">account_box</i></a>";
var input_name;
var input_psw;
var btn_submit;
var btn_disconnect;
var nav_name;

$(document).ready(function() {
    $('.button-navbar-mobile').sideNav({
        menuWidth: 400, // Default is 300
        edge: 'right', // Choose the horizontal origin
        closeOnClick: false, // Closes side-nav on <a> clicks, useful for Angular/Meteor
        draggable: true // Choose whether you can drag to open on touch screens,
    });


    nav_bar_account_link = $("#nav-bar-account");
    input_name = $("#user_input");
    input_psw = $("#password_input");
    btn_submit = $("#btn-submit-connect");
    btn_disconnect = $("#nav-disconnect");
    nav_name = $("#nav-name");

    if(Cookies.get('token') === undefined){
        disconnected()
    }
    else{
        connected();
    }

    listeners();
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
            belowOrigin: true, // Displays dropdown below the button
            alignment: 'left', // Displays dropdown with edge aligned to the left of button
            stopPropagation: false // Stops event propagation
        }
    );
    nav_name.text(Cookies.get('name'));
}

function disconnected() {
    console.log("Disconnected");
    nav_bar_account_link.html(disconnected_link);
    $('.modal').modal();

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
            Cookies.set('token',data.token);
            Cookies.set('name', data.name);
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


function listeners() {
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
       location.reload();
    });

}