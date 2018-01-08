var savedPlaylist;
var error = false;
var state;

$(document).ready(function() {
    setInterval("getCurentMusic()",1000);
    // the "href" attribute of the modal trigger must specify the modal ID that wants to be triggered
    $('.modal').modal();
    $('.button-collapse-1').sideNav({
        menuWidth: 400, // Default is 300
        edge: 'right', // Choose the horizontal origin
        closeOnClick: false, // Closes side-nav on <a> clicks, useful for Angular/Meteor
        draggable: true // Choose whether you can drag to open on touch screens,
    });
    var height = $( window ).height();

    $('#btn_play').click(function () {
        switch (state){
            case "PLAYING":
                sendCommand(JSON.stringify({ command: "PAUSE"}))
                break;

            case "PAUSE":
                sendCommand(JSON.stringify({ command: "PLAY"}))
                break;
        }
        
    })

    $('#btn_next').click(function () {
        sendCommand(JSON.stringify({ command: "NEXT"}));
    })
    $('#btn_stop').click(function () {
        sendCommand(JSON.stringify({ command: "STOP"}));
    })

    $('.dropdown-button').dropdown({
            inDuration: 300,
            outDuration: 225,
            constrainWidth: false, // Does not change width of dropdown to that of the activator
            hover: false, // Activate on hover
            gutter: 0, // Spacing from edge
            belowOrigin: false, // Displays dropdown below the button
            alignment: 'left', // Displays dropdown with edge aligned to the left of button
            stopPropagation: false // Stops event propagation
        }
    );

    $('#input_link').on("input", function () {
       if($('#input_link').val() == ""){
           if (!$('#btn_add_bottom').hasClass("disabled")) {
               $('#btn_add_bottom').addClass("disabled");
           }
           if (!$('#btn_add_top').hasClass("disabled")) {
               $('#btn_add_top').addClass("disabled");
           }
       }
       else{
           if ($('#btn_add_bottom').hasClass("disabled")) {
               $('#btn_add_bottom').removeClass("disabled");
           }
           if ($('#btn_add_top').hasClass("disabled")) {
               $('#btn_add_top').removeClass("disabled");
           }
       }
    });

    $('#btn_add_top').click(function () {
        var command = {
            command: "ADD",
            url: $('#input_link').val(),
            playlistLimit: $('#limit_range').val(),
            onHead: true
        };
        $('#input_link').val('');
        sendCommand(JSON.stringify(command));
    })
    $('#btn_add_bottom').click(function () {

        var command = {
            command: "ADD",
            url: $('#input_link').val(),
            playlistLimit: $('#limit_range').val(),
            onHead: false
        };
        $('#input_link').val('');
        sendCommand(JSON.stringify(command));
    })

})






function getCurentMusic() {
    $.get("api/music/currentMusicInfo", function (data) {
    }).done(function (data) {

        // alert( "second success" );
        // console.log(data);
        state = data.state;
        switch (data.state) {
            case "STOP":
                $('#music_text').text("Connected on Vocal Channel");

                if (!$('#btn_info').hasClass("indeterminate")) {
                    $('#btn_info').addClass("determinate").removeClass("indeterminate");
                }
                $('#music_progress').width("0%");

                $('#btn_play').children().text("play_arrow");
                if (!$('#btn_stop').hasClass("disabled")) {
                    $('#btn_stop').addClass("disabled");
                }
                if (!$('#btn_info').hasClass("disabled")) {
                    $('#btn_info').addClass("disabled");
                }
                if ($('#add_btn').hasClass("disabled")) {
                    $('#add_btn').removeClass("disabled");
                }

                $('#music_img').attr("src","/img/no_music.jpg");

                break;

            case "PLAYING":
                $('#btn_play').children().text("pause");
                updateControl(data);

                break;

            case "PAUSE":
                $('#btn_play').children().text("play_arrow");
                updateControl(data);


                break;

            case "LOADING":
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
                if (!$('#btn_play').hasClass("disabled")) {
                    $('#btn_play').addClass("disabled");
                }
                if (!$('#btn_stop').hasClass("disabled")) {
                    $('#btn_stop').addClass("disabled");
                }
                if (!$('#btn_next').hasClass("disabled")) {
                    $('#btn_next').addClass("disabled");
                }
                if (!$('#btn_info').hasClass("disabled")) {
                    $('#btn_info').addClass("disabled");
                }
                if (!$('#add_btn').hasClass("disabled")) {
                    $('#add_btn').addClass("disabled");
                }


                $('#music_img').attr("src","/img/disconnected.png");
                break;
        }
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
    $.get("api/music/getPlaylist", function (data) {
    }).done(function (data) {
        data = data.list;
        if(data != null && data.length != 0){
            var noUpdate = comparePlaylist(data, savedPlaylist);

            if(!noUpdate){
                savedPlaylist = data;
                $('#playlist_list').empty();

                data.forEach(function(element){
                    var template = $('#playlist_template').clone();
                    template.removeAttr("id");
                    template.removeAttr("style");
                    var content = template.html();
                    content = content.replace("@title", element.title);
                    content = content.replace("@author", element.author);
                    content = content.replace("@lenght", msToTime(element.length));
                    content = content.replace(/@url/g, element.uri);
                    template.html(content);

                    $('#playlist_list').append(template);

                });
            }
        }
        else
            $('#playlist_list').empty();




    });

}

function updateModal(data){
    $('#modal_title').text("Title: "+ data.info.title);
    $('#modal_author').text("Author: "+ data.info.author);
    $('#modal_lenght').text("Duration: "+ msToTime(data.info.length));
    $('#modal_url').text("URL: "+ data.info.uri);



}

function updateControl(data){
    $('#music_text').text(data.info.title);
    var percent = (data.currentPos / data.info.length) * 100;
    // console.log(percent)
    if (!$('#btn_info').hasClass("indeterminate")) {
        $('#btn_info').addClass("determinate").removeClass("indeterminate");
    }
    $('#music_progress').width(percent + "%");

    if ($('#btn_play').hasClass("disabled")) {
        $('#btn_play').removeClass("disabled");
    }
    if ($('#btn_stop').hasClass("disabled")) {
        $('#btn_stop').removeClass("disabled");
    }
    if ($('#btn_info').hasClass("disabled")) {
        $('#btn_info').removeClass("disabled");
    }
    if ($('#add_btn').hasClass("disabled")) {
        $('#add_btn').removeClass("disabled");
    }

    if ($('#btn_next').hasClass("disabled")) {
        $('#btn_next').removeClass("disabled");
    }

    $('#music_img').attr("src","https://img.youtube.com/vi/"+data.info.identifier+"/hqdefault.jpg");
    updateModal(data);
}

function sendCommand(commandStr){
    $.ajax({
        type: "POST",
        dataType: 'json',
        contentType: 'application/json',
        url: "/api/music/command",
        data:  commandStr,
        success: function (data) {
            console.log(data);
        }

    }).fail(function (data) {
        console.log(data);
        alert(data.responseJSON.Message);
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


