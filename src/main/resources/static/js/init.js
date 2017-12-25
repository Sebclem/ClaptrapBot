var savedPlaylist;
var error = false;

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

})


function updateModal(data){
    $('#modal_title').text("Title: "+ data.info.title);
    $('#modal_author').text("Author: "+ data.info.author);
    $('#modal_lenght').text("Duration: "+ msToTime(data.info.length));
    $('#modal_url').text("URL: "+ data.info.uri);



}



function getCurentMusic() {
    $.get("api/music/currentMusicInfo", function (data) {
    }).done(function (data) {

        // alert( "second success" );
        // console.log(data);
        switch (data.state) {
            case "STOP":
                $('#music_text').text("No Music");

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
        }
        getPlayList();
    })
    .fail(function (data) {
        if(!error){
            alert("error");
            error = true;
        }

    })
}


function getPlayList() {
    $.get("api/music/getPlaylist", function (data) {
    }).done(function (data) {
        data = data.list;
        if(data.length != 0){
            var noUpdate = comparePlaylist(data, savedPlaylist);

            if(!noUpdate){
                savedPlaylist = data;
                $('#playlist_list').empty();

                data.forEach(function(element){
                    var template = $('#playlist_template').clone();
                    template.removeAttr("id");
                    template.removeAttr("style");
                    var content = template.html();
                    console.log(content);
                    content = content.replace("@title", element.title);
                    content = content.replace("@author", element.author);
                    content = content.replace("@lenght", msToTime(element.length));
                    content = content.replace("@url", element.uri)
                    template.html(content);

                    $('#playlist_list').append(template);

                });
            }
        }



    });

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


function comparePlaylist(list1, list2){
    if(list1 == null || list2 == null){
        console.log(list1);
        console.log(list2);
        console.log("False From null")
        return false;
    }

    if(list1.length != list2.length){
        console.log("False from length");
        return false;
    }


    for(var i = 0; i++; i < list1.length){
        if(list1[i].uri != list2[i].uri)
            console.log("false from compare")
            return false
    }
    return true;
}

function updateControl(data){
    $('#music_text').text(data.info.title);
    var percent = (data.currentPos / data.info.length) * 100;
    // console.log(percent)
    if (!$('#btn_info').hasClass("indeterminate")) {
        $('#btn_info').addClass("determinate").removeClass("indeterminate");
    }
    $('#music_progress').width(percent + "%");


    if ($('#btn_stop').hasClass("disabled")) {
        $('#btn_stop').removeClass("disabled");
    }
    if ($('#btn_info').hasClass("disabled")) {
        $('#btn_info').removeClass("disabled");
    }

    $('#music_img').attr("src","http://img.youtube.com/vi/"+data.info.identifier+"/hqdefault.jpg");
    updateModal(data);
}
