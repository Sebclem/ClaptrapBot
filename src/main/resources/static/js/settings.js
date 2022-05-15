var post_json = {settings: []};


$(document).ready(function () {

    $('select').formSelect();
    modal_loading = $('#modal_loading');
    modal_loading.modal({
        dismissible: false
    });

    $('#sendBtn').click(function () {

        var select = $('.collect-select');
        select.each(function () {
            var val = $(this).find("select").val();
            var id = $(this).attr("id");
            if (val != null) {
                post_json["settings"].push({"id": id, "val": val});
            }

        });


        var select_multi = $('.collect-select-multiple');
        select_multi.each(function () {
            var instance = M.FormSelect.getInstance($(this).find("select")[0]);

            var id = $(this).attr("id");
            post_json["settings"].push({"id": id, "vals": instance.getSelectedValues()});


        });

        var switch_collected = $('.collect-switch');
        switch_collected.each(function () {
            var val = $(this).is(':checked').toString();
            var id = $(this).attr("id");
            if (val != null) {
                post_json["settings"].push({"id": id, "val": val});
            }

        });

        var text = $('.collect-text');
        text.each(function () {
            var val = $(this).val();
            var id = $(this).attr("id");
            if (val != null) {
                post_json["settings"].push({"id": id, "val": val});
            }

        });

        modal_loading.modal('open');
        $.ajax({
            type: "POST",
            contentType: 'application/json',
            url: "/api/settings",
            data: JSON.stringify(post_json)

        }).done(function (data) {
            console.log("ok");
            M.toast({
                html: '<i class="small material-icons" style="margin-right: 0.3em">done</i>Save Successful ! ',
                classes: 'rounded green'
            });
            modal_loading.modal('close');
        }).fail(function (data) {
            console.log(data);
            modal_loading.modal('close');
            M.toast({
                html: '<i class="small material-icons" style="margin-right: 0.3em">report</i>Save Failed ! ',
                classes: 'rounded red'
            });
        });


    })
});