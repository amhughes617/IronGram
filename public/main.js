lastPhotosData = null;

function getPhotos(photosData) {
    if (JSON.stringify(lastPhotosData) !== JSON.stringify(photosData)) {
        $("#photos").empty();
        for(var i in photosData) {
            var elem = $("<img>");
            elem.attr("src", photosData[i].fileName);
            $("#photos").append(elem);
            lastPhotosData = photosData;
        }
    }
}

function getUser() {
    $.get(
        "/user",
        function(userData) {
            if (userData.length == 0) {
                $("#login").show();
            }
            else {
                $("#upload").show();
                $.get("/photos", getPhotos);
            }
        }
    );
}
setInterval(getUser,1000);
