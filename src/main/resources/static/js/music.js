var savedPlaylist;
var error = false;
var state;
var disconected = false;
var modal_loading;
var btn_play;
var btn_stop;
var btn_next;
var btn_info;
var btn_disconnect;
var btn_flush;
var btn_add;
var switchAutoFlow;
var loadingFlag = false;
var guild;

$(document).ready(function() {
    if(Cookies.get('guild') != undefined) {

        guild = Cookies.get('guild')
        btn_play = $('#btn_play');
        btn_stop = $('#btn_stop');
        btn_next = $('#btn_next');
        btn_info = $('#btn_info');
        btn_disconnect = $('#btn_disconnect');
        btn_flush = $('#flush_btn');
        btn_add = $('#add_btn');
        switchAutoFlow = $("#autoflow");

        setInterval("getCurentMusic()", 1000);
        $('#modalAdd').modal();

        $('#modal_current_info').modal();

        $('#modalChanels').modal({
            dismissible: false // Modal can be dismissed by clicking outside of the modal
        });

        modal_loading = $('#modal_loading');
        modal_loading.modal({
            dismissible: false
        });

        $('.button-collapse-1').sideNav({
            menuWidth: 400, // Default is 300
            edge: 'right', // Choose the horizontal origin
            closeOnClick: false, // Closes side-nav on <a> clicks, useful for Angular/Meteor
            draggable: true // Choose whether you can drag to open on touch screens,
        });

        $('.dropdown-button').dropdown({
            inDuration: 300,
            outDuration: 225,
            constrainWidth: false, // Does not change width of dropdown to that of the activator
            hover: false, // Activate on hover
            gutter: 0, // Spacing from edge
            belowOrigin: false, // Displays dropdown below the button
            alignment: 'left', // Displays dropdown with edge aligned to the left of button
            stopPropagation: false // Stops event propagation
        });

        listeners();
    }
});

function getCurentMusic() {
    $.get("api/music/currentMusicInfo?guild=" + guild, function (data) {
    }).done(function (data) {

        state = data.state;
        switch (data.state) {
            case "STOP":
                disconected = false;
                $('#modalChanels').modal('close');
                $('#music_text').text("Connected on Vocal Channel");

                if (!$('#btn_info').hasClass("indeterminate")) {
                    $('#btn_info').addClass("determinate").removeClass("indeterminate");
                }
                $('#music_progress').width("0%");
                if(Cookies.get('token') != undefined){
                    disableBtn(btn_stop);
                    disableBtn(btn_info);
                    enableBtn(btn_add);
                    enableBtn(btn_flush);
                    enableBtn(btn_play);
                    enableBtn(btn_next);
                    enableBtn(btn_disconnect);
                }
                else{
                    disableBtn(btn_play);
                    disableBtn(btn_stop);
                    disableBtn(btn_info);
                    disableBtn(btn_add);
                    disableBtn(btn_flush);
                    disableBtn(btn_next);
                    disableBtn(btn_disconnect);
                }
                btn_play.children().text("play_arrow");
                $('#music_img').attr("src","/img/no_music.jpg");
                $('#total_time').text("00:00");
                $('#current_time').text("00:00");
                btn_play.removeClass("amber");
                btn_play.addClass("green");

                break;

            case "PLAYING":
                disconected = false;
                $('#modalChanels').modal('close');
                btn_play.children().text("pause");
                btn_play.removeClass("green");
                btn_play.addClass("amber");
                updateControl(data);

                break;

            case "PAUSE":
                disconected = false;
                $('#modalChanels').modal('close');
                btn_play.children().text("play_arrow");
                btn_play.removeClass("amber");
                btn_play.addClass("green");
                btn_play.addClass("green");
                updateControl(data);
                break;

            case "LOADING":
                disconected = false;
                $('#modalChanels').modal('close');
                if (!$('#btn_info').hasClass("determinate")) {
                    $('#btn_info').addClass("indeterminate").removeClass("determinate");
                }
                break;

            case "DISCONNECTED":
                $('#music_text').text("Disconnected from Vocal");

                if (!$('#btn_info').hasClass("indeterminate")) {
                    $('#btn_info').addClass("determinate").removeClass("indeterminate");
                }
                $('#music_progress').width("0%");

                $('#btn_play').children().text("play_arrow");

                disableBtn(btn_play);
                disableBtn(btn_stop);
                disableBtn(btn_info);
                disableBtn(btn_add);
                disableBtn(btn_flush);
                disableBtn(btn_next);
                disableBtn(btn_disconnect);

                $('#music_img').attr("src","/img/disconnected.png");
                if(Cookies.get('token') != undefined){
                    if(!disconected){
                        getChannels();
                        disconected = true;
                    }
                }




                break;
        }
        if(switchAutoFlow.is(':checked') != data.autoflow)
            switchAutoFlow.prop('checked', data.autoflow);
        getPlayList();

    })
    .fail(function (data) {
        if(!error){
            alert("Connection lost, I keep trying to refresh!");
            error = true;
        }

    })
}

