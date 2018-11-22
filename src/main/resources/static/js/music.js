//import * as M from "./materialize";

let savedPlaylist;
let error = false;
let state;
let disconected = false;
let modal_loading;
let btn_play;
let btn_stop;
let btn_next;
let btn_info;
let btn_disconnect_music;
let btn_flush;
let btn_add;
let switchAutoFlow;
let loadingFlag = true;
let guild;
let interval;

$(document).ready(function () {
    if (Cookies.get('guild') !== undefined) {

        guild = Cookies.get('guild');
        btn_play = $('#btn_play');
        btn_stop = $('#btn_stop');
        btn_next = $('#btn_next');
        btn_info = $('#btn_info');
        btn_disconnect_music = $('#btn_disconnect');
        btn_flush = $('#flush_btn');
        btn_add = $('#add_btn');
        switchAutoFlow = $("#autoflow");



        M.Modal.init($('#modalAdd').get(0));

        $('#modal_current_info').modal();

        $('#modalChanels').modal({
            dismissible: false // Modal can be dismissed by clicking outside of the modal
        });


        modal_loading = M.Modal.init($('#modal_loading').get(0), {dismissible: false});
        modal_loading.open();



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
        interval = setInterval("getCurentMusic()", 1000);

    }
});

function getCurentMusic() {
    $.get("api/music/currentMusicInfo?guild=" + guild, function (data) {
    }).done(function (data) {
        if (error) {
            error = false;
            M.Toast.dismissAll();
        }
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
                if (Cookies.get('token') !== undefined) {
                    disableBtn(btn_stop);
                    disableBtn(btn_info);
                    enableBtn(btn_add);
                    enableBtn(btn_flush);
                    enableBtn(btn_play);
                    enableBtn(btn_next);
                    enableBtn(btn_disconnect_music);
                }
                else {
                    disableBtn(btn_play);
                    disableBtn(btn_stop);
                    disableBtn(btn_info);
                    disableBtn(btn_add);
                    disableBtn(btn_flush);
                    disableBtn(btn_next);
                    disableBtn(btn_disconnect_music);
                }
                btn_play.children().text("play_arrow");
                $('#music_img').attr("src", "/img/no_music.jpg");
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
                disableBtn(btn_disconnect_music);

                $('#music_img').attr("src", "/img/disconnected.png");
                if (Cookies.get('token') != undefined) {
                    if (!disconected) {
                        getChannels();
                        disconected = true;
                    }
                }

                clearInterval(interval);
                break;
        }
        if (switchAutoFlow.is(':checked') != data.autoflow)
            switchAutoFlow.prop('checked', data.autoflow);
        if(data.state !== "DISCONNECTED" && data.state !== "STOP")
            getPlayList();
        else{
            if (loadingFlag) {
                modal_loading.close();
                loadingFlag = false;
            }
        }

    })
        .fail(function (data) {
            if (!error) {
                error = true;
                console.error("Connection lost, I keep trying to refresh!");
                M.toast({
                    html: " <i class=\"material-icons\" style='margin-right: 10px'>warning</i> Connection Lost!",
                    classes: 'red',
                    displayLength: 99999999
                });

            }

        })
}

function getPlayList() {
    $.get("api/music/getPlaylist?guild=" + guild, function (data) {
    }).done(function (data) {
        data = data.list;
        if (data != null && data.length != 0) {
            var noUpdate = comparePlaylist(data, savedPlaylist);
            if (!noUpdate) {
                savedPlaylist = data;
                $('#playlist_list').empty();

                data.forEach(function (element) {
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
                    $('.collapsible').collapsible();

                });
                $(".btn_dell_playlist").click(function () {
                    var command = {
                        command: "DELL",
                        url: $(this).attr("data_url")
                    };
                    sendCommand(command, true);


                });
            }
        }
        else {
            $('#playlist_list').empty();
            savedPlaylist = {};
        }
        if (loadingFlag) {
            modal_loading.close();
            loadingFlag = false;
        }


    }).fail(function (data) {
        if (!error) {
            M.toast({
                html: " <i class=\"material-icons\" style='margin-right: 10px'>warning</i> Connection Lost!",
                classes: 'red',
                displayLength: 99999999
            });
            error = true;
        }
        if (loadingFlag) {
            modal_loading.close();
            loadingFlag = false;
        }

    });

}

