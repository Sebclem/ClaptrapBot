$(document).ready(function () {
    setInterval("loop()",1000);
});

function loop() {
    $.ajax({
        type: "GET",
        url: "/api/isReady",
        success: function (data) {
            console.log("Ready");
            debugger;
            location.reload();
        }

    }).fail(function (data) {
        console.log("Not ready");
    });
}