function getPlayList() {
    $.get("api/music/getPlaylist?guild=" + guild, function (data) {
    }).done(function (data) {
        data = data.list;
        if(data != null && data.length != 0){
            var noUpdate = comparePlaylist(data, savedPlaylist);
            // console.log("List up to date : "+noUpdate);
            if(!noUpdate){
                savedPlaylist = data;
                $('#playlist_list').empty();

                data.forEach(function(element){
                    var template = $('#playlist_template').clone();
                    template.removeAttr("id");
                    template.removeAttr("style");
                    var content = template.html();
                    content = content.replace("@title", element.audioTrackInfo.title);
                    content = content.replace("@author", element.audioTrackInfo.author);
                    content = content.replace("@lenght", msToTime(element.audioTrackInfo.length));
                    content = content.replace(/@url/g, element.audioTrackInfo.uri);
                    content = content.replace(/@user/g, element.user);
                    template.html(content);

                    $('#playlist_list').append(template);

                });
                $(".btn_dell_playlist").click(function () {
                    var command = {
                        command: "DELL",
                        url: $(this).attr("data_url")
                    };
                    sendCommand(command);


                });
            }
        }
        else{
            $('#playlist_list').empty();
            savedPlaylist = {};
        }
        if(loadingFlag){
            modal_loading.modal('close');
            loadingFlag = false;
        }


    }).fail(function (data) {
        if(!error){
            alert("Comunication error, please refresh.");
            error = true;
        }
        if(loadingFlag){
            modal_loading.modal('close');
            loadingFlag = false;
        }

    });

}

function getChannels(){
    $.get("api/music/getChanel?guild=" + guild, function (data) {
    }).done(function (data) {
        console.log(data);
        $('#channelForm').empty();
        data.forEach(function(element){
            var template = $('#radioTemplate').clone();
            template.removeAttr("id");
            template.removeAttr("style");
            var content = template.html();
            content = content.replace("@name", element.name);
            content = content.replace(/@id/g, element.id);
            template.html(content);

            $('#channelForm').append(template);
        });
        $('#modalChanels').modal('open');

    }).fail(function (data) {
        if(!error){
            alert("Com error, please refresh.");
            error = true;
        }

    });
}

function updateModal(data){
    $('#modal_title').text("Title: "+ data.info.audioTrackInfo.title);
    $('#modal_author').text("Author: "+ data.info.audioTrackInfo.author);
    $('#modal_lenght').text("Duration: "+ msToTime(data.info.audioTrackInfo.length));
    $('#modal_url').text("URL: "+ data.info.audioTrackInfo.uri);
    $('#modal_submit').text("Submitted by: "+ data.info.user);



}

function updateControl(data){
    $('#music_text').text(data.info.audioTrackInfo.title);
    var percent = (data.currentPos / data.info.audioTrackInfo.length) * 100;
    // console.log(percent)
    if (!$('#music_progress').hasClass("indeterminate")) {
        $('#music_progress').addClass("determinate").removeClass("indeterminate");
    }
    $('#music_progress').width(percent + "%");

    if(Cookies.get('token') != undefined){
        enableBtn(btn_play);
        enableBtn(btn_stop);
        enableBtn(btn_info);
        enableBtn(btn_add);
        enableBtn(btn_flush);
        enableBtn(btn_next);
        enableBtn(btn_disconnect);
    }
    else
    {
        disableBtn(btn_play);
        disableBtn(btn_stop);
        disableBtn(btn_info);
        disableBtn(btn_add);
        disableBtn(btn_flush);
        disableBtn(btn_next);
        disableBtn(btn_disconnect);
    }


    $('#music_img').attr("src","https://img.youtube.com/vi/"+data.info.audioTrackInfo.identifier+"/hqdefault.jpg");
    // console.log(data);
    $('#total_time').text(msToTime(data.info.audioTrackInfo.length));
    $('#current_time').text(msToTime(data.currentPos));
    updateModal(data);
}