function getChannels() {
    $.get("api/music/getChanel?guild=" + guild, function (data) {
    }).done(function (data) {

        $('#channelForm').empty();
        data.forEach(function (element) {
            var template = $('#radioTemplate').clone();
            template.removeAttr("id");
            template.removeAttr("style");
            var content = template.html();
            content = content.replace("@name", element.name);
            content = content.replace(/@id/g, element.id);
            template.html(content);

            $('#channelForm').append(template);
        });
        $('#btn_ok_channel').addClass("disabled");
        $('#modalChanels').modal('open');

    }).fail(function (data) {
        if (!error) {
            M.toast({
                html: " <i class=\"material-icons\" style='margin-right: 10px'>warning</i> Connection Lost!",
                classes: 'red',
                displayLength: 99999999
            });

            error = true;
        }

    });
}

function updateModal(data) {
    $('#modal_title').text("Title: " + data.info.audioTrackInfo.title);
    $('#modal_author').text("Author: " + data.info.audioTrackInfo.author);
    $('#modal_lenght').text("Duration: " + msToTime(data.info.audioTrackInfo.length));
    $('#modal_url').html("<div>URL:  <a target=\"_blank\"  href=\"" + data.info.audioTrackInfo.uri + "\">" + data.info.audioTrackInfo.uri + "</a></div>");
    //
    $('#modal_submit').text("Submitted by: " + data.info.user);


}

function updateControl(data) {
    $('#music_text').text(data.info.audioTrackInfo.title);
    var percent = (data.currentPos / data.info.audioTrackInfo.length) * 100;
    if (!$('#music_progress').hasClass("indeterminate")) {
        $('#music_progress').addClass("determinate").removeClass("indeterminate");
    }
    $('#music_progress').width(percent + "%");

    if (Cookies.get('token') !== undefined) {
        enableBtn(btn_play);
        enableBtn(btn_stop);
        enableBtn(btn_info);
        enableBtn(btn_add);
        enableBtn(btn_flush);
        enableBtn(btn_next);
        enableBtn(btn_disconnect_music);
    }
    else {
        disableBtn(btn_play);
        disableBtn(btn_stop);
        disableBtn(btn_info);
        disableBtn(btn_add);
        disableBtn(btn_flush);
        disableBtn(btn_next);
        disableBtn(btn_disconnect_music);
    }


    $('#music_img').attr("src", "https://img.youtube.com/vi/" + data.info.audioTrackInfo.identifier + "/hqdefault.jpg");
    $('#total_time').text(msToTime(data.info.audioTrackInfo.length));
    $('#current_time').text(msToTime(data.currentPos));
    updateModal(data);
}

function sendCommand(command, stopRefresh) {
    if(stopRefresh){
        clearInterval(interval);
        modal_loading.open();

    }

    $.ajax({
        type: "POST",
        dataType: 'json',
        contentType: 'application/json',
        url: "/api/music/command?guild=" + guild,
        data: JSON.stringify(command),
        success: function (data) {
            loadingFlag = true;
            if(stopRefresh)
                interval = setInterval("getCurentMusic()", 1000);
            if(command.command === "ADD"){
                M.toast({
                    html: " <i class=\"material-icons\" style='margin-right: 10px'>check_circle</i> Video added to playlist!",
                    classes: 'green',
                    displayLength: 5000
                });
            }

        }

    }).fail(function (data) {
        console.log(data);
        modal_loading.close();
        if (data.responseJSON.error === "token") {
            Cookies.remove('token');
            Cookies.remove('name');
            location.reload();
        }
        else{
            M.toast({
                html: " <i class=\"material-icons\" style='margin-right: 10px'>warning</i> Command fail!",
                classes: 'red',
                displayLength: 99999999
            });
        }
    });
}

function comparePlaylist(list1, list2) {
    if (list1 == null || list2 == null) {
        return false;
    }
    if (list1.length !== list2.length) {
        return false;
    }


    for (let i = 0; i < list1.length; i++) {
        if (list1[i].audioTrackInfo.uri !== list2[i].audioTrackInfo.uri)
            return false
    }
    return true;
}



