var input_name;
var input_psw;
var btn_submit;


$(document).ready(() => {

    $("#login_form").submit(function (e) {
        e.preventDefault();
        tryConnection();
    });


    input_name = $("#user_input");
    input_psw = $("#password_input");
    btn_submit = $("#btn-submit-connect");

    input_name.on("input", function () {
        if (input_name.val() !== "" && input_psw.val() !== "") {
            popInSubmit();
        } else {
            popOutSubmit();
        }
    });

    input_psw.on("input", function () {
        if (input_name.val() !== "" && input_psw.val() !== "") {
            popInSubmit();
        } else {
            popOutSubmit();
        }
    });


});


function popOutSubmit() {
    if (btn_submit.hasClass("scale-in")) {
        btn_submit.removeClass("scale-in");
        btn_submit.addClass("scale-out");
    }
}

function popInSubmit() {
    if (btn_submit.hasClass("scale-out")) {
        btn_submit.removeClass("scale-out");
        btn_submit.addClass("scale-in");
    }
}


function tryConnection() {

    var request = {name: input_name.val(), password: input_psw.val()};
    $.ajax({
        type: "POST",
        dataType: 'json',
        contentType: 'application/json',
        url: "/api/userManagement/requestToken",
        data: JSON.stringify(request),
        success: function (data) {
            console.log(data);
            Cookies.set('token', data.token, {expires: 31});
            Cookies.set('name', data.name, {expires: 31});
            window.location.reload(true);
        }

    }).fail(function (data) {
        console.log(data);
        switch (data.responseJSON.error) {
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