function sendCommand(command){
    modal_loading.modal('open');
    console.log(command)
    $.ajax({
        type: "POST",
        dataType: 'json',
        contentType: 'application/json',
        url: "/api/music/command?guild=" + guild,
        data:  JSON.stringify(command),
        success: function (data) {
            console.log(data);
            loadingFlag = true;
            getCurentMusic();

        }

    }).fail(function (data) {
        console.log(data);
        alert(data.responseJSON.Message);
        modal_loading.modal('close');
        if(data.responseJSON.error === "token"){
            Cookies.remove('token');
            Cookies.remove('name');
            location.reload();
        }
    });
}

function comparePlaylist(list1, list2){
    if(list1 == null || list2 == null){
        return false;
    }

    if(list1.length != list2.length){
        return false;
    }


    for(var i = 0; i++; i < list1.length){
        if(list1[i].uri != list2[i].uri)
            return false
    }
    return true;
}

function msToTime(duration) {
    var milliseconds = parseInt((duration%1000)/100)
        , seconds = parseInt((duration/1000)%60)
        , minutes = parseInt((duration/(1000*60))%60)
        , hours = parseInt((duration/(1000*60*60))%24);

    hours = (hours < 10) ? "0" + hours : hours;
    minutes = (minutes < 10) ? "0" + minutes : minutes;
    seconds = (seconds < 10) ? "0" + seconds : seconds;
    if(hours > 0 )
        return hours + ":" + minutes + ":" + seconds;
    else
        return minutes + ":" + seconds;
}

function listeners() {
    $('#btn_play').click(function () {
        switch (state){
            case "PLAYING":
                sendCommand({ command: "PAUSE"})
                break;

            case "PAUSE":
                sendCommand({ command: "PLAY"})
                break;
            default:
                sendCommand({command: "PLAY"})
        }

    });

    $('#btn_next').click(function () {
        sendCommand({ command: "NEXT"});
    });
    $('#btn_stop').click(function () {
        sendCommand({ command: "STOP"});
    });

    $('#input_link').on("input", function () {
        if($('#input_link').val() == ""){
            disableBtn($('#btn_add_bottom'));
            disableBtn($('#btn_add_top'));
        }
        else{
            enableBtn($('#btn_add_bottom'));
            enableBtn($('#btn_add_top'));
        }
    });

    $('#modalChanels').change(function () {
        if ($('#btn_ok_channel').hasClass("disabled")) {
            $('#btn_ok_channel').removeClass("disabled");
        }
    });

    $('#flush_btn').click(function () {
        var command = {
            command: "FLUSH"
        };
        sendCommand(command);
    });

    $('#btn_add_top').click(function () {
        var command = {
            command: "ADD",
            url: $('#input_link').val(),
            playlistLimit: $('#limit_range').val(),
            onHead: true
        };
        $('#input_link').val('');
        sendCommand(command);
    });

    $('#btn_add_bottom').click(function () {

        var command = {
            command: "ADD",
            url: $('#input_link').val(),
            playlistLimit: $('#limit_range').val(),
            onHead: false
        };
        $('#input_link').val('');
        sendCommand(command);
    });

    $('#btn_ok_channel').click(function () {

        var command = {
            command: "CONNECT",
            chanelId: $('input[name=vocalRadio]:checked').val()
        };
        sendCommand(command);
    });

    $('#btn_disconnect').click(function () {
        sendCommand({command : "DISCONNECT"})
    });

    switchAutoFlow.click(function () {
        console.log(switchAutoFlow.is(':checked'))
        if(switchAutoFlow.is(':checked')){
            sendCommand({command: 'AUTOFLOWON'})
        }
        else
            sendCommand({command: 'AUTOFLOWOFF'})
    });
}

function disableBtn(btn) {
    if (!btn.hasClass("disabled")) {
        btn.addClass("disabled");
    }
}

function enableBtn(btn){
    if (btn.hasClass("disabled")) {
        btn.removeClass("disabled");
    }
}