function search() {
    let input_search = $('#input_search');
    let list = $("#search_result");
    let load = $("#search_load");
    disableBtn($('#btn_search'));
    disableBtn(input_search);
    list.removeClass("scale-in");
    load.removeClass("hide");
    load.addClass("scale-in");

    $.get("/api/music/search?query=" + input_search.val(), (data) => {
        list.empty();
        data.forEach((item)=>{

            let html =
                "<li class=\"collection-item avatar\">" +
                "   <img src=\""+item["imageUrl"]+"\" alt=\"\" class=\"\">" +
                "   <a class=\"title truncate\" href='https://youtube.com/watch?v="+item["id"]+"' target=\"_blank\"><b>"+item["title"]+"</b></a>" +
                "   <p class='truncate grey-text text-darken-1'>"+item["channelTittle"]+ " &#9553 "+ item["publishedAt"].substr(0, item["publishedAt"].indexOf('T'))+" <br>" + ytTimeToTime(item["duration"])  +
                "   </p>" +
                "   <a href=\"#!\" class=\"secondary-content btn waves-effect waves-light green add-btn-list scale-transition\" id='"+item["id"]+"'><i class=\"material-icons \">add_circle_outline</i></a>" +
                "   </div>" +
                "</li>";



            list.append(html)
        });

        $(".add-btn-list").click(addListClick);
        // list.removeClass("hide");

        load.removeClass("scale-in");
        load.addClass("hide");
        list.addClass("scale-in");
        enableBtn($('#btn_search'));
        enableBtn(input_search);


    }).fail( (data)=>{
        if(data.status === 401){
            M.toast({
                html: " <i class=\"material-icons\" style='margin-right: 10px'>warning</i> Unauthorized, please re-login.",
                classes: 'red',
                displayLength: 99999999
            });
        }else{
            M.toast({
                html: " <i class=\"material-icons\" style='margin-right: 10px'>warning</i>Internal server error, please contact dev.",
                classes: 'red',
                displayLength: 99999999
            });

        }
        list.empty();
        load.removeClass("scale-in");
        load.addClass("hide");
        enableBtn($('#btn_search'));
        enableBtn(input_search);
    });

}


function addListClick(event){
    let button;
    if(event.target.nodeName === "I"){
        button = event.target.parentNode;
    }
    else
        button = event.target;
    button.classList.add("scale-out");

    let command = {
        command: "ADD",
        url: button.id,
        playlistLimit: $('#limit_range').val(),
        onHead: !$('#bottom').is(':checked')
    };
    sendCommand(command, false);
}

function ytTimeToTime(duration) {
    let hours;
    let minutes;
    let seconds;
    if(duration === "PT0S")
        return "&#x1F534 LIVE";
    if(duration.includes("H"))
        hours = parseInt(duration.match(/\d*H/)[0].replace("H",""), 10);
    else
        hours = 0;

    if(duration.includes("M"))
        minutes = parseInt(duration.match(/\d*M/)[0].replace("M",""), 10);
    else
        minutes = 0;

    if(duration.includes("S"))
        seconds = parseInt(duration.match(/\d*S/)[0].replace("S",""), 10);
    else
        seconds = 0;

    hours = (hours < 10) ? "0" + hours : hours;
    minutes = (minutes < 10) ? "0" + minutes : minutes;
    seconds = (seconds < 10) ? "0" + seconds : seconds;
    if (hours > 0)
        return hours + ":" + minutes + ":" + seconds;
    else
        return minutes + ":" + seconds;
}




function msToTime(duration) {
    var milliseconds = parseInt((duration % 1000) / 100)
        , seconds = parseInt((duration / 1000) % 60)
        , minutes = parseInt((duration / (1000 * 60)) % 60)
        , hours = parseInt((duration / (1000 * 60 * 60)) % 24);

    hours = (hours < 10) ? "0" + hours : hours;
    minutes = (minutes < 10) ? "0" + minutes : minutes;
    seconds = (seconds < 10) ? "0" + seconds : seconds;
    if (hours > 0)
        return hours + ":" + minutes + ":" + seconds;
    else
        return minutes + ":" + seconds;
}

function listeners() {


    $('#btn_play').click(function () {
        switch (state) {
            case "PLAYING":
                sendCommand({command: "PAUSE"}, true);
                break;

            case "PAUSE":
                sendCommand({command: "PLAY"}, true);
                break;
            default:
                sendCommand({command: "PLAY"},true);
        }

    });

    $('#btn_search').click(search);

    $("form").submit(function(e) {
        e.preventDefault();
        search();
    });

    $('#btn_next').click(function () {
        sendCommand({command: "NEXT"},true);
    });
    $('#btn_stop').click(function () {
        sendCommand({command: "STOP"}, true);
    });


    $('#input_search').on("input", function () {
        if ($('#input_search').val() == "") {
            disableBtn($('#btn_search'));
        }
        else {
            enableBtn($('#btn_search'));
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
        sendCommand(command, true);
    });

    $('#btn_ok_channel').click(function () {

        var command = {
            command: "CONNECT",
            chanelId: $('input[name=vocalRadio]:checked').val()
        };
        sendCommand(command, true);
    });

    $('#btn_disconnect').click(function () {
        sendCommand({command: "DISCONNECT"}, true)
    });

    switchAutoFlow.click(function () {
        if (switchAutoFlow.is(':checked')) {
            sendCommand({command: 'AUTOFLOWON'}, false)
        }
        else
            sendCommand({command: 'AUTOFLOWOFF'}, false)
    });
}

function disableBtn(btn) {
    if (!btn.hasClass("disabled")) {
        btn.addClass("disabled");
    }
}

function enableBtn(btn) {
    if (btn.hasClass("disabled")) {
        btn.removeClass("disabled");
    }
}


