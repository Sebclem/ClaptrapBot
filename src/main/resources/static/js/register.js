
var ok_passwrd = false;
$(document).ready(function() {
    var baseUrl = window.location.protocol + "//" +window.location.host + window.location.pathname;
    console.log(baseUrl);
    $('.button-collapse-1').sideNav({
        menuWidth: 400, // Default is 300
        edge: 'right', // Choose the horizontal origin
        closeOnClick: false, // Closes side-nav on <a> clicks, useful for Angular/Meteor
        draggable: true // Choose whether you can drag to open on touch screens,
    });

    var sendBtn = $('#sendBtn');


    $('#name').on("input", function () {
        if($('#name').val() === ""){
            if (!sendBtn.hasClass("disabled")) {
                sendBtn.addClass("disabled");
            }
        }
        else{
            if (sendBtn.hasClass("disabled") && ok_passwrd) {
                sendBtn.removeClass("disabled");
            }
        }
    });


    var passwrd = $('#passwrd');
    var confirm = $('#passwrd2');
    passwrd.on("input", function () {
        if((passwrd.val() === confirm.val())&& passwrd.val() !== ''){
            if (passwrd.hasClass("invalid")) {
                passwrd.addClass("valid");
                passwrd.removeClass("invalid");
                confirm.addClass("valid");
                confirm.removeClass("invalid");


            }
            if($('#name').val() !== ""){
                if (sendBtn.hasClass("disabled")) {
                    sendBtn.removeClass("disabled");
                }
            }
            ok_passwrd = true;
        }
        else{
            if (!passwrd.hasClass("invalid")) {
                passwrd.addClass("invalid");
                passwrd.removeClass("valid");
                confirm.addClass("invalid");
                confirm.removeClass("valid");

            }
            if (!sendBtn.hasClass("disabled")) {
                sendBtn.addClass("disabled");
            }
            ok_passwrd = false;
        }
    });

    confirm.on("input", function () {
        if((passwrd.val() === confirm.val())&& passwrd.val() !== ''){
            if (passwrd.hasClass("invalid")) {
                passwrd.addClass("valid");
                passwrd.removeClass("invalid");
                confirm.addClass("valid");
                confirm.removeClass("invalid");

            }
            if($('#name').val() !== ""){
                if (sendBtn.hasClass("disabled")) {
                    sendBtn.removeClass("disabled");
                }
            }
            ok_passwrd = true;
        }
        else{
            if (!passwrd.hasClass("invalid")) {
                passwrd.addClass("invalid");
                passwrd.removeClass("valid");
                confirm.addClass("invalid");
                confirm.removeClass("valid");
            }

            if (!sendBtn.hasClass("disabled")) {
                sendBtn.addClass("disabled");
            }
            ok_passwrd = false;
        }
    });


    $('#sendBtn').click(function () {
        var name = $('#name').val();
        var password = $('#passwrd').val();

        $.ajax({
            type: "POST",
            dataType: 'json',
            contentType: 'application/json',
            url: "/api/userManagement/preRegister",
            data:  JSON.stringify({ name: name, password: password}),
            success: function (data) {
                console.log(data);
                window.location.href = baseUrl + "?id="+data.id
            }

        }).fail(function (data) {
            console.log(data);
            if(data.status === 404){
                alert("User Not Found!");
                $('#name').addClass("invalid");
                $('#name').removeClass("valid");

            }
            else{
                alert(data.responseJSON.message);
            }

        });
    });

    $('#modalToken').modal({dismissible: false});
    if(id !== ''){
        $('#modalToken').modal('open');
    }


    $('#input_preToken').on("input", function () {
        var sendBtn = $('#preTokenSend');
        if($('#input_preToken').val().length < 4){
            if (!sendBtn.hasClass("disabled")) {
                sendBtn.addClass("disabled");
            }
        }
        else{
            if (sendBtn.hasClass("disabled")) {
                sendBtn.removeClass("disabled");
            }
        }
    });
    $('#preTokenSend').click(function () {
        $.ajax({
            type: "POST",
            dataType: 'json',
            contentType: 'application/json',
            url: "/api/userManagement/confirmAccount",
            data:  JSON.stringify({ id: id.toString(), checkToken: $('#input_preToken').val()}),
            success: function (data) {
                console.log(data);
                alert("Connection ok!")
            }

        }).fail(function (data) {
            console.log(data);
            alert(data.responseJSON.message);

        });
    });


});
