
var ok_passwrd = false;
$(document).ready(function() {
    var baseUrl = window.location.protocol + "//" +window.location.host + window.location.pathname;
    console.log(baseUrl);
    
    var sendBtn = $('#sendBtn');


    $('#name').on("input", function () {
        if($('#name').val() === ""){
            if (sendBtn.hasClass("scale-in")) {
                sendBtn.removeClass("scale-in");
                sendBtn.addClass("scale-out");
            }
        }
        else{
            if (sendBtn.hasClass("scale-out") && ok_passwrd) {
                sendBtn.removeClass("scale-out");
                sendBtn.addClass("scale-in");
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
                if (sendBtn.hasClass("scale-out")) {
                    sendBtn.removeClass("scale-out");
                    sendBtn.addClass("scale-in");
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
            if (sendBtn.hasClass("scale-in")) {
                sendBtn.removeClass("scale-in");
                sendBtn.addClass("scale-out");
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
                if (sendBtn.hasClass("scale-out")) {
                    sendBtn.removeClass("scale-out");
                    sendBtn.addClass("scale-in");
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

            if (sendBtn.hasClass("scale-in")) {
                sendBtn.removeClass("scale-in");
                sendBtn.addClass("scale-out");
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
            if (sendBtn.hasClass("scale-in")) {
                sendBtn.removeClass("scale-in");
                sendBtn.addClass("scale-out");
            }
        }
        else{
            if (sendBtn.hasClass("scale-out")) {
                sendBtn.removeClass("scale-out");
                sendBtn.addClass("scale-in");
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
                Cookies.set('token',data.token);
                Cookies.set('name', data.name);
                debugger;
                window.location.href = "/"
            }

        }).fail(function (data) {
            console.log(data);
            alert(data.responseJSON.message);

        });
    });


